package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import com.supermartijn642.wirelesschargers.data.*;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("wirelesschargers")
public class WirelessChargers {

    public static final PacketChannel CHANNEL = PacketChannel.create("wirelesschargers");

    public static final ItemGroup GROUP = new ItemGroup("wirelesschargers") {
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem());
        }
    };

    public WirelessChargers(){
        CHANNEL.registerMessage(ToggleHighlightAreaPacket.class, ToggleHighlightAreaPacket::new, true);
        CHANNEL.registerMessage(CycleRedstoneModePacket.class, CycleRedstoneModePacket::new, true);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModCompatibility::init);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onBlockRegistry(RegistryEvent.Register<Block> e){
            for(ChargerType type : ChargerType.values())
                type.registerBlock(e.getRegistry());
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> e){
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
            e.getGenerator().addProvider(new ChargerAdvancementProvider(e));
        }
    }

}
