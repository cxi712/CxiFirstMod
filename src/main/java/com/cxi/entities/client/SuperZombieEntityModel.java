package com.cxi.entities.client;

import com.cxi.CxiFirstMod;
import com.cxi.entities.custom.SuperZombieEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/**
 * @author 常袭
 * @version 1.0
 * @description: TODO
 * @date 2023/10/31 23:06
 */
public class SuperZombieEntityModel extends GeoModel<SuperZombieEntity> {

    @Override
    public Identifier getModelResource(SuperZombieEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "geo/super_zombie.geo.json");
    }

    @Override
    public Identifier getTextureResource(SuperZombieEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "textures/entities/super_zombie/super_zombie.png");
    }

    @Override
    public Identifier getAnimationResource(SuperZombieEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "animations/super_zombie.animation.json");
    }

}
