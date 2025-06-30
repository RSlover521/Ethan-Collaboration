package com.rslover521.client;


import com.b1n_ry.yigd.block.entity.GraveBlockEntity;
import com.mojang.authlib.GameProfile;
import com.yarn_util.SimpleProfile;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.math.Direction;

public class GraveSkullRenderer implements BlockEntityRenderer<GraveBlockEntity>{
	
	private final BlockEntityRendererFactory.Context context;
	private final EntityModelLoader modelLoader = new EntityModelLoader();
	
	public GraveSkullRenderer(BlockEntityRendererFactory.Context context) {
		this.context = context;
	}
	
	@Override
	public void render(GraveBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if(entity.getComponent() == null) return;
		
		ProfileComponent ownerProfile = entity.getComponent().getOwner();
		if(ownerProfile == null) return;
		
		GameProfile profile = ownerProfile.gameProfile();
		
		if(profile == null || profile.getName() == null) return;
		
		matrices.push();
		matrices.translate(0.5, 1.0, 0.5);
		
		Direction drection = Direction.NORTH;
		float yaw = 180.0F;
		float animationProgress = 0.0F;
		
		SkullBlock.SkullType skullType = SkullBlock.Type.PLAYER;
		
		SkullBlockEntityModel model = SkullBlockEntityRenderer.getModels(modelLoader).get(skullType);
		RenderLayer layer = SkullBlockEntityRenderer.getRenderLayer(skullType, ownerProfile);
		
		SkullBlockEntityRenderer.renderSkull(drection, yaw, animationProgress, matrices, vertexConsumers, light, model, layer);
		matrices.pop();
	}
	

}
