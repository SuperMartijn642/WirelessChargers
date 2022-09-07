package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.LootTableGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerLootTableGenerator extends LootTableGenerator {

    public ChargerLootTableGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        for(ChargerType type : ChargerType.values())
            this.dropSelf(type.getBlock());
    }
}
