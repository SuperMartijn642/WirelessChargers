package com.supermartijn642.wirelesschargers.data;

import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.data.event.GatherDataEvent;

/**
 * Created 8/3/2021 by SuperMartijn642
 */
public class ChargerBlockTagsProvider extends BlockTagsProvider {

    public ChargerBlockTagsProvider(GatherDataEvent e){
        super(e.getGenerator(), "wirelesschargers", e.getExistingFileHelper());
    }

    @Override
    protected void addTags(){
        for(ChargerType type : ChargerType.values()){
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(type.getBlock());
            this.tag(BlockTags.NEEDS_STONE_TOOL).add(type.getBlock());
        }
    }
}
