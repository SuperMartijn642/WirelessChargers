package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.wirelesschargers.screen.ChargerScreen;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e){
        for(ChargerType type : ChargerType.values())
            ClientRegistry.bindTileEntityRenderer(type.getTileEntityType(), ChargerRenderer::new);
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
            IBakedModel model = e.getModelRegistry().get(location);
            if(model != null)
                e.getModelRegistry().put(location, new ChargerBakedItemModel(model));
        }
    }

    public static void openChargerScreen(ITextComponent title, BlockPos pos){
        ClientUtils.displayScreen(new ChargerScreen(title, pos));
    }

}
