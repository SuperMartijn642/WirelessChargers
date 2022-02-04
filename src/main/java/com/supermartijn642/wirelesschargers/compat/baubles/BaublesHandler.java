package com.supermartijn642.wirelesschargers.compat.baubles;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class BaublesHandler implements IBaublesHandler {

    private static final IItemHandlerModifiable EMPTY = new EmptyHandler();

    @Override
    public IItemHandlerModifiable getCuriosStacks(EntityPlayer player){
        return BaublesApi.getBaublesHandler(player);
    }
}
