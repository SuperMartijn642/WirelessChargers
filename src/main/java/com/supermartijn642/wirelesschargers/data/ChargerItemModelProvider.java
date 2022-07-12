package com.supermartijn642.wirelesschargers.data;

import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.data.event.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerItemModelProvider extends ItemModelProvider {

    public ChargerItemModelProvider(GatherDataEvent e){
        super(e.getGenerator(), "wirelesschargers", e.getExistingFileHelper());
    }

    @Override
    protected void registerModels(){
        for(ChargerType type : ChargerType.values())
            this.withExistingParent("item/" + type.getRegistryName(), type.modelType.blockModel);
    }
}
