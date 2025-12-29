package com.supermartijn642.wirelesschargers.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class HighlightAreaButton extends AbstractButtonWidget {

    public static final Identifier BUTTONS = Identifier.fromNamespaceAndPath("wirelesschargers", "screen/highlight_area_buttons");

    private final Supplier<Boolean> highlightArea;

    public HighlightAreaButton(int x, int y, BlockPos entityPos, Supplier<Boolean> highlightArea){
        super(x, y - 3, 24, 24, () -> WirelessChargers.CHANNEL.sendToServer(new ToggleHighlightAreaPacket(entityPos)));
        this.highlightArea = highlightArea;
    }

    @Override
    public Component getNarrationMessage(){
        Holder<Component> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    public void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        graphics.submitSprite(BUTTONS, this.x, this.y, this.width, this.height, p -> p.uv(this.highlightArea.get() ? 0 : 1 / 2f, (this.isFocused() ? 1 : 0) / 3f, 1 / 2f, 1 / 3f));
    }

    @Override
    protected void getTooltips(Consumer<Component> tooltips){
        Component value = this.highlightArea.get() ? TextComponents.string("True").color(ChatFormatting.GREEN).get() : TextComponents.string("False").color(ChatFormatting.RED).get();
        tooltips.accept(TextComponents.translation("wirelesschargers.screen.highlight_area", value).color(ChatFormatting.GRAY).get());
    }
}
