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

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class ChargerItemStackBlockEntityRenderer extends ItemStackTileEntityRenderer {

    public static final ChargerItemStackBlockEntityRenderer INSTANCE = new ChargerItemStackBlockEntityRenderer();

    public static ChargerItemStackBlockEntityRenderer getInstance(){
        return INSTANCE;
    }

    private ChargerItemStackBlockEntityRenderer(){
    }

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType cameraTransform, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay){
        IBakedModel model = ClientUtils.getMinecraft().getItemRenderer().getItemModelShaper().getItemModel(stack);
        renderDefaultItem(stack, matrixStack, cameraTransform, bufferSource, combinedLight, combinedOverlay, model);

        ChargerBlockEntity entity = ((ChargerBlock)((BlockItem)stack.getItem()).getBlock()).type.createTileEntity();
        entity.readData(stack.getTag() == null ? new CompoundNBT() : stack.getTag().getCompound("tileData"));

        TileEntityRendererDispatcher.instance.renderItem(entity, matrixStack, bufferSource, combinedLight, combinedOverlay);
    }

    private static void renderDefaultItem(ItemStack itemStack, MatrixStack matrixStack, ItemCameraTransforms.TransformType cameraTransform, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, IBakedModel model){
        ItemRenderer renderer = ClientUtils.getMinecraft().getItemRenderer();

        matrixStack.pushPose();

        if(model.isLayered()){
            net.minecraftforge.client.ForgeHooksClient.drawItemLayered(renderer, model, itemStack, matrixStack, bufferSource, combinedLight, combinedOverlay, true);
        }else{
            RenderType rendertype = RenderTypeLookup.getRenderType(itemStack, true);
            IVertexBuilder ivertexbuilder;

            ivertexbuilder = ItemRenderer.getFoilBuffer(bufferSource, rendertype, true, itemStack.hasFoil());

            renderer.renderModelLists(model, itemStack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
        }

        matrixStack.popPose();
    }
}
