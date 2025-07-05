package com.rslover521.custom_grave_data;

import com.rslover521.grave_danger_bedrock_port.GraveDangerBedrockPort;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class GraveBlockRegistery {
	public static final Block GRAVE_BLOCK = new BedrockGraveBlock(
			FabricBlockSettings.copyOf(Blocks.STONE)
			.sounds(BlockSoundGroup.STONE)
			.strength(1.5f, 6.0f)
			.pistonBehavior(PistonBehavior.BLOCK));
	
	public static BlockEntityType<BedrockGraveBlockEntity> BEDROCK_GRAVE_BLOCK_ENTITY;
	
	public static void register() {
		Registry.register(Registries.BLOCK, Identifier.of(GraveDangerBedrockPort.MOD_ID, "grave_block"), GRAVE_BLOCK);
	}
}
