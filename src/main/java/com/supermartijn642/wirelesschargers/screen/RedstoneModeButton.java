package com.supermartijn642.wirelesschargers.screen;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class RedstoneModeButton extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = ResourceLocation.fromNamespaceAndPath("wirelesschargers", "textures/screen/redstone_mode_buttons.png");

    private final Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode;

    public RedstoneModeButton(int x, int y, BlockPos entityPos, Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode){
        super(x, y, 20, 20, () -> WirelessChargers.CHANNEL.sendToServer(new CycleRedstoneModePacket(entityPos)));
        this.redstoneMode = redstoneMode;
    }

    @Override
    public Component getNarrationMessage(){
        Holder<Component> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    public void render(WidgetRenderContext context, int mouseX, int mouseY){
        ScreenUtils.drawTexture(BUTTONS, context.poseStack(), this.x, this.y, this.width, this.height, this.redstoneMode.get().index / 3f, (this.isFocused() ? 1 : 0) / 3f, 1 / 3f, 1 / 3f);
    }

    @Override
    protected void getTooltips(Consumer<Component> tooltips){
        Component value = TextComponents.translation("wirelesschargers.screen.redstone_" + this.redstoneMode.get().name().toLowerCase(Locale.ROOT)).color(ChatFormatting.GOLD).get();
        tooltips.accept(TextComponents.translation("wirelesschargers.screen.redstone", value).color(ChatFormatting.GRAY).get());
    }
}
