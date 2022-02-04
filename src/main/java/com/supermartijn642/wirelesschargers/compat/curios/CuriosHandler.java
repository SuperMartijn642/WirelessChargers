package com.supermartijn642.wirelesschargers.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class CuriosHandler implements ICuriosHandler {

    private static final IItemHandlerModifiable EMPTY = new EmptyHandler();

    @Override
    public IItemHandlerModifiable getCuriosStacks(Player player){
        LazyOptional<IItemHandlerModifiable> optional = CuriosApi.getCuriosHelper().getEquippedCurios(player);
        return optional.orElse(EMPTY);
    }
}
