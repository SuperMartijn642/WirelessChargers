package com.supermartijn642.wirelesschargers.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created 9/2/2021 by SuperMartijn642
 */
public class ChargerAdvancementProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> tabs = ImmutableList.of(new TheEndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

    public ChargerAdvancementProvider(GatherDataEvent e){
        this.generator = e.getGenerator();
    }

    public void run(HashCache hashCache){
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if(!set.add(advancement.getId())){
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }else{
                Path advancementPath = createPath(path, advancement);
                try{
                    DataProvider.save(GSON, hashCache, advancement.deconstruct().serializeToJson(), advancementPath);
                }catch(IOException ioexception){
                    LOGGER.error("Couldn't save advancement {}", advancementPath, ioexception);
                }
            }
        };

        Advancement wireless_charging = Advancement.Builder.advancement().
            display(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem(), TextComponents.translation("wirelesschargers.advancement.wireless_charging.title").get(), TextComponents.translation("wirelesschargers.advancement.wireless_charging.description").get(), new ResourceLocation("minecraft","textures/block/redstone_block.png"), FrameType.TASK, true, true, false)
            .requirements(RequirementsStrategy.OR)
            .addCriterion("has_player_charger", InventoryChangeTrigger.TriggerInstance.hasItems(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem()))
            .addCriterion("has_block_charger", InventoryChangeTrigger.TriggerInstance.hasItems(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem()))
            .save(consumer, "wirelesschargers:wireless_charging");
        Advancement no_more_batteries = Advancement.Builder.advancement()
            .parent(wireless_charging)
            .display(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem(), TextComponents.translation("wirelesschargers.advancement.no_more_batteries.title").get(), TextComponents.translation("wirelesschargers.advancement.no_more_batteries.description").get(), null, FrameType.TASK, true, true, false)
            .addCriterion("has_charger", InventoryChangeTrigger.TriggerInstance.hasItems(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem()))
            .save(consumer, "wirelesschargers:no_more_batteries");
        Advancement no_more_cables = Advancement.Builder.advancement()
            .parent(wireless_charging)
            .display(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem(), TextComponents.translation("wirelesschargers.advancement.no_more_cables.title").get(), TextComponents.translation("wirelesschargers.advancement.no_more_cables.description").get(), null, FrameType.TASK, true, true, false)
            .addCriterion("has_charger", InventoryChangeTrigger.TriggerInstance.hasItems(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem()))
            .save(consumer, "wirelesschargers:no_more_cables");
    }

    private static Path createPath(Path path, Advancement advancement){
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    public String getName(){
        return "Advancements";
    }
}
