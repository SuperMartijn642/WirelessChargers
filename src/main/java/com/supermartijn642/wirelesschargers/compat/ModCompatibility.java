package com.supermartijn642.wirelesschargers.compat;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.wirelesschargers.compat.baubles.BaublesHandler;
import com.supermartijn642.wirelesschargers.compat.baubles.DummyBaublesHandler;
import com.supermartijn642.wirelesschargers.compat.baubles.IBaublesHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class ModCompatibility {

    public static IBaublesHandler baubles;

    public static void init(FMLInitializationEvent e){
        baubles = CommonUtils.isModLoaded("baubles") ? new BaublesHandler() : new DummyBaublesHandler();
    }
}
