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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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
    public static void onModelRegistry(ModelRegistryEvent e){
        for(ChargerModelType type : ChargerModelType.values())
            ModelLoader.addSpecialModel(type.ringModel);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e){
        // replace the reservoir item models
        for(ChargerType type : ChargerType.values()){
            ResourceLocation location = new ModelResourceLocation("wirelesschargers:" + type.getRegistryName(), "inventory");
            BakedModel model = e.getModelRegistry().get(location);
            if(model != null)
                e.getModelRegistry().put(location, new ChargerBakedItemModel(model));
        }
    }

    public static void openChargerScreen(Component title, BlockPos pos){
        ClientUtils.displayScreen(new ChargerScreen(title, pos));
    }

}
