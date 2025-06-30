package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

/**
 * Created 26/12/2024 by SuperMartijn642
 */
public class ChargerSpecialModelRenderer implements SpecialModelRenderer.Unbaked {

    public static final MapCodec<ChargerSpecialModelRenderer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            ChargerType.CODEC.fieldOf("charger").forGetter(renderer -> renderer.chargerType)
        ).apply(instance, ChargerSpecialModelRenderer::new)
    );

    private final ChargerType chargerType;
    private ChargerBlockEntity entity;

    public ChargerSpecialModelRenderer(ChargerType type){
        this.chargerType = type;
    }

    @Override
    public @Nullable SpecialModelRenderer<?> bake(EntityModelSet entityModelSet){
        return new SpecialModelRenderer<CompoundTag>() {
            @Override
            public void render(@Nullable CompoundTag data, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, boolean hasFoil){
                ChargerBlockEntity entity = ChargerSpecialModelRenderer.this.getEntity();
                entity.readData(TagValueInput.create(new ProblemReporter.ScopedCollector(entity.problemPath(), WirelessChargers.LOGGER), CommonUtils.getRegistryAccess(), data));
                BlockEntityRenderer<ChargerBlockEntity> renderer = ClientUtils.getMinecraft().getBlockEntityRenderDispatcher().getRenderer(entity);
                //noinspection DataFlowIssue
                renderer.render(entity, ClientUtils.getPartialTicks(), poseStack, bufferSource, combinedLight, combinedOverlay, Vec3.ZERO);
            }

            @Override
            public void getExtents(Set<Vector3f> set){
            }

            @Override
            public @Nullable CompoundTag extractArgument(ItemStack stack){
                return stack.has(BaseBlock.TILE_DATA) ? stack.get(BaseBlock.TILE_DATA) : new CompoundTag();
            }
        };
    }

    private ChargerBlockEntity getEntity(){
        if(this.entity == null)
            this.entity = this.chargerType.createBlockEntity(BlockPos.ZERO, this.chargerType.getBlock().defaultBlockState());
        return this.entity;
    }

    @Override
    public MapCodec<? extends SpecialModelRenderer.Unbaked> type(){
        return CODEC;
    }
}
