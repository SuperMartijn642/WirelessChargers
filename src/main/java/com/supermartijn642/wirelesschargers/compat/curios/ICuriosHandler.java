package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public interface ICuriosHandler {

    IItemHandlerModifiable getCuriosStacks(PlayerEntity player);
}
