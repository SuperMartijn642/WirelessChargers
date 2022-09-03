package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer implements CustomBlockEntityRenderer<ChargerBlockEntity> {

    @Override
    public void render(ChargerBlockEntity entity, float partialTicks, int combinedOverlay){
        IBakedModel model = ClientUtils.getMinecraft().getModelManager().getModel(entity.type.modelType.ringModel);

        GlStateManager.pushMatrix();
        GlStateManager.translated(0.5, 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), 0.5);
        Quaternion quaternion = new Quaternion(0, (entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3, 0, false);
        GlStateManager.rotated(quaternion.i(), quaternion.j(), quaternion.k(), quaternion.r());
        GlStateManager.translated(-0.5, 0, -0.5);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            model, 1, 1, 1, 1
        );

        GlStateManager.translated(0, entity.type.modelType.ringYOffset, 0);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            model, 1, 1, 1, 1
        );

        GlStateManager.popMatrix();

        if(entity.isAreaHighlighted()){
            AxisAlignedBB area = entity.getOperatingArea().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.05);

            Random random = new Random(entity.getBlockPos().hashCode());

            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.2f;

            RenderUtils.renderBox(area, red, green, blue, true);
            RenderUtils.renderBoxSides(area, red, green, blue, alpha, true);
        }
    }
}
