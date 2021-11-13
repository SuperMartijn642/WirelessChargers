package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod(modid = WirelessChargers.MODID, name = WirelessChargers.NAME, version = WirelessChargers.VERSION, dependencies = WirelessChargers.DEPENDENCIES)
public class WirelessChargers {

    public static final String MODID = "wirelesschargers";
    public static final String NAME = "Wireless Chargers";
    public static final String VERSION = "1.0.3";
    public static final String DEPENDENCIES = "required-after:supermartijn642corelib@[1.0.14,);required-after:supermartijn642configlib@[1.0.9,)";

    public static final PacketChannel CHANNEL = PacketChannel.create("wirelesschargers");

    public static final CreativeTabs GROUP = new CreativeTabs("wirelesschargers") {
        @Override
        public ItemStack getTabIconItem(){
            return new ItemStack(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem());
        }
    };

    public WirelessChargers(){
        CHANNEL.registerMessage(ToggleHighlightAreaPacket.class, ToggleHighlightAreaPacket::new, true);
        CHANNEL.registerMessage(CycleRedstoneModePacket.class, CycleRedstoneModePacket::new, true);
    }

    @Mod.EventBusSubscriber
    public static class ModEvents {

        @SubscribeEvent
        public static void onBlockRegistry(RegistryEvent.Register<Block> e){
            for(ChargerType type : ChargerType.values()){
                type.registerBlock(e.getRegistry());
                type.registerTileEntity();
            }
        }

        @SubscribeEvent
        public static void onItemRegistry(RegistryEvent.Register<Item> e){
            for(ChargerType type : ChargerType.values())
                type.registerItem(e.getRegistry());
        }
    }

}
