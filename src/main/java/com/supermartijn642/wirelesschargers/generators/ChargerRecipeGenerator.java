package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.init.Items;

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
            .input('A', "dustGlowstone")
            .input('B', "ingotIron")
            .input('C', Items.ENDER_PEARL)
            .input('D', "blockRedstone")
            .unlockedBy(Items.ENDER_PEARL);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', "dustGlowstone")
            .input('B', "ingotIron")
            .input('C', ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', "blockRedstone")
            .unlockedBy(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem());

        // Basic wireless player charger
        this.shaped(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .input('A', "ingotGold")
            .input('B', "ingotIron")
            .input('C', Items.ENDER_PEARL)
            .input('D', "blockRedstone")
            .unlockedBy(Items.ENDER_PEARL);

        // Advanced wireless block charger
        this.shaped(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .input('A', "dustGlowstone")
            .input('B', "ingotGold")
            .input('C', ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .input('D', Items.BLAZE_POWDER)
            .input('E', "blockRedstone")
            .unlockedBy(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem());
    }
}
