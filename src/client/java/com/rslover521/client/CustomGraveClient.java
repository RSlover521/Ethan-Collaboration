package com.rslover521.client;

import org.spongepowered.asm.mixin.Mixin;

import com.b1n_ry.yigd.Yigd;
import com.b1n_ry.yigd.block.entity.GraveBlockEntity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class CustomGraveClient implements ClientModInitializer{
	@SuppressWarnings("deprecation")
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(Yigd.GRAVE_BLOCK_ENTITY, context -> new GraveSkullRenderer(context));
	}

}
