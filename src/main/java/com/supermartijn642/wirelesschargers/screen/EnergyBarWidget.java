package com.supermartijn642.wirelesschargers.screen;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.AbstractButtonWidget;
import com.supermartijn642.core.util.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 11/17/2020 by SuperMartijn642
 */
public class EnergyBarWidget extends AbstractButtonWidget {

    private static final ResourceLocation BARS = new ResourceLocation("wirelesschargers", "textures/screen/energy_bars.png");

    private final Supplier<Integer> energy, capacity;

    public EnergyBarWidget(int x, int y, int width, int height, Supplier<Integer> energy, Supplier<Integer> capacity){
        super(x, y, width, height, () -> EnergyFormat.cycleEnergyType(!Screen.hasShiftDown()));
        this.energy = energy;
        this.capacity = capacity;
    }

    @Override
    public Component getNarrationMessage(){
        Holder<Component> tooltip = new Holder<>();
        this.getTooltips(tooltip::set);
        return tooltip.get();
    }

    @Override
    public void render(WidgetRenderContext context, int mouseX, int mouseY){
        ScreenUtils.bindTexture(BARS);
        ScreenUtils.drawTexture(context.poseStack(), this.x, this.y, this.width, this.height, this.isFocused() ? 1 / 11f : 0, 0, 1 / 11f, 1);
        int energy = this.energy.get();
        int capacity = this.capacity.get();
        float percentage = capacity == 0 ? 1 : Math.max(Math.min(energy / (float)capacity, 1), 0);
        if(percentage != 0)
            ScreenUtils.drawTexture(context.poseStack(), this.x, this.y + this.height * (1 - percentage), this.width, this.height * percentage, 3 / 11f, 1 - percentage, 1 / 11f, percentage);
    }

    @Override
    protected void getTooltips(Consumer<Component> tooltips){
        Component energy = TextComponents.string(EnergyFormat.formatEnergy(this.energy.get())).color(ChatFormatting.GOLD).get();
        Component capacity = TextComponents.string(EnergyFormat.formatEnergy(this.capacity.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(ChatFormatting.GRAY).get();
        tooltips.accept(TextComponents.translation("wirelesschargers.screen.stored_energy", energy, capacity).color(ChatFormatting.GRAY).get());
    }
}
