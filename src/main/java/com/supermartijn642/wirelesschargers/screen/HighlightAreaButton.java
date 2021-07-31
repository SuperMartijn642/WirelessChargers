package com.supermartijn642.wirelesschargers.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.IHoverTextWidget;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

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
    protected ITextComponent getNarrationMessage(){
        return this.getHoverText();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        ScreenUtils.bindTexture(BUTTONS);
        ScreenUtils.drawTexture(this.x, this.y, this.width, this.height, this.highlightArea.get() ? 0 : 1 / 2f, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 1 / 2f, 1 / 3f);
    }

    @Override
    public ITextComponent getHoverText(){
        ITextComponent value = this.highlightArea.get() ? TextComponents.string("True").color(TextFormatting.GREEN).get() : TextComponents.string("False").color(TextFormatting.RED).get();
        return TextComponents.translation("wirelesschargers.screen.highlight_area", value).color(TextFormatting.GRAY).get();
    }
}
