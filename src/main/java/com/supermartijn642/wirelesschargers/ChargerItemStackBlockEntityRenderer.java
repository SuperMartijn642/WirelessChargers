package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

    public static ChargerItemStackBlockEntityRenderer getInstance(){
        return INSTANCE;
    }

    private ChargerItemStackBlockEntityRenderer(){
    }

    @Override
    public void renderByItem(ItemStack stack){
        IBakedModel model = ClientUtils.getMinecraft().getItemRenderer().getItemModelShaper().getItemModel(stack);
        renderDefaultItem(stack, model);

        ChargerBlockEntity entity = ((ChargerBlock)((BlockItem)stack.getItem()).getBlock()).type.createTileEntity();
        entity.readData(stack.getTag() == null ? new CompoundNBT() : stack.getTag().getCompound("tileData"));

        TileEntityRendererDispatcher.instance.renderItem(entity);
    }

    /**
     * Adapted from {@link ItemRenderer#render(ItemStack, IBakedModel)}
     */
    private static void renderDefaultItem(ItemStack itemStack, IBakedModel model){
        GlStateManager.pushMatrix();

        renderModelLists(model, itemStack, -1);
        if(itemStack.hasFoil()){
            ItemRenderer.renderFoilLayer(ClientUtils.getTextureManager(), () -> {
                renderModelLists(model, ItemStack.EMPTY, -8372020);
            }, 8);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Adapted from {@link ItemRenderer#renderModelLists(IBakedModel, int, ItemStack)}
     */
    private static void renderModelLists(IBakedModel model, ItemStack stack, int combinedLight){
        ItemRenderer itemRenderer = ClientUtils.getItemRenderer();

        if(net.minecraftforge.common.ForgeConfig.CLIENT.allowEmissiveItems.get()){
            net.minecraftforge.client.ForgeHooksClient.renderLitItem(itemRenderer, model, combinedLight, stack);
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.BLOCK_NORMALS);
        Random random = new Random();

        for(Direction direction : Direction.values()){
            random.setSeed(42L);
            itemRenderer.renderQuadList(bufferbuilder, model.getQuads(null, direction, random), combinedLight, stack);
        }

        random.setSeed(42L);
        itemRenderer.renderQuadList(bufferbuilder, model.getQuads(null, null, random), combinedLight, stack);
        tessellator.end();
    }
}
