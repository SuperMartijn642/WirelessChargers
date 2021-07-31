package com.supermartijn642.wirelesschargers.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.gui.TileEntityBaseScreen;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/**
 * Created 7/30/2021 by SuperMartijn642
 */
public class ChargerScreen extends TileEntityBaseScreen<ChargerBlockEntity> {

    public ChargerScreen(ITextComponent title, BlockPos tilePos){
        super(title, tilePos);
    }

    @Override
    protected float sizeX(@Nonnull ChargerBlockEntity entity){
        return 66;
    }

    @Override
    protected float sizeY(@Nonnull ChargerBlockEntity entity){
        return 72;
    }

    @Override
    protected void addWidgets(@Nonnull ChargerBlockEntity entity){
        this.addWidget(new EnergyBarWidget(10, 10, 20, 52, this::getEnergy, this::getCapacity));
        this.addWidget(new HighlightAreaButton(36, 11, this.tilePos, this::isAreaHighlighted));
        this.addWidget(new RedstoneModeButton(36, 41, this.tilePos, this::getRedstoneMode));
    }

    @Override
    protected void render(MatrixStack matrixStack, int mouseX, int mouseY, @Nonnull ChargerBlockEntity entity){
        this.drawScreenBackground(matrixStack);
    }

    private int getEnergy(){
        ChargerBlockEntity entity = this.getObjectOrClose();
        return entity == null ? 0 : entity.getEnergyStored();
    }

    private int getCapacity(){
        ChargerBlockEntity entity = this.getObjectOrClose();
        return entity == null ? 1 : entity.getMaxEnergyStored();
    }

    private boolean isAreaHighlighted(){
        ChargerBlockEntity entity = this.getObjectOrClose();
        return entity != null && entity.isAreaHighlighted();
    }

    private ChargerBlockEntity.RedstoneMode getRedstoneMode(){
        ChargerBlockEntity entity = this.getObjectOrClose();
        return entity == null ? ChargerBlockEntity.RedstoneMode.DISABLED : entity.getRedstoneMode();
    }
}
