package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class CuriosHandler implements ICuriosHandler {

    @Override
    public IItemHandlerModifiable getCuriosStacks(Player player){
        return CuriosApi.getCuriosInventory(player).map(ICuriosItemHandler::getEquippedCurios).orElse(null);
    }
}
