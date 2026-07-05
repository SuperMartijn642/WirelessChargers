package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.ItemInfoGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.ChargerSpecialModelRenderer;
import com.supermartijn642.wirelesschargers.ChargerType;

/**
 * Created 26/12/2024 by SuperMartijn642
 */
public class ChargerItemInfoGenerator extends ItemInfoGenerator {

    public ChargerItemInfoGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        for(ChargerType type : ChargerType.values())
            this.info(type.getItem()).model(
                this.compositeModel()
                    .addModel(this.model(type.modelType.blockModel))
                    .addModel(this.specialModel(new ChargerSpecialModelRenderer(type), type.modelType.blockModel, null))
            );
    }
}
