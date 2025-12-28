package com.supermartijn642.wirelesschargers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Consumer;

/**
 * Created 23/05/2023 by SuperMartijn642
 */
public class ChargerApiProviders {

    public static void register(IEventBus eventBus){
        eventBus.addListener((Consumer<RegisterCapabilitiesEvent>)event -> {
            for(ChargerType type : ChargerType.values())
                event.registerBlockEntity(Capabilities.Energy.BLOCK, type.getBlockEntityType(), (entity, side) -> entity);
        });
    }
}
