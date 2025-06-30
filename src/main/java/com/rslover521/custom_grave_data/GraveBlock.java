package com.rslover521.custom_grave_data;

import com.b1n_ry.yigd.Yigd;
import com.b1n_ry.yigd.block.entity.GraveBlockEntity;
import com.b1n_ry.yigd.compat.CompatComponent;
import com.b1n_ry.yigd.components.ExpComponent;
import com.b1n_ry.yigd.components.GraveComponent;
import com.b1n_ry.yigd.components.InventoryComponent;
import com.b1n_ry.yigd.config.YigdConfig;
import com.b1n_ry.yigd.data.DeathInfoManager;
import com.b1n_ry.yigd.data.GraveItem;
import com.b1n_ry.yigd.data.GraveStatus;
import com.b1n_ry.yigd.data.TimePoint;
import com.b1n_ry.yigd.util.DropRule;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.serialization.MapCodec;
import com.yarn_util.SimpleProfile;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Vec3d;


import net.minecraft.block.entity.BlockEntity;       

import net.minecraft.item.ItemStack;         // for .hasTag(), .getTag()

import net.minecraft.util.Identifier;         // may be needed



import java.util.*;

public abstract class GraveBlock extends BlockWithEntity implements Waterloggable {
    private static VoxelShape SHAPE_EAST = VoxelShapes.fullCube();
    private static VoxelShape SHAPE_WEST = VoxelShapes.fullCube();
    private static VoxelShape SHAPE_SOUTH = VoxelShapes.fullCube();
    private static VoxelShape SHAPE_NORTH = VoxelShapes.fullCube();
	private @Nullable SimpleProfile graveSkullGameProfile;
	private List<GraveItem> items = new ArrayList<>();
	private Map<String, CompatComponent<?>> modInventoryItems = new HashMap<>();
	
	
    public GraveBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction dir = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, dir)
                .with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return YigdConfig.getConfig().graveRendering.useCustomFeatureRenderer
                ? BlockRenderType.INVISIBLE
                : BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case SOUTH -> SHAPE_SOUTH;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    	if (!world.isClient && placer instanceof ServerPlayerEntity serverPlayer) {
    		BlockEntity be = world.getBlockEntity(pos);
    		if (be instanceof GraveBlockEntity grave) {
    			if (grave.getComponent() == null) {
    				SimpleProfile profile = new SimpleProfile(serverPlayer.getUuid(), serverPlayer.getName().getString());
    				InventoryComponent inv = new InventoryComponent(serverPlayer);
    				ExpComponent exp = new ExpComponent(serverPlayer);
    				
    				UUID killerId = serverPlayer.getAttacker() != null ? serverPlayer.getAttacker().getUuid() : null;
    				
    				GraveComponent component = new CustomGraveComponent(
    					profile,
    					inv, 
    					exp, 
    					(ServerWorld) world, 
    					pos, 
    					Text.literal(""), 
    					killerId
    				);
    				grave.setComponent(component);
    			}
    			if(stack.getName() != null && !stack.getName().getString().isEmpty() && !stack.getName().equals(stack.getItem().getName())) {
    				grave.setGraveText(stack.getName());
    				grave.markDirty();
    			}
    		}
    	}
    	super.onPlaced(world, pos, state, placer, stack);
    }
    
    public ActionResult onUseWithoutItem(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

        boolean isFloodgate = false;
        try {
            Class<?> apiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Object instance = apiClass.getMethod("getInstance").invoke(null);
            isFloodgate = (boolean) apiClass.getMethod("isFloodgatePlayer", UUID.class).invoke(instance, serverPlayer.getUuid());
        } catch (Exception ignored) {}

        if (!world.isClient && world.getBlockEntity(pos) instanceof GraveBlockEntity grave) {
            GraveComponent component = grave.getComponent();
            if (component == null) {
                UUID id = grave.getGraveId();
                if (id != null) component = DeathInfoManager.INSTANCE.getGrave(id).orElse(null);
                if (component == null) return interactWithNonPlayerGrave(grave, state, world, pos, player, player.getActiveHand(), hit);
            }

            if (YigdConfig.getConfig().graveConfig.persistentGraves.enabled &&
                    component.getStatus() == GraveStatus.CLAIMED &&
                    player.getActiveHand() == Hand.MAIN_HAND) {
                MutableText message = component.getDeathMessage().copy();

                TimePoint time = component.getCreationTime();
                if (YigdConfig.getConfig().graveConfig.persistentGraves.showDeathDay)
                    message.append(Text.translatable("text.yigd.message.on_day", time.getDay()));
                if (YigdConfig.getConfig().graveConfig.persistentGraves.showDeathIrlTime)
                    message.append(Text.translatable("text.yigd.message.irl_time",
                            time.getMonthName(),
                            time.getDate(),
                            time.getYear(),
                            time.getHour(YigdConfig.getConfig().graveConfig.persistentGraves.useAmPm),
                            time.getMinute(),
                            time.getTimePostfix(YigdConfig.getConfig().graveConfig.persistentGraves.useAmPm)));

                player.sendMessage(message);
                return ActionResult.SUCCESS;
            }

            if (YigdConfig.getConfig().graveConfig.retrieveMethods.onClick) {
                if (isFloodgate) {
                    dropGraveItems((ServerWorld) world, pos, serverPlayer, grave);
                    return ActionResult.SUCCESS;
                } else {
                    return component.claim(serverPlayer, (ServerWorld) world, grave.getPreviousState(), pos, serverPlayer.getMainHandStack());
                }
            }
        }

        return ActionResult.FAIL;
    }
    
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return onUseWithoutItem(state, world, pos, player, hit);
        }
        return ActionResult.PASS;
    }

    private ActionResult interactWithNonPlayerGrave(GraveBlockEntity grave, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    	if (player.isSneaking()) return ActionResult.FAIL;

    	ItemStack stack = player.getStackInHand(hand);
    	if (!stack.isOf(Items.PLAYER_HEAD)) return ActionResult.PASS;

    	// Serialize the stack to NBT
    	NbtCompound nbt = serializeItemStack(stack);
    	if (!nbt.contains("SkullOwner", NbtElement.COMPOUND_TYPE)) {
    		return ActionResult.PASS;
    	}

    	NbtCompound skullOwnerNbt = nbt.getCompound("SkullOwner");
    	GameProfile profile = readGameProfileFromNbt(skullOwnerNbt);
    	if (profile == null) return ActionResult.PASS;

    	// setGraveSkull(profile);
    	grave.markDirty();
    	world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
    	if (!player.getAbilities().creativeMode) {
    		stack.decrement(1);
    	}
    	return ActionResult.SUCCESS;
    }


    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            if (YigdConfig.getConfig().graveConfig.retrieveMethods.onStand ||
                (YigdConfig.getConfig().graveConfig.retrieveMethods.onSneak && player.isSneaking())) {
                if (world.getBlockEntity(pos) instanceof GraveBlockEntity grave) {
                    GraveComponent component = grave.getComponent();
                    if (component == null && grave.getGraveId() != null)
                        component = DeathInfoManager.INSTANCE.getGrave(grave.getGraveId()).orElse(null);
                    if (component != null && component.getStatus() != GraveStatus.CLAIMED) {
                        component.claim(player, (ServerWorld) world, grave.getPreviousState(), pos, player.getMainHandStack());
                    }
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    private void dropGraveItems(ServerWorld world, BlockPos pos, ServerPlayerEntity player, GraveBlockEntity grave) {
        GraveComponent component = grave.getComponent();
        if (component != null && component.getStatus() != GraveStatus.CLAIMED) {
        	InventoryComponent invComp = component.getInventoryComponent();
        	invComp.dropGraveItems(world, Vec3d.ofCenter(pos));
            component.setStatus(GraveStatus.CLAIMED);
            player.sendMessage(Text.literal("Your grave items were dropped!"));
        }
    }
    // Static shape loading (unchanged from your version)
    // Include your `reloadShapeFromJson` and static block here as-is
    
    public static GameProfile readGameProfileFromNbt(NbtCompound tag) {
        if (tag == null) return null;

        UUID id = null;
        if (tag.contains("Id")) {
            try {
                id = UUID.fromString(tag.getString("Id"));
            } catch (IllegalArgumentException e) {
                // Invalid UUID string
                id = null;
            }
        }

        String name = tag.contains("Name") ? tag.getString("Name") : null;

        if (id == null && (name == null || name.isEmpty())) return null;

        return new GameProfile(id, name);
    }
    
    
     public static NbtCompound serializeItemStack(ItemStack stack) {
    	 var dynamicOps = NbtOps.INSTANCE;
    	 var optional = ItemStack.CODEC.encodeStart(dynamicOps, stack)
    			 .resultOrPartial(err -> {
                    // optional: log or ignore errors
                });
            return optional
                .map(tag -> (NbtCompound) tag)
                .orElse(new NbtCompound());
            
     }
     
     public void setGraveSkull(@Nullable SimpleProfile profile) {
    	    if (profile == null) return; 
    	    else {
    	        this.graveSkullGameProfile = profile; // your new field you add to the class
    	    }
    	}
     
     public List<GraveItem> getVanillaItems() {
    	 return this.items;
     }

     public Map<String, CompatComponent<?>> getModInventories() {
    	 return this.modInventoryItems;
     }
}
    



