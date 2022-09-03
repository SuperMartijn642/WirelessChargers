package com.supermartijn642.wirelesschargers.packets;

import com.supermartijn642.core.network.BlockEntityBasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.wirelesschargers.ChargerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created 7/31/2021 by SuperMartijn642
 */
public class CycleRedstoneModePacket extends BlockEntityBasePacket<ChargerBlockEntity> {

    public CycleRedstoneModePacket(){
        super();
    }

    public CycleRedstoneModePacket(BlockPos pos){
        super(pos);
    }

    @Override
    protected void handle(ChargerBlockEntity entity, PacketContext context){
        entity.cycleRedstoneMode();
    }
}
