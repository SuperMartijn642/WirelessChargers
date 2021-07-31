package com.supermartijn642.wirelesschargers.data;

import com.supermartijn642.wirelesschargers.ChargerType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlockStateProvider extends BlockStateProvider {

    public ChargerBlockStateProvider(GatherDataEvent e){
        super(e.getGenerator(), "wirelesschargers", e.getExistingFileHelper());
    }

    @Override
    protected void registerStatesAndModels(){
        for(ChargerType type : ChargerType.values()){
            this.getVariantBuilder(type.getBlock()).forAllStatesExcept(
                state -> new ConfiguredModel[]{new ConfiguredModel(this.models().getExistingFile(type.modelType.blockModel))},
                BlockStateProperties.WATERLOGGED
            );
        }
    }
}
