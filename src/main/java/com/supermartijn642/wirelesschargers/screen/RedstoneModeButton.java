package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class RedstoneModeButton extends AbstractButtonWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("wirelesschargers", "textures/screen/redstone_mode_buttons.png");

    private final Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode;

    public RedstoneModeButton(int x, int y, BlockPos entityPos, Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode){
        super(x, y, 20, 20, () -> WirelessChargers.CHANNEL.sendToServer(new CycleRedstoneModePacket(entityPos)));
        this.redstoneMode = redstoneMode;
    }

    @Override
    public ITextComponent getNarrationMessage(){
        Holder<ITextComponent> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY){
        ScreenUtils.bindTexture(BUTTONS);
        ScreenUtils.drawTexture(poseStack, this.x, this.y, this.width, this.height, this.redstoneMode.get().index / 3f, (this.isFocused() ? 1 : 0) / 3f, 1 / 3f, 1 / 3f);
    }

    @Override
    protected void getTooltips(Consumer<ITextComponent> tooltips){
        ITextComponent value = TextComponents.translation("wirelesschargers.screen.redstone_" + this.redstoneMode.get().name().toLowerCase(Locale.ROOT)).color(TextFormatting.GOLD).get();
        tooltips.accept(TextComponents.translation("wirelesschargers.screen.redstone", value).color(TextFormatting.GRAY).get());
    }
}
