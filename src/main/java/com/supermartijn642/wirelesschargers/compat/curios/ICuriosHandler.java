package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public interface ICuriosHandler {

    IItemHandlerModifiable getCuriosStacks(Player player);
}
