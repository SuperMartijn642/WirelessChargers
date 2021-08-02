package com.supermartijn642.wirelesschargers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public enum ChargerType {

    BASIC_WIRELESS_BLOCK_CHARGER(true, false, WirelessChargersConfig.basicBlockChargerRange, WirelessChargersConfig.basicBlockChargerCapacity, WirelessChargersConfig.basicBlockChargerTransferRate, ChargerModelType.BASIC_WIRELESS_BLOCK_CHARGER, "Basic Wireless Block Charger"),
    ADVANCED_WIRELESS_BLOCK_CHARGER(true, false, WirelessChargersConfig.advancedBlockChargerRange, WirelessChargersConfig.advancedBlockChargerCapacity, WirelessChargersConfig.advancedBlockChargerTransferRate, ChargerModelType.ADVANCED_WIRELESS_BLOCK_CHARGER, "Advanced Wireless Block Charger"),
    BASIC_WIRELESS_PLAYER_CHARGER(false, true, WirelessChargersConfig.basicPlayerChargerRange, WirelessChargersConfig.basicPlayerChargerCapacity, WirelessChargersConfig.basicPlayerChargerTransferRate, ChargerModelType.BASIC_WIRELESS_PLAYER_CHARGER, "Basic Wireless Player Charger"),
    ADVANCED_WIRELESS_PLAYER_CHARGER(false, true, WirelessChargersConfig.advancedPlayerChargerRange, WirelessChargersConfig.advancedPlayerChargerCapacity, WirelessChargersConfig.advancedPlayerChargerTransferRate, ChargerModelType.ADVANCED_WIRELESS_PLAYER_CHARGER, "Advanced Wireless Player Charger");

    private BlockEntityType<ChargerBlockEntity> tileEntityType;
    private ChargerBlock block;
    private BlockItem item;
    public final boolean canChargeBlocks, canChargePlayers;
    public final Supplier<Integer> range, capacity, transferRate;
    public final ChargerModelType modelType;
    public final String englishTranslation;

    ChargerType(boolean canChargeBlocks, boolean canChargePlayers, Supplier<Integer> range, Supplier<Integer> capacity, Supplier<Integer> transferRate, ChargerModelType modelType, String englishTranslation){
        this.canChargeBlocks = canChargeBlocks;
        this.canChargePlayers = canChargePlayers;
        this.range = range;
        this.capacity = capacity;
        this.transferRate = transferRate;
        this.modelType = modelType;
        this.englishTranslation = englishTranslation;
    }

    public String getRegistryName(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    public ChargerBlock getBlock(){
        return this.block;
    }

    public ChargerBlockEntity createTileEntity(BlockPos pos, BlockState state){
        return new ChargerBlockEntity(this, pos, state);
    }

    public BlockEntityType<ChargerBlockEntity> getTileEntityType(){
        return this.tileEntityType;
    }

    public BlockItem getItem(){
        return this.item;
    }

    public void registerBlock(IForgeRegistry<Block> registry){
        if(this.block != null)
            throw new IllegalStateException("Blocks have already been registered!");

        this.block = new ChargerBlock(this);
        registry.register(this.block);
    }

    public void registerTileEntity(IForgeRegistry<BlockEntityType<?>> registry){
        if(this.tileEntityType != null)
            throw new IllegalStateException("Tile entities have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering tile entity types!");

        this.tileEntityType = BlockEntityType.Builder.of(this::createTileEntity, this.block).build(null);
        this.tileEntityType.setRegistryName(this.getRegistryName() + "_block_entity");
        registry.register(this.tileEntityType);
    }

    public void registerItem(IForgeRegistry<Item> registry){
        if(this.item != null)
            throw new IllegalStateException("Items have already been registered!");
        if(this.block == null)
            throw new IllegalStateException("Blocks must be registered before registering items!");

        this.item = new ChargerBlockItem(this.block, new Item.Properties().tab(WirelessChargers.GROUP));
        this.item.setRegistryName(this.block.getRegistryName());
        registry.register(this.item);
    }
}
