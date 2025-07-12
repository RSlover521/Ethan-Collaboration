package com.rslover521.grave_danger_bedrock_port;

import java.util.Map;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.floodgate.api.FloodgateApi;

import com.b1n_ry.yigd.block.GraveBlock;
import com.b1n_ry.yigd.block.entity.GraveBlockEntity;
import com.b1n_ry.yigd.components.GraveComponent;
import com.rslover521.custom_grave_data.BedrockGraveBlock;
import com.rslover521.custom_grave_data.BedrockGraveBlockEntity;
import com.rslover521.custom_grave_data.ModBlocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.api.block.custom.CustomBlockState;
import org.geysermc.geyser.api.block.custom.component.CustomBlockComponents;
import org.geysermc.geyser.api.block.custom.component.GeometryComponent;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;

import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraveDangerBedrockPort implements ModInitializer, EventRegistrar {
	
	// This code runs as soon as Minecraft is in a mod-load-ready state.
	// However, some things (like resources) may still be uninitialized.
	// Proceed with mild caution.
	
	public static final String MOD_ID = "grave_danger_bedrock_port";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	
	// These lines of code is used Registeration of grave block (Bedrock)
	  public static final Identifier BLOCK_ID = Identifier.of(MOD_ID, "bedrock_grave_block");
	    public static final Identifier BE_ID = Identifier.of(MOD_ID, "bedrock_grave_block_entity");

	    @SuppressWarnings("deprecation")
		public static final Block BEDROCK_GRAVE_BLOCK = new BedrockGraveBlock(FabricBlockSettings.copyOf(Blocks.STONE));
	    public static BlockEntityType<BedrockGraveBlockEntity> BEDROCK_GRAVE_BLOCK_ENTITY;

	    @Override
	    public void onInitialize() {
	        // Register the block and item
	        Registry.register(Registries.BLOCK, BLOCK_ID, BEDROCK_GRAVE_BLOCK);
	        Registry.register(Registries.ITEM, BLOCK_ID, new BlockItem(BEDROCK_GRAVE_BLOCK, new Item.Settings()));

	        // Register block entity
	        BEDROCK_GRAVE_BLOCK_ENTITY = Registry.register(
	            Registries.BLOCK_ENTITY_TYPE,
	            BE_ID,
	            FabricBlockEntityTypeBuilder.create(BedrockGraveBlockEntity::new, BEDROCK_GRAVE_BLOCK).build()
	        );

	        System.out.println("Registered BedrockGraveBlock + BlockEntity!");
        
	    // Geyser Registration
	    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
	        if (FabricLoader.getInstance().isModLoaded("geyser-fabric")) {
	            try {
	                Class<?> geyserApiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
	                Object apiInstance = geyserApiClass.getMethod("api").invoke(null);
	                Object eventBus = apiInstance.getClass().getMethod("eventBus").invoke(apiInstance);
	                try {
	                    // Try register(Object listener, Identifier id) method
	                    eventBus.getClass()
	                        .getMethod("register", Object.class, Class.forName("net.minecraft.util.Identifier"))
	                        .invoke(eventBus, this, BE_ID);
	                    LOGGER.info("Geyser API listener registered with identifier on server starting.");
	                } catch (NoSuchMethodException nsme) {
	                    // Fallback to register(Object listener)
	                    eventBus.getClass()
	                        .getMethod("register", Object.class)
	                        .invoke(eventBus, this);
	                    LOGGER.info("Geyser API listener registered without identifier on server starting.");
	                }
	            } catch (ClassNotFoundException e) {
	                LOGGER.warn("Geyser API class not found, skipping listener registration on server start.");
	            } catch (Exception e) {
	                LOGGER.warn("Failed to register with Geyser API on server start", e);
	            }
	        } else {
	            LOGGER.info("Geyser API NOT available - skipping Bedrock block setup.");
	        }
	    });
	    
		System.out.println("GraveBlockEntity superclass: " + GraveBlockEntity.class.getSuperclass().getName());
		System.out.println("My BlockEntity class: " + net.minecraft.block.entity.BlockEntity.class.getName());
		
		LOGGER.info("Grave Danger Bedrock Port Loaded");
		LOGGER.info("Hello Fabric world!");
		
		LOGGER.info("My BlockEntity class: {}", BlockEntity.class.getName());
		
		
		// DEBUGGING
		Registry<Block> blockRegistry = Registries.BLOCK;
		for (Map.Entry<RegistryKey<Block>, Block> entry : blockRegistry.getEntrySet()) {
		    if (blockRegistry.getId(entry.getValue()).getNamespace().equals("minecraft") && blockRegistry.getId(entry.getValue()).getPath().equals("[unregistered]")) {
		        System.out.println("Broken block: " + entry.getValue().getClass().getName());
		    }
		}
		
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (isBedrockPlayer(oldPlayer)) {
		    LOGGER.info("Bedrock player died: {} ", oldPlayer.getName());
		        }
		    });
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
		    BlockPos pos = hitResult.getBlockPos();
		    BlockState state = world.getBlockState(pos);

		    if (!world.isClient()) {
		        // Check if the block is a grave
		        if (state.getBlock() instanceof GraveBlock) {
		            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUuid())) {
		            	BlockEntity be = world.getBlockEntity(pos);
		            	if (be instanceof GraveBlockEntity grave) {
		            		GraveComponent graveComponent = grave.getComponent();
		            		if(graveComponent != null) {
		            			if (player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
		            				graveComponent.claim(serverPlayer, serverWorld, grave.getPreviousState(), pos, player.getStackInHand(hand));
		            				LOGGER.info("Bedrock player interacted with a grave!");
		            				return ActionResult.SUCCESS;
		            			}
		            		}
		            	}
		            }
		        }
		    }
		    return ActionResult.PASS;
		});
	}
	public boolean isBedrockPlayer(ServerPlayerEntity player) {
		return FloodgateApi.getInstance().isFloodgatePlayer(player.getUuid());
	}
	
	@Subscribe
	public void onGeyserDefineCustomBlocks(GeyserDefineCustomBlocksEvent event) {
	    LOGGER.info("Registering custom grave block for Bedrock");

	    CustomBlockComponents components = CustomBlockComponents.builder()
	        .geometry(GeometryComponent.builder().identifier("geometry.grave").build())
	        .build();

	    CustomBlockData graveBlock = CustomBlockData.builder()
	        .name("bedrock_grave_block")
	        .components(components)
	        .build();

	    try {
	        event.register(graveBlock);
	        LOGGER.info("Successfully registered grave_block for Bedrock clients.");
	    } catch (Exception e) {
	        LOGGER.warn("Failed to register grave_block for Bedrock players.", e);
	    }
	}
}

	
	
