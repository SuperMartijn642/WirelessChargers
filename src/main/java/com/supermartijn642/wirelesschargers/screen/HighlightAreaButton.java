package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.IHoverTextWidget;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class HighlightAreaButton extends AbstractButtonWidget implements IHoverTextWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("wirelesschargers", "textures/screen/highlight_area_buttons.png");

    private final Supplier<Boolean> highlightArea;

    public HighlightAreaButton(int x, int y, BlockPos tilePos, Supplier<Boolean> highlightArea){
        super(x, y - 3, 24, 24, () -> WirelessChargers.CHANNEL.sendToServer(new ToggleHighlightAreaPacket(tilePos)));
        this.highlightArea = highlightArea;
    }

    @Override
    protected Component getNarrationMessage(){
        return this.getHoverText();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        ScreenUtils.bindTexture(BUTTONS);
        ScreenUtils.drawTexture(matrixStack, this.x, this.y, this.width, this.height, this.highlightArea.get() ? 0 : 1 / 2f, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 1 / 2f, 1 / 3f);
    }

    @Override
    public Component getHoverText(){
        Component value = this.highlightArea.get() ? TextComponents.string("True").color(ChatFormatting.GREEN).get() : TextComponents.string("False").color(ChatFormatting.RED).get();
        return TextComponents.translation("wirelesschargers.screen.highlight_area", value).color(ChatFormatting.GRAY).get();
    }
}
