package com.cxi.entities.client;

import com.cxi.CxiFirstMod;
import com.cxi.entities.custom.SuperZombieEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @author 常袭
 * @version 1.0
 * @description: TODO
 * @date 2023/10/31 23:04
 */
public class SuperZombieEntityRenderer extends GeoEntityRenderer<SuperZombieEntity> {

    public SuperZombieEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager,new SuperZombieEntityModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public void render(SuperZombieEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public Identifier getTexture(SuperZombieEntity entity) {
        return new Identifier(CxiFirstMod.MOD_ID, "textures/entities/super_zombie/super_zombie.png");
    }

}
