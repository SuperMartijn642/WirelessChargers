package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraftforge.client.model.BakedModelWrapper;

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
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat){
        super.handlePerspective(cameraTransformType, mat);
        return this;
    }
}
