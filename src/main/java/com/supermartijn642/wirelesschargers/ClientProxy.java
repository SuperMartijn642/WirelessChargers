package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.wirelesschargers.screen.ChargerScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers e){
        for(ChargerType type : ChargerType.values())
            e.registerBlockEntityRenderer(type.getTileEntityType(), context -> new ChargerRenderer());
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterAdditional e){
        for(ChargerModelType type : ChargerModelType.values())
            e.register(type.ringModel);
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.BakingCompleted e){
        // replace the reservoir item models
        for(ChargerType type : ChargerType.values()){
            ResourceLocation location = new ModelResourceLocation("wirelesschargers:" + type.getRegistryName(), "inventory");
            BakedModel model = e.getModels().get(location);
            if(model != null)
                e.getModels().put(location, new ChargerBakedItemModel(model));
        }
    }

    public static void openChargerScreen(Component title, BlockPos pos){
        ClientUtils.displayScreen(new ChargerScreen(title, pos));
    }

}
