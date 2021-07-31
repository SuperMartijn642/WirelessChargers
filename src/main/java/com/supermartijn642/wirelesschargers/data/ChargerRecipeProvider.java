package com.supermartijn642.wirelesschargers.data;

import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRecipeProvider extends RecipeProvider {

    public ChargerRecipeProvider(GatherDataEvent e){
        super(e.getGenerator());
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> recipeConsumer){
        // Basic wireless block charger
        ShapedRecipeBuilder.shaped(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .define('A', Tags.Items.DUSTS_GLOWSTONE)
            .define('B', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.ENDER_PEARLS)
            .define('D', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlocks("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
            .save(recipeConsumer);

        // Advanced wireless block charger
        ShapedRecipeBuilder.shaped(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', Tags.Items.DUSTS_GLOWSTONE)
            .define('B', Tags.Items.INGOTS_IRON)
            .define('C', ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .define('D', Items.BLAZE_POWDER)
            .define('E', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlocks("has_basic_charger", has(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem()))
            .save(recipeConsumer);

        // Basic wireless player charger
        ShapedRecipeBuilder.shaped(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("BDB")
            .define('A', Tags.Items.INGOTS_GOLD)
            .define('B', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.ENDER_PEARLS)
            .define('D', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlocks("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
            .save(recipeConsumer);

        // Advanced wireless block charger
        ShapedRecipeBuilder.shaped(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem())
            .pattern(" A ")
            .pattern("BCB")
            .pattern("DED")
            .define('A', Tags.Items.DUSTS_GLOWSTONE)
            .define('B', Tags.Items.INGOTS_GOLD)
            .define('C', ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .define('D', Items.BLAZE_POWDER)
            .define('E', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .unlocks("has_basic_charger", has(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem()))
            .save(recipeConsumer);
    }
}
