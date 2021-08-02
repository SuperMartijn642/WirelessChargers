package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.IHoverTextWidget;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import com.supermartijn642.wirelesschargers.WirelessChargers;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class RedstoneModeButton extends AbstractButtonWidget implements IHoverTextWidget {

    private static final ResourceLocation BUTTONS = new ResourceLocation("wirelesschargers", "textures/screen/redstone_mode_buttons.png");

    private final Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode;

    public RedstoneModeButton(int x, int y, BlockPos tilePos, Supplier<ChargerBlockEntity.RedstoneMode> redstoneMode){
        super(x, y, 20, 20, () -> WirelessChargers.CHANNEL.sendToServer(new CycleRedstoneModePacket(tilePos)));
        this.redstoneMode = redstoneMode;
    }

    @Override
    protected Component getNarrationMessage(){
        return this.getHoverText();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        ScreenUtils.bindTexture(BUTTONS);
        ScreenUtils.drawTexture(matrixStack, this.x, this.y, this.width, this.height, this.redstoneMode.get().index / 3f, (this.active ? this.hovered ? 1 : 0 : 2) / 3f, 1 / 3f, 1 / 3f);
    }

    @Override
    public Component getHoverText(){
        Component value = TextComponents.translation("wirelesschargers.screen.redstone_" + this.redstoneMode.get().name().toLowerCase(Locale.ROOT)).color(ChatFormatting.GOLD).get();
        return TextComponents.translation("wirelesschargers.screen.redstone", value).color(ChatFormatting.GRAY).get();
    }
}
