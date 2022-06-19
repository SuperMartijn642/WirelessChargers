package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import com.supermartijn642.wirelesschargers.data.*;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Objects;

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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModCompatibility::init);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent e){
            if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS))
                onBlockRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
                onTileEntityRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                onItemRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onBlockRegistry(IForgeRegistry<Block> registry){
            for(ChargerType type : ChargerType.values())
                type.registerBlock(registry);
        }

        public static void onTileEntityRegistry(IForgeRegistry<BlockEntityType<?>> registry){
            for(ChargerType type : ChargerType.values())
                type.registerTileEntity(registry);
        }

        public static void onItemRegistry(IForgeRegistry<Item> registry){
            for(ChargerType type : ChargerType.values())
                type.registerItem(registry);
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent e){
            e.getGenerator().addProvider(e.includeClient(), new ChargerItemModelProvider(e));
            e.getGenerator().addProvider(e.includeClient(), new ChargerBlockStateProvider(e));
            e.getGenerator().addProvider(e.includeClient(), new ChargerLanguageProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new ChargerLootTableProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new ChargerRecipeProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new ChargerBlockTagsProvider(e));
            e.getGenerator().addProvider(e.includeServer(), new ChargerAdvancementProvider(e));
        }
    }

}
