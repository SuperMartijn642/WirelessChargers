package com.supermartijn642.wirelesschargers.generators;

import com.supermartijn642.core.generator.AtlasSourceGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.wirelesschargers.screen.EnergyBarWidget;
import com.supermartijn642.wirelesschargers.screen.HighlightAreaButton;
import com.supermartijn642.wirelesschargers.screen.RedstoneModeButton;

/**
 * Created 02/09/2022 by SuperMartijn642
 */
public class ChargerAtlasSourceGenerator extends AtlasSourceGenerator {

    public ChargerAtlasSourceGenerator(ResourceCache cache){
        super("wirelesschargers", cache);
    }

    @Override
    public void generate(){
        this.guiAtlas()
            .texture(EnergyBarWidget.BARS)
            .texture(HighlightAreaButton.BUTTONS)
            .texture(RedstoneModeButton.BUTTONS);
    }
}
