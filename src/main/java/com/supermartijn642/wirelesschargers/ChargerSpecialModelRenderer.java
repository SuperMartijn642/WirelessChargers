package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.client.renderer.block.model.CompositeBlockModel;
import net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created 26/12/2024 by SuperMartijn642
 */
public class ChargerSpecialModelRenderer implements SpecialModelRenderer.Unbaked<BlockEntityRenderState> {

    public static final MapCodec<ChargerSpecialModelRenderer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ChargerType.CODEC.fieldOf("charger").forGetter(renderer -> renderer.chargerType)
        ).apply(instance, ChargerSpecialModelRenderer::new)
    );
    private static final CameraRenderState DUMMY_CAMERA_RENDER_STATE = new CameraRenderState();

    public static BlockModel.Unbaked createBlockModel(ChargerType type, BlockState state, BlockColors blockColors){
        return new CompositeBlockModel.Unbaked(
            new BlockStateModelWrapper.Unbaked(state, blockColors.getTintSources(state), Optional.empty()),
            new SpecialBlockModelWrapper.Unbaked<>(new ChargerSpecialModelRenderer(type), Optional.empty()),
            Optional.empty()
        );
    }

    private final ChargerType chargerType;
    private ChargerBlockEntity entity;

    public ChargerSpecialModelRenderer(ChargerType type){
        this.chargerType = type;
    }

    @Override
    public @Nullable SpecialModelRenderer<BlockEntityRenderState> bake(SpecialModelRenderer.BakingContext context){
        return new SpecialModelRenderer<>() {
            @Override
            public void submit(@Nullable BlockEntityRenderState entityRenderState, PoseStack poseStack, SubmitNodeCollector output, int combinedLight, int combinedOverlay, boolean hasFoil, int k){
                if(entityRenderState != null)
                    ClientUtils.getMinecraft().getBlockEntityRenderDispatcher().submit(entityRenderState, poseStack, output, DUMMY_CAMERA_RENDER_STATE);
            }

            @Override
            public void getExtents(Consumer<Vector3fc> set){
            }

            @Override
            public @Nullable BlockEntityRenderState extractArgument(ItemStack stack){
                // Read entity data from stack
                ChargerBlockEntity entity = ChargerSpecialModelRenderer.this.getEntity();
                CompoundTag data = stack.has(BaseBlock.TILE_DATA) ? stack.get(BaseBlock.TILE_DATA) : new CompoundTag();
                entity.readData(TagValueInput.create(new ProblemReporter.ScopedCollector(entity.problemPath(), WirelessChargers.LOGGER), CommonUtils.getRegistryAccess(), data));
                // Extract block entity render state
                BlockEntityRenderer<ChargerBlockEntity,BlockEntityRenderState> renderer = ClientUtils.getMinecraft().getBlockEntityRenderDispatcher().getRenderer(entity);
                if(renderer == null)
                    return null;
                BlockEntityRenderState entityRenderState = renderer.createRenderState();
                renderer.extractRenderState(entity, entityRenderState, ClientUtils.getPartialTicks(), Vec3.ZERO, null);
                return entityRenderState;
            }
        };
    }

    private ChargerBlockEntity getEntity(){
        if(this.entity == null)
            this.entity = this.chargerType.createBlockEntity(BlockPos.ZERO, this.chargerType.getBlock().defaultBlockState());
        return this.entity;
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked<BlockEntityRenderState>> type(){
        return CODEC;
    }
}
