package com.supermartijn642.wirelesschargers;

import team.reborn.energy.api.EnergyStorage;

/**
 * Created 23/05/2023 by SuperMartijn642
 */
public class ChargerApiProviders {

    public static void register(){
        for(ChargerType type : ChargerType.values())
            EnergyStorage.SIDED.registerForBlockEntity((entity, direction) -> entity, type.getBlockEntityType());
    }
}
