package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import com.supermartijn642.wirelesschargers.generators.*;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = "@mod_id@", name = "@mod_name@", version = "@mod_version@", dependencies = "required-after:supermartijn642corelib@@core_library_dependency@;required-after:supermartijn642configlib@@config_library_dependency@")
public class WirelessChargers {

    public static final PacketChannel CHANNEL = PacketChannel.create("wirelesschargers");
    public static final CreativeItemGroup GROUP = CreativeItemGroup.create("wirelesschargers", ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER::getItem);

    public WirelessChargers(){
        CHANNEL.registerMessage(ToggleHighlightAreaPacket.class, ToggleHighlightAreaPacket::new, true);
        CHANNEL.registerMessage(CycleRedstoneModePacket.class, CycleRedstoneModePacket::new, true);

        register();
        if(CommonUtils.getEnvironmentSide().isClient())
            WirelessChargersClient.register();
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("wirelesschargers");
        for(ChargerType type : ChargerType.values()){
            handler.registerBlockCallback(type::registerBlock);
            handler.registerBlockEntityTypeCallback(type::registerBlockEntity);
            handler.registerItemCallback(type::registerItem);
        }
    }

    public static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("wirelesschargers");
        handler.addGenerator(ChargerModelGenerator::new);
        handler.addGenerator(ChargerBlockStateGenerator::new);
        handler.addGenerator(ChargerLanguageGenerator::new);
        handler.addGenerator(ChargerLootTableGenerator::new);
        handler.addGenerator(ChargerRecipeGenerator::new);
        handler.addGenerator(ChargerTagGenerator::new);
        handler.addGenerator(ChargerAdvancementGenerator::new);
    }

    @Mod.EventHandler
    public static void onInit(FMLInitializationEvent e){
        ModCompatibility.init(e);
    }
}
