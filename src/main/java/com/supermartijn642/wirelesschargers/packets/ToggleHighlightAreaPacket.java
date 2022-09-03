package com.supermartijn642.wirelesschargers.packets;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import net.minecraft.core.BlockPos;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class ToggleHighlightAreaPacket extends BlockEntityBasePacket<ChargerBlockEntity> {

    public ToggleHighlightAreaPacket(){
        super();
    }

    public ToggleHighlightAreaPacket(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(ChargerBlockEntity entity, PacketContext context){
        entity.toggleHighlightArea();
    }
}
