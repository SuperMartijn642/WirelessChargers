package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class DummyCuriosHandler implements ICuriosHandler {

    private static final IItemHandlerModifiable EMPTY = new EmptyHandler();

    @Override
    public IItemHandlerModifiable getCuriosStacks(PlayerEntity player){
        return EMPTY;
    }
}
