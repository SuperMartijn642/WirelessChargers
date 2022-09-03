package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.AdvancementGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.util.ResourceLocation;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerAdvancementGenerator extends AdvancementGenerator {

    public ChargerAdvancementGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        this.advancement("wireless_charging")
            .icon(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .background(new ResourceLocation("minecraft", "block/redstone_block"))
            .hasItemsCriterion("has_player_charger", ChargerType.BASIC_WIRELESS_PLAYER_CHARGER.getItem())
            .hasItemsCriterion("has_block_charger", ChargerType.BASIC_WIRELESS_BLOCK_CHARGER.getItem())
            .requirementGroup("has_player_charger", "has_block_charger");
        this.advancement("no_more_batteries")
            .parent("wireless_charging")
            .icon(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem())
            .hasItemsCriterion("has_charger", ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER.getItem());
        this.advancement("no_more_cables")
            .parent("wireless_charging")
            .icon(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem())
            .hasItemsCriterion("has_charger", ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER.getItem());
    }
}
