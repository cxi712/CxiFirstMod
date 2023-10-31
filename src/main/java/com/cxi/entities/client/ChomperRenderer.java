package com.cxi.entities.client;

import com.cxi.CxiFirstMod;
import com.cxi.entities.custom.ChomperEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @author 常袭
 * @version 1.0
 * @description: TODO
 * @date 2023/10/31 17:48
 */
public class ChomperRenderer extends GeoEntityRenderer<ChomperEntity> {

    public ChomperRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChomperModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public Identifier getTextureLocation(ChomperEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "textures/entities/chomper/chomper.png");
    }

    @Override
    public void render(ChomperEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
