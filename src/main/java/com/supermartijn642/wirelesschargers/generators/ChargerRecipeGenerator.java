package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerRecipeGenerator extends RecipeGenerator {

    public ChargerRecipeGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        // Basic wireless block charger
        this.shaped(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .input('A', Tags.Items.DUSTS_GLOWSTONE)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Tags.Items.ENDER_PEARLS)
            .input('D', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(Tags.Items.ENDER_PEARLS);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', Tags.Items.DUSTS_GLOWSTONE)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem());

        // Basic wireless player charger
        this.shaped(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .input('A', Tags.Items.INGOTS_GOLD)
            .input('B', Tags.Items.INGOTS_IRON)
            .input('C', Tags.Items.ENDER_PEARLS)
            .input('D', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(Tags.Items.ENDER_PEARLS);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', Tags.Items.DUSTS_GLOWSTONE)
            .input('B', Tags.Items.INGOTS_GOLD)
            .input('C', ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem());
    }
}
