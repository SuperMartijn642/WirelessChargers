package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.Random;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class ChargerItemStackBlockEntityRenderer extends ItemStackTileEntityRenderer {

    public static final ChargerItemStackBlockEntityRenderer INSTANCE = new ChargerItemStackBlockEntityRenderer();

    private ChargerItemStackBlockEntityRenderer(){
    }

    @Override
    public void renderByItem(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay){
        IBakedModel model = ClientUtils.getMinecraft().getItemRenderer().getItemModelShaper().getItemModel(stack);
        renderDefaultItem(stack, matrixStack, bufferSource, combinedLight, combinedOverlay, model);

        ChargerBlockEntity entity = ((ChargerBlock)((BlockItem)stack.getItem()).getBlock()).type.createTileEntity();
        entity.readData(stack.getTag() == null ? new CompoundNBT() : stack.getTag().getCompound("tileData"));

        TileEntityRendererDispatcher.instance.renderItem(entity, matrixStack, bufferSource, combinedLight, combinedOverlay);
    }

    /**
     * Adapted from {@link ItemRenderer#render(ItemStack, ItemCameraTransforms.TransformType, boolean, MatrixStack, IRenderTypeBuffer, int, int, IBakedModel)}
     */
    private static void renderDefaultItem(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay, IBakedModel model){
        matrixStack.pushPose();

        RenderType rendertype = RenderTypeLookup.getRenderType(itemStack);
        IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBuffer(renderTypeBuffer, rendertype, true, itemStack.hasFoil());

        renderModelLists(model, itemStack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);

        matrixStack.popPose();
    }

    /**
     * Adapted from {@link ItemRenderer#renderModelLists(IBakedModel, ItemStack, int, int, MatrixStack, IVertexBuilder)}
     */
    private static void renderModelLists(IBakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, MatrixStack matrixStack, IVertexBuilder vertexConsumer){
        ItemRenderer renderer = ClientUtils.getMinecraft().getItemRenderer();

        Random random = new Random();
        for(Direction direction : Direction.values()){
            random.setSeed(42L);
            renderer.renderQuadList(matrixStack, vertexConsumer, model.getQuads(null, direction, random), stack, combinedLight, combinedOverlay);
        }

        random.setSeed(42L);
        renderer.renderQuadList(matrixStack, vertexConsumer, model.getQuads(null, null, random), stack, combinedLight, combinedOverlay);
    }
}
