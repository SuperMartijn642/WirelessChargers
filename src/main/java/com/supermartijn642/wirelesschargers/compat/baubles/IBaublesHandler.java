package com.supermartijn642.wirelesschargers.compat.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public interface IBaublesHandler {

    IItemHandlerModifiable getCuriosStacks(EntityPlayer player);
}
