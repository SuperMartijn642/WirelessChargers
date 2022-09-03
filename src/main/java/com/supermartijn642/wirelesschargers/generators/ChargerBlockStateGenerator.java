package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.BlockStateGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerType;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerBlockStateGenerator extends BlockStateGenerator {

    public ChargerBlockStateGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        for(ChargerType type : ChargerType.values())
            this.blockState(type.getBlock()).emptyVariant(builder -> builder.model(type.modelType.blockModel));
    }
}
