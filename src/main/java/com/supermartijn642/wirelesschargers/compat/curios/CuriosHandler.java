package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

import javax.annotation.Nonnull;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class CuriosHandler implements ICuriosHandler {

    private static final IItemHandlerModifiable EMPTY = new EmptyHandler();

    @Override
    public IItemHandlerModifiable getCuriosStacks(PlayerEntity player){
        LazyOptional<IItemHandlerModifiable> optional = CuriosAPI.getCuriosHandler(player).map(
            handler -> new IItemHandlerModifiable() {
                @Override
                public void setStackInSlot(int slot, @Nonnull ItemStack stack){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            stackHandler.setStackInSlot(slot, stack);
                        else
                            slot -= stackHandler.getSlots();
                    }
                }

                @Override
                public int getSlots(){
                    int slots = 0;
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values())
                        slots += stackHandler.getSlots();
                    return slots;
                }

                @Nonnull
                @Override
                public ItemStack getStackInSlot(int slot){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            return stackHandler.getStackInSlot(slot);
                        else
                            slot -= stackHandler.getSlots();
                    }
                    return ItemStack.EMPTY;
                }

                @Nonnull
                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            return stackHandler.insertItem(slot, stack, simulate);
                        else
                            slot -= stackHandler.getSlots();
                    }
                    return stack;
                }

                @Nonnull
                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            return stackHandler.extractItem(slot, amount, simulate);
                        else
                            slot -= stackHandler.getSlots();
                    }
                    return ItemStack.EMPTY;
                }

                @Override
                public int getSlotLimit(int slot){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            return stackHandler.getSlotLimit(slot);
                        else
                            slot -= stackHandler.getSlots();
                    }
                    return 0;
                }

                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack){
                    for(CurioStackHandler stackHandler : handler.getCurioMap().values()){
                        if(slot < stackHandler.getSlots())
                            return stackHandler.isItemValid(slot, stack);
                        else
                            slot -= stackHandler.getSlots();
                    }
                    return false;
                }
            }
        );
        return optional.orElse(EMPTY);
    }
}
