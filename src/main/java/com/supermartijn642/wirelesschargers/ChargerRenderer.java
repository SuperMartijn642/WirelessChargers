package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer implements BlockEntityRenderer<ChargerBlockEntity> {

    public static final RenderType AREA_HIGHLIGHT_RENDER_TYPE = RenderType.create("wireless_charger_area_highlight", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, RenderTypeExtension.getAreaHighlightCompositeState());

    @Override
    public void render(ChargerBlockEntity entity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        BakedModel model = ClientUtils.getMinecraft().getModelManager().getModel(entity.type.modelType.ringModel);

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), 0.5);
        matrixStack.mulPose(new Quaternion(0, (entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3, 0, false));
        matrixStack.translate(-0.5, 0, -0.5);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            matrixStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE
        );

        matrixStack.translate(0, entity.type.modelType.ringYOffset, 0);

        ClientUtils.getBlockRenderer().getModelRenderer().renderModel(
            matrixStack.last(), bufferSource.getBuffer(RenderType.solid()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE
        );

        matrixStack.popPose();

        if(entity.isAreaHighlighted())
            renderAreaHighlight(entity, matrixStack, bufferSource);
    }

    public static void renderAreaHighlight(ChargerBlockEntity entity, PoseStack matrixStack, MultiBufferSource bufferSource){
        AABB area = entity.getOperatingArea().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.05);

        Matrix4f matrix = matrixStack.last().pose();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(AREA_HIGHLIGHT_RENDER_TYPE);

        float minX = (float)area.minX, maxX = (float)area.maxX;
        float minY = (float)area.minY, maxY = (float)area.maxY;
        float minZ = (float)area.minZ, maxZ = (float)area.maxZ;

        Random random = new Random(entity.getBlockPos().hashCode());

        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float alpha = 0.2f;

        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
    }

    // Just need this to access protected fields
    private static class RenderTypeExtension extends RenderType {

        public RenderTypeExtension(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_){
            super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
        }

        public static RenderType.CompositeState getAreaHighlightCompositeState(){
            return RenderType.CompositeState.builder()
                .setShaderState(POSITION_COLOR_SHADER)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTextureState(NO_TEXTURE)
                .setCullState(NO_CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false);
        }
    }
}
