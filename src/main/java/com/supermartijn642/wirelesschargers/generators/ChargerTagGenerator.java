package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.wirelesschargers.ChargerType;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerTagGenerator extends TagGenerator {

    public ChargerTagGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        for(ChargerType type : ChargerType.values()){
            this.blockMineableWithPickaxe().add(type.getBlock());
            this.blockNeedsStoneTool().add(type.getBlock());
        }
    }
}
