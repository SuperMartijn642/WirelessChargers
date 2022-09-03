package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerModelGenerator extends ModelGenerator {

    public ChargerModelGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        for(ChargerType type : ChargerType.values())
            this.model("item/" + type.getRegistryName()).parent(type.modelType.blockModel);
    }
}
