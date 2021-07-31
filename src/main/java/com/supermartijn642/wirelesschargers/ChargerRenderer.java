package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer extends TileEntityRenderer<ChargerBlockEntity> {

    public static final RenderType AREA_HIGHLIGHT_RENDER_TYPE = RenderType.create("wireless_charger_area_highlight", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderTypeExtension.getAreaHighlightCompositeState());

    public ChargerRenderer(TileEntityRendererDispatcher rendererDispatcher){
        super(rendererDispatcher);
    }

    @Override
    public void render(ChargerBlockEntity entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay){
        IBakedModel model = ClientUtils.getMinecraft().getModelManager().getModel(entity.type.modelType.ringModel);

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

    public static void renderAreaHighlight(ChargerBlockEntity entity, MatrixStack matrixStack, IRenderTypeBuffer bufferSource){
        AxisAlignedBB area = entity.getOperatingArea().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.05);

        Matrix4f matrix = matrixStack.last().pose();
        IVertexBuilder vertexConsumer = bufferSource.getBuffer(AREA_HIGHLIGHT_RENDER_TYPE);

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

        public RenderTypeExtension(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_){
            super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        }

        public static RenderType.State getAreaHighlightCompositeState(){
            return RenderType.State.builder()
                .setAlphaState(DEFAULT_ALPHA)
                .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
                .setTextureState(NO_TEXTURE)
                .setCullState(NO_CULL)
                .setDepthTestState(RenderState.LEQUAL_DEPTH_TEST)
                .createCompositeState(false);
        }
    }
}
