package com.cxi.entities.client;

import com.cxi.CxiFirstMod;
import com.cxi.entities.custom.ChomperEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/**
 * @author 常袭
 * @version 1.0
 * @description: TODO
 * @date 2023/10/31 17:43
 */
public class ChomperModel extends GeoModel<ChomperEntity> {
    @Override
    public Identifier getModelResource(ChomperEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "geo/chomper.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChomperEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "textures/entities/chomper/chomper.png");
    }

    @Override
    public Identifier getAnimationResource(ChomperEntity animatable) {
        return new Identifier(CxiFirstMod.MOD_ID, "animations/chomper.animation.json");
    }
}
