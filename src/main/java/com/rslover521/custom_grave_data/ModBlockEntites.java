package com.rslover521.custom_grave_data;

import net.minecraft.block.entity.BlockEntityType;

import com.b1n_ry.yigd.block.GraveBlock;
import com.rslover521.grave_danger_bedrock_port.GraveDangerBedrockPort;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntites {
	public static BlockEntityType<BedrockGraveBlockEntity> BEDROCK_GRAVE;
	
	public static void registerAll() {
		BEDROCK_GRAVE = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of (GraveDangerBedrockPort.MOD_ID, "bedrock_grave"), 
				BlockEntityType.Builder.create(BedrockGraveBlockEntity::new, GraveBlockRegistery.GRAVE_BLOCK).build(null)
		);
	}
}
