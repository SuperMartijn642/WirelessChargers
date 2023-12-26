package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

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
        poseStack.mulPose(new Quaternionf().setAngleAxis((entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3, 0, 1, 0));
        poseStack.translate(-0.5, 0, -0.5);

        RandomSource randomsource = RandomSource.create();
        randomsource.setSeed(42);
        for(RenderType renderType : model.getRenderTypes(entity.getBlockState(), randomsource, ModelData.EMPTY)){
            ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), bufferSource.getBuffer(renderType), entity.getBlockState(), model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, renderType
            );
        }

        poseStack.translate(0, entity.type.modelType.ringYOffset, 0);

        randomsource.setSeed(42);
        for(RenderType renderType : model.getRenderTypes(entity.getBlockState(), randomsource, ModelData.EMPTY)){
            ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), bufferSource.getBuffer(renderType), entity.getBlockState(), model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, renderType
            );
        }

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
