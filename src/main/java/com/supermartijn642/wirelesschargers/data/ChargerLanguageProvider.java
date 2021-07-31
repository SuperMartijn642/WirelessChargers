package com.supermartijn642.wirelesschargers.data;

import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerLanguageProvider extends LanguageProvider {

    public ChargerLanguageProvider(GatherDataEvent e){
        super(e.getGenerator(), "wirelesschargers", "en_us");
    }

    @Override
    protected void addTranslations(){
        this.add("itemGroup.wirelesschargers", "Wireless Chargers");

        this.add("wirelesschargers.charger.info.blocks","Charges blocks in a %1$dx%1$dx%1$d area.");
        this.add("wirelesschargers.charger.info.players","Charges players' items in a %1$dx%1$dx%1$d area.");
        this.add("wirelesschargers.charger.info.transfer_rate_blocks","Charging rate: %d per block");
        this.add("wirelesschargers.charger.info.transfer_rate_players","Charging rate: %d per player");
        this.add("wirelesschargers.charger.info.stored_energy","Energy stored: %1$d / %2$d");
        this.add("wirelesschargers.charger.info.redstone_mode","Redstone mode: %s");

        this.add("wirelesschargers.screen.stored_energy", "Energy stored: %1$d / %2$d");
        this.add("wirelesschargers.screen.highlight_area", "Highlight area: %s");
        this.add("wirelesschargers.screen.redstone", "Redstone mode: %s");
        this.add("wirelesschargers.screen.redstone_high", "High");
        this.add("wirelesschargers.screen.redstone_low", "Low");
        this.add("wirelesschargers.screen.redstone_disabled", "Disabled");

        for(ChargerType type : ChargerType.values())
            this.add(type.getBlock(), type.englishTranslation);
    }
}
