package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
        GlStateManager.translate(x + 0.5, y + 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), z + 0.5);
        GlStateManager.rotate((float)Math.toDegrees((entity.renderingRotation + entity.renderingRotationSpeed * partialTicks) / 3), 0, 1, 0);
        GlStateManager.translate(-0.5, 0, -0.5);

        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModelBrightnessColor(
            model, alpha, 1, 1, 1
        );

        GlStateManager.translate(0, entity.type.modelType.ringYOffset, 0);

        ClientUtils.getBlockRenderer().getBlockModelRenderer().renderModelBrightnessColor(
            model, alpha, 1, 1, 1
        );

        if(entity.isAreaHighlighted())
            renderAreaHighlight(entity);

        GlStateManager.popMatrix();
    }

    public static void renderAreaHighlight(ChargerBlockEntity entity){
        AxisAlignedBB area = entity.getOperatingArea().offset(-entity.getPos().getX(), -entity.getPos().getY(), -entity.getPos().getZ()).grow(0.05);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.matrixMode(5889);
        GlStateManager.disableTexture2D();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();
        vertexConsumer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        float minX = (float)area.minX, maxX = (float)area.maxX;
        float minY = (float)area.minY, maxY = (float)area.maxY;
        float minZ = (float)area.minZ, maxZ = (float)area.maxZ;

        Random random = new Random(entity.getPos().hashCode());

        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float alpha = 0.2f;

        vertexConsumer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        GlStateManager.matrixMode(5888);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}
