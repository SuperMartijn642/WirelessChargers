package com.supermartijn642.wirelesschargers;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Consumer;

/**
 * Created 23/05/2023 by SuperMartijn642
 */
public class ChargerApiProviders {

    public static void register(){
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((Consumer<RegisterCapabilitiesEvent>)event -> {
            for(ChargerType type : ChargerType.values())
                event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type.getBlockEntityType(), (entity, side) -> entity);
        });
    }
}
