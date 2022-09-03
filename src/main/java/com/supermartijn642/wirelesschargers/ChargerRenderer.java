package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer implements CustomBlockEntityRenderer<ChargerBlockEntity> {

    @Override
    public void render(ChargerBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        BakedModel model = ClientUtils.getMinecraft().getModelManager().getModel(entity.type.modelType.ringModel);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), 0.5);
        poseStack.mulPose(new Quaternion(0, (entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3, 0, false));
        poseStack.translate(-0.5, 0, -0.5);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE
        );

        poseStack.translate(0, entity.type.modelType.ringYOffset, 0);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE
        );

        poseStack.popPose();

        if(entity.isAreaHighlighted()){
            AABB area = entity.getOperatingArea().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.05);

            Random random = new Random(entity.getBlockPos().hashCode());

            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.2f;

            RenderUtils.renderBox(poseStack, area, red, green, blue, true);
            RenderUtils.renderBoxSides(poseStack, area, red, green, blue, alpha, true);
        }
    }
}
