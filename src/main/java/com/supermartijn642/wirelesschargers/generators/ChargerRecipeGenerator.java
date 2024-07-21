package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.world.item.Items;

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
            .input('A', ConventionalItemTags.GLOWSTONE_DUSTS)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.ENDER_PEARL)
            .input('D', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(Items.ENDER_PEARL);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', ConventionalItemTags.GLOWSTONE_DUSTS)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem());

        // Basic wireless player charger
        this.shaped(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .input('A', ConventionalItemTags.GOLD_INGOTS)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', Items.ENDER_PEARL)
            .input('D', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(Items.ENDER_PEARL);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', ConventionalItemTags.GLOWSTONE_DUSTS)
            .input('B', ConventionalItemTags.GOLD_INGOTS)
            .input('C', ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
            .unlockedBy(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem());
    }
}
