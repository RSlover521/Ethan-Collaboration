package com.rslover521.custom_grave_data;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import com.b1n_ry.yigd.block.entity.GraveBlockEntity;
import com.b1n_ry.yigd.components.GraveComponent;
import com.b1n_ry.yigd.data.DeathInfoManager;
import com.b1n_ry.yigd.data.GraveStatus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yarn_util.SimpleProfile;

import java.util.UUID;

public class BedrockGraveBlockEntity extends GraveBlockEntity{
	
	public static final MapCodec<BedrockGraveBlockEntity> CODEC = RecordCodecBuilder.mapCodec(instance ->
    instance.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(be -> be.getPos()),
        BlockState.CODEC.fieldOf("block_state").forGetter(be -> be.getCachedState())
    ).apply(instance, BedrockGraveBlockEntity::new)
);
	

	public MapCodec<? extends BlockEntity> getCodec() {
		return CODEC;
	}
	

	public BedrockGraveBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
	}
	
	public void tryClaimGrave(ServerPlayerEntity player, ServerWorld world) {
		GraveComponent component = getGraveComponent();
		if (component == null && this.getGraveId() != null) {
			component = DeathInfoManager.INSTANCE.getGrave(this.getGraveId()).orElse(null);
		}
		if (component != null && component.getStatus() == GraveStatus.UNCLAIMED) {
			component.claim(player, world, this.getCachedState(), this.getPos(), player.getMainHandStack());
		}
	}
	public boolean isBedrockGrave() {
		try {
			Class<?> floodgateApi = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
			Object api = floodgateApi.getMethod("getInstance").invoke(null);
			UUID graveOwnerId = getGraveOwnerUuid();
			return graveOwnerId != null && (boolean) floodgateApi.getMethod("isFloodgatePlayer", UUID.class).invoke(api, graveOwnerId);
		} catch (Exception e) {
			com.rslover521.grave_danger_bedrock_port.GraveDangerBedrockPort.LOGGER.info("Cannot determine if one grave block is Bedrock or not.");
			return false;
		}
	}
	// Util method: Get the GraveComponent from this grave
	private GraveComponent getGraveComponent() {
		GraveComponent component = this.getComponent();
		if (component != null) return component;
		
		if (this.getGraveId() != null) {
			return DeathInfoManager.INSTANCE.getGrave(this.getGraveId()).orElse(null);
		}
		return null;
	}
	
	//Util method: Get UUID of grave owner from SimpleProfile
	private UUID getGraveOwnerUuid() {
		GraveComponent component = getGraveComponent();
		if (component == null) return null;
		
		if (component instanceof CustomGraveComponent custom) {
			return custom.getSimpleProfile().getUuid();
		}
		return null;
	}
}
