package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.wirelesschargers.data.*;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("wirelesschargers")
public class WirelessChargers {

    public static final PacketChannel CHANNEL = PacketChannel.create("wirelesschargers");

    public static final CreativeModeTab GROUP = new CreativeModeTab("wirelesschargers") {
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem());
        }
    };

    public WirelessChargers(){
        CHANNEL.registerMessage(ToggleHighlightAreaPacket.class, ToggleHighlightAreaPacket::new, true);
        CHANNEL.registerMessage(CycleRedstoneModePacket.class, CycleRedstoneModePacket::new, true);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onBlockRegistry(RegistryEvent.Register<Block> e){
            for(ChargerType type : ChargerType.values())
                type.registerBlock(e.getRegistry());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<BlockEntityType<?>> e){
            for(ChargerType type : ChargerType.values())
                type.registerTileEntity(e.getRegistry());
        }

        @SubscribeEvent
        public static void onItemRegistry(RegistryEvent.Register<Item> e){
            for(ChargerType type : ChargerType.values())
                type.registerItem(e.getRegistry());
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent e){
            e.getGenerator().addProvider(new ChargerItemModelProvider(e));
            e.getGenerator().addProvider(new ChargerBlockStateProvider(e));
            e.getGenerator().addProvider(new ChargerLanguageProvider(e));
            e.getGenerator().addProvider(new ChargerLootTableProvider(e));
            e.getGenerator().addProvider(new ChargerRecipeProvider(e));
            e.getGenerator().addProvider(new ChargerBlockTagsProvider(e));
        }
    }

}
