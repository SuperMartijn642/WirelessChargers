package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;
import com.supermartijn642.wirelesschargers.WirelessChargers;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerLanguageGenerator extends LanguageGenerator {

    public ChargerLanguageGenerator(ResourceCache cache){
        super("wirelesschargers", cache, "en_us");
    }

    @Override
    public void generate(){
        this.itemGroup(WirelessChargers.GROUP, "Wireless Chargers");

        this.translation("wirelesschargers.charger.info.blocks", "Charges blocks in a %1$dx%1$dx%1$d area.");
        this.translation("wirelesschargers.charger.info.players", "Charges players' items in a %1$dx%1$dx%1$d area.");
        this.translation("wirelesschargers.charger.info.transfer_rate_blocks", "Charging rate: %d per block");
        this.translation("wirelesschargers.charger.info.transfer_rate_players", "Charging rate: %d per player");
        this.translation("wirelesschargers.charger.info.stored_energy", "Energy stored: %1$d / %2$d");
        this.translation("wirelesschargers.charger.info.redstone_mode", "Redstone mode: %s");

        this.translation("wirelesschargers.screen.stored_energy", "Energy stored: %1$d / %2$d");
        this.translation("wirelesschargers.screen.highlight_area", "Highlight area: %s");
        this.translation("wirelesschargers.screen.redstone", "Redstone mode: %s");
        this.translation("wirelesschargers.screen.redstone_high", "High");
        this.translation("wirelesschargers.screen.redstone_low", "Low");
        this.translation("wirelesschargers.screen.redstone_disabled", "Disabled");

        this.translation("wirelesschargers.advancement.wireless_charging.title", "Wireless Charging");
        this.translation("wirelesschargers.advancement.wireless_charging.description", "Craft a wireless charger");
        this.translation("wirelesschargers.advancement.no_more_batteries.title", "No More Batteries");
        this.translation("wirelesschargers.advancement.no_more_batteries.description", "Craft an advanced wireless player charger");
        this.translation("wirelesschargers.advancement.no_more_cables.title", "No More Cables");
        this.translation("wirelesschargers.advancement.no_more_cables.description", "Craft an advanced wireless block charger");

        for(ChargerType type : ChargerType.values())
            this.block(type.getBlock(), type.englishTranslation);
    }
}
