package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class ChargerItemStackBlockEntityRenderer extends BlockEntityWithoutLevelRenderer {

    private final BlockEntityRenderDispatcher renderDispatcher;

    public ChargerItemStackBlockEntityRenderer(BlockEntityRenderDispatcher renderDispatcher){
        super(renderDispatcher, new EntityModelSet());
        this.renderDispatcher = renderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType cameraTransform, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        BakedModel model = ClientUtils.getMinecraft().getItemRenderer().getItemModelShaper().getItemModel(stack);
        renderDefaultItem(stack, matrixStack, cameraTransform, bufferSource, combinedLight, combinedOverlay, model);

        ChargerBlockEntity entity = ((ChargerBlock)((BlockItem)stack.getItem()).getBlock()).type.createTileEntity(BlockPos.ZERO, ((BlockItem)stack.getItem()).getBlock().defaultBlockState());
        entity.readData(stack.getTag() == null ? new CompoundTag() : stack.getTag().getCompound("tileData"));

        this.renderDispatcher.renderItem(entity, matrixStack, bufferSource, combinedLight, combinedOverlay);
    }

    private static void renderDefaultItem(ItemStack itemStack, PoseStack matrixStack, ItemTransforms.TransformType cameraTransform, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model){
        ItemRenderer renderer = ClientUtils.getMinecraft().getItemRenderer();

        matrixStack.pushPose();

        if(model.isLayered()){
            net.minecraftforge.client.ForgeHooksClient.drawItemLayered(renderer, model, itemStack, matrixStack, bufferSource, combinedLight, combinedOverlay, true);
        }else{
            RenderType rendertype = ItemBlockRenderTypes.getRenderType(itemStack, true);
            VertexConsumer ivertexbuilder;

            ivertexbuilder = ItemRenderer.getFoilBuffer(bufferSource, rendertype, true, itemStack.hasFoil());

            renderer.renderModelLists(model, itemStack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
        }

        matrixStack.popPose();
    }
}
