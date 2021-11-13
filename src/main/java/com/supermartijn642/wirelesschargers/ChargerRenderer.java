package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer extends TileEntitySpecialRenderer<ChargerBlockEntity> {

    @Override
    public void render(ChargerBlockEntity entity, double x, double y, double z, float partialTicks, int combinedOverlay, float alpha){
        IBakedModel model = ClientUtils.getBlockRenderer().getModelForState(entity.type.getBlock().getDefaultState().withProperty(ChargerBlock.RING, true));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), 0.5);
        GlStateManager.rotate((float)Math.toDegrees((entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3), 0, 1, 0);
        GlStateManager.translate(-0.5, 0, -0.5);

        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModelBrightnessColor(
            model, alpha, 1, 1, 1
        );

        GlStateManager.translate(0, entity.type.modelType.ringYOffset, 0);

        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModelBrightnessColor(
            model, alpha, 1, 1, 1
        );

        GlStateManager.popMatrix();

        if(entity.isAreaHighlighted()){
            AxisAlignedBB area = entity.getOperatingArea().offset(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ()).grow(0.05);

            Random random = new Random(entity.getPos().hashCode());

            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            alpha *= 0.2f;

            RenderUtils.renderBox(area, red, green, blue);
            RenderUtils.renderBoxSides(area, red, green, blue, alpha);
        }

        GlStateManager.popMatrix();
    }
}
