package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.wirelesschargers.screen.ChargerScreen;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> e){
        ClientRegistry.bindTileEntitySpecialRenderer(ChargerBlockEntity.class, new ChargerRenderer());
    }

    @SubscribeEvent
    public static void setup(ModelRegistryEvent e){
        // Stupid 1.12 stuff
        for(ChargerType type : ChargerType.values())
            type.getItem().setTileEntityItemStackRenderer(ChargerItemStackBlockEntityRenderer.INSTANCE);

        for(ChargerType type : ChargerType.values())
            ModelLoader.setCustomModelResourceLocation(type.getItem(), 0, new ModelResourceLocation(type.getItem().getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent e) throws Exception{
//        for(ChargerModelType type : ChargerModelType.values())
//            ModelLoaderRegistry.getModel(type.ringModel);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) throws Exception{
        // replace the reservoir item models
        for(ChargerType type : ChargerType.values()){
            ModelResourceLocation location = new ModelResourceLocation("wirelesschargers:" + type.getRegistryName(), "inventory");
            IBakedModel model = e.getModelRegistry().getObject(location);
            if(model != null)
                e.getModelRegistry().putObject(location, new ChargerBakedItemModel(model));
        }
    }

    public static void openChargerScreen(ITextComponent title, BlockPos pos){
        ClientUtils.displayScreen(new ChargerScreen(title, pos));
    }

}
