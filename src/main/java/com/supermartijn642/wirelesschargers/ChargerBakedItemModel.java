package com.supermartijn642.wirelesschargers;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

/**
 * Created 3/18/2021 by SuperMartijn642
 */
public class ChargerBakedItemModel extends BakedModelWrapper<IBakedModel> {

    public ChargerBakedItemModel(IBakedModel model){
        super(model);
    }

    @Override
    public boolean isCustomRenderer(){
        return true;
    }

    @Override
    public Pair<? extends IBakedModel,Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType){
        Pair<? extends IBakedModel,Matrix4f> pair = super.handlePerspective(cameraTransformType);
        return Pair.of(this, pair.getRight());
    }
}
