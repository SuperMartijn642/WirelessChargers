package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BlockEntityBaseWidget;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * Created 7/30/2021 by SuperMartijn642
 */
public class ChargerScreen extends BlockEntityBaseWidget<ChargerBlockEntity> {

    private final Component title;
    private final BlockPos entityPos;

    public ChargerScreen(Component title, Level entityLevel, BlockPos entityPos){
        super(0, 0, 66, 72, entityLevel, entityPos);
        this.title = title;
        this.entityPos = entityPos;
    }

    @Override
    public Component getNarrationMessage(ChargerBlockEntity object){
        return this.title;
    }

    @Override
    protected void addWidgets(ChargerBlockEntity entity){
        this.addWidget(new EnergyBarWidget(10, 10, 20, 52, this::getEnergy, this::getCapacity));
        this.addWidget(new HighlightAreaButton(36, 11, this.entityPos, this::isAreaHighlighted));
        this.addWidget(new RedstoneModeButton(36, 41, this.entityPos, this::getRedstoneMode));
    }

    @Override
    protected void renderBackground(PoseStack poseStack, int mouseX, int mouseY, ChargerBlockEntity object){
        ScreenUtils.drawScreenBackground(poseStack, this.x, this.y, this.width, this.height);
        super.renderBackground(poseStack, mouseX, mouseY, object);
    }

    private int getEnergy(){
        return this.validateObjectOrClose() ? this.object.getEnergyStored() : 0;
    }

    private int getCapacity(){
        return this.validateObjectOrClose() ? this.object.getMaxEnergyStored() : 1;
    }

    private boolean isAreaHighlighted(){
        return this.validateObjectOrClose() && this.object.isAreaHighlighted();
    }

    private ChargerBlockEntity.RedstoneMode getRedstoneMode(){
        return this.validateObjectOrClose() ? this.object.getRedstoneMode() : ChargerBlockEntity.RedstoneMode.DISABLED;
    }
}
