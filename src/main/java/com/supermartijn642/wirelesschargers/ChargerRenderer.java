package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer extends TileEntityRenderer<ChargerBlockEntity> {

    @Override
    public void render(ChargerBlockEntity entity, double x, double y, double z, float partialTicks, int combinedLight){
        IBakedModel model = ClientUtils.getMinecraft().getModelManager().getModel(entity.type.modelType.ringModel);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.05 * Math.sin((entity.renderingTickCount + partialTicks) % 100 / 100d * 2 * Math.PI), z + 0.5);
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

        if(entity.isAreaHighlighted())
            renderAreaHighlight(entity);

        GlStateManager.popMatrix();
    }

    public static void renderAreaHighlight(ChargerBlockEntity entity){
        AxisAlignedBB area = entity.getOperatingArea().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.05);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.matrixMode(5889);
        GlStateManager.disableTexture();
        RenderHelper.turnOff();
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuilder();
        vertexConsumer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        float minX = (float)area.minX, maxX = (float)area.maxX;
        float minY = (float)area.minY, maxY = (float)area.maxY;
        float minZ = (float)area.minZ, maxZ = (float)area.maxZ;

        Random random = new Random(entity.getBlockPos().hashCode());

        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float alpha = 0.2f;

        vertexConsumer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();


        vertexConsumer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        vertexConsumer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        vertexConsumer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        tessellator.end();

        GlStateManager.matrixMode(5888);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}
