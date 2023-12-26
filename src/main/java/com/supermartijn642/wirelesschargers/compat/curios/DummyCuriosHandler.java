package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyHandler;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class DummyCuriosHandler implements ICuriosHandler {

    private static final IItemHandlerModifiable EMPTY = new EmptyHandler();

    @Override
    public IItemHandlerModifiable getCuriosStacks(Player player){
        return EMPTY;
    }
}
