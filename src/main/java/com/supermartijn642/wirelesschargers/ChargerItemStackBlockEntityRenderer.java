package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class ChargerItemStackBlockEntityRenderer extends TileEntityItemStackRenderer {

    public static final ChargerItemStackBlockEntityRenderer INSTANCE = new ChargerItemStackBlockEntityRenderer();

    private ChargerItemStackBlockEntityRenderer(){
    }

    @Override
    public void renderByItem(ItemStack stack){
        IBakedModel model = ClientUtils.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        renderDefaultItem(stack, model);

        ChargerBlockEntity entity = ((ChargerBlock)((ItemBlock)stack.getItem()).getBlock()).type.createTileEntity();
        entity.readData(stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound().getCompoundTag("tileData"));

        TileEntityRendererDispatcher.instance.render(entity, 0, 0, 0, 0);
    }

    /**
     * Adapted from {@link RenderItem#renderItem(ItemStack, IBakedModel)}
     */
    private static void renderDefaultItem(ItemStack itemStack, IBakedModel model){
        renderModel(model, itemStack, -1);
        if(itemStack.hasEffect())
            renderEffect(model);
    }

    /**
     * Adapted from {@link RenderItem#renderModel(IBakedModel, int, ItemStack)}
     */
    private static void renderModel(IBakedModel model, ItemStack stack, int combinedLight){
        RenderItem renderItem = ClientUtils.getMinecraft().getRenderItem();

        if(net.minecraftforge.common.ForgeModContainer.allowEmissiveItems){
            net.minecraftforge.client.ForgeHooksClient.renderLitItem(renderItem, model, combinedLight, stack);
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for(EnumFacing direction : EnumFacing.values())
            renderItem.renderQuads(bufferbuilder, model.getQuads(null, direction, 0L), combinedLight, stack);

        renderItem.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), combinedLight, stack);
        tessellator.draw();
    }

    private static void renderEffect(IBakedModel model){

        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        ClientUtils.getTextureManager().bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, ItemStack.EMPTY, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, ItemStack.EMPTY, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        ClientUtils.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }
}
