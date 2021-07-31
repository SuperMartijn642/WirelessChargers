package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.AbstractButtonWidget;
import com.supermartijn642.core.gui.widget.IHoverTextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Supplier;

/**
 * Created 11/17/2020 by SuperMartijn642
 */
public class EnergyBarWidget extends AbstractButtonWidget implements IHoverTextWidget {

    private static final ResourceLocation BARS = new ResourceLocation("wirelesschargers", "textures/screen/energy_bars.png");

    private final Supplier<Integer> energy, capacity;

    public EnergyBarWidget(int x, int y, int width, int height, Supplier<Integer> energy, Supplier<Integer> capacity){
        super(x, y, width, height, () -> EnergyFormat.cycleEnergyType(!Screen.hasShiftDown()));
        this.energy = energy;
        this.capacity = capacity;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        Minecraft.getInstance().getTextureManager().bind(BARS);
        GlStateManager.enableAlphaTest();
        ScreenUtils.drawTexture(this.x, this.y, this.width, this.height, this.isHovered() ? 1 / 11f : 0, 0, 1 / 11f, 1);
        int energy = this.energy.get();
        int capacity = this.capacity.get();
        float percentage = capacity == 0 ? 1 : Math.max(Math.min(energy / (float)capacity, 1), 0);
        if(percentage != 0)
            ScreenUtils.drawTexture(this.x, this.y + this.height * (1 - percentage), this.width, this.height * percentage, 3 / 11f, 1 - percentage, 1 / 11f, percentage);
    }

    @Override
    public ITextComponent getHoverText(){
        ITextComponent energy = TextComponents.string(EnergyFormat.formatEnergy(this.energy.get())).color(TextFormatting.GOLD).get();
        ITextComponent capacity = TextComponents.string(EnergyFormat.formatEnergy(this.capacity.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(TextFormatting.GRAY).get();
        return TextComponents.translation("wirelesschargers.screen.stored_energy", energy, capacity).color(TextFormatting.GRAY).get();
    }

    @Override
    protected ITextComponent getNarrationMessage(){
        return this.getHoverText();
    }
}
