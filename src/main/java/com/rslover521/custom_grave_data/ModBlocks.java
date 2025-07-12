package com.rslover521.custom_grave_data;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;

public class ModBlocks {
	@SuppressWarnings("deprecation")
	public static final Block BEDROCK_GRAVE_BLOCK = new BedrockGraveBlock(FabricBlockSettings.copyOf(Blocks.STONE));
	
	@SuppressWarnings("deprecation")
	public static final BlockEntityType<BedrockGraveBlockEntity> BEDROCK_GRAVE_BLOCK_ENTITY =
	        FabricBlockEntityTypeBuilder.create(
	                BedrockGraveBlockEntity::new,
	                BEDROCK_GRAVE_BLOCK
	            ).build();
}

