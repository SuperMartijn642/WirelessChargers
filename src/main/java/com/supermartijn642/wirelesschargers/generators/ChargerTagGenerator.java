package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.tags.BlockTags;

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
            this.blockTag(BlockTags.MINEABLE_WITH_PICKAXE).add(type.getBlock());
            this.blockTag(BlockTags.NEEDS_STONE_TOOL).add(type.getBlock());
        }
    }
}
