package com.rslover521.custom_grave_data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.*;

public class BedrockGraveBlock extends Block {
  
    public BedrockGraveBlock(Settings settings) {
        super(settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BedrockGraveBlockEntity(pos, state);
    }
    
    public boolean hasBlockEntity(BlockState state) {
    	return true;
    }
}
