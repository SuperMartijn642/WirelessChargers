package com.supermartijn642.wirelesschargers.compat;

import com.supermartijn642.wirelesschargers.compat.curios.CuriosHandler;
import com.supermartijn642.wirelesschargers.compat.curios.DummyCuriosHandler;
import com.supermartijn642.wirelesschargers.compat.curios.ICuriosHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created 04/02/2022 by SuperMartijn642
 */
public class ModCompatibility {

    public static ICuriosHandler curios;

    public static void init(FMLCommonSetupEvent e){
        curios = ModList.get().isLoaded("curios") ? new CuriosHandler() : new DummyCuriosHandler();
    }
}
