package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.block.BlockShape;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;

import java.util.List;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer implements CustomBlockEntityRenderer<ChargerBlockEntity,ChargerRenderer.State> {

    private static final Matrix4fc IDENTITY_MATRIX = new Matrix4f();

    @Override
    public State createStateHolder(){
        return new State();
    }

    @Override
    public void updateState(State state, ChargerBlockEntity entity, UpdateContext context){
        BlockAndTintGetter level = entity.getLevel() instanceof ClientLevel l ? l : BlockAndTintGetter.EMPTY;
        BlockPos pos = entity.getBlockPos();
        BlockState blockState = entity.getBlockState();
        BlockStateModel ringModel = WirelessChargersClient.getRingModel(entity.type);
        long seed = blockState.getSeed(pos);
        RandomSource random = context.randomSource(seed);
        ModelDataManager modelDataManager = level.getModelDataManager();
        ModelData modelData = ringModel.getModelData(level, pos, blockState, modelDataManager == null ? ModelData.EMPTY : modelDataManager.getAtOrEmpty(pos));
        List<BlockStateModelPart> parts = state.ringRenderState.setupModel(IDENTITY_MATRIX, ringModel.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT));
        ringModel.collectParts(random, parts, modelData);
        IntList tintLayers = state.ringRenderState.tintLayers();
        for(BlockTintSource tintSource : ClientUtils.getMinecraft().getBlockColors().getTintSources(blockState))
            tintLayers.add(tintSource.colorInWorld(blockState, level, pos));
        state.ringOffset = entity.type.modelType.ringYOffset;
        state.renderingTickCount = entity.renderingTickCount;
        state.renderingRotation = entity.renderingRotation;
        state.renderingRotationSpeed = entity.renderingRotationSpeed;
        state.shouldHighlightArea = entity.isAreaHighlighted();
        if(state.shouldHighlightArea){
            state.operatingArea = BlockShape.create(entity.getOperatingArea().move(-pos.getX(), -pos.getY(), -pos.getZ()).inflate(0.05));
            state.pos = pos;
        }
    }

    @Override
    public void submit(SubmitNodeCollector output, State state, RenderContext context){
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.05 * Math.sin((state.renderingTickCount + context.partialTicks()) % 100 / 100d * 2 * Math.PI), 0.5);
        poseStack.mulPose(new Quaternionf().setAngleAxis((state.renderingRotation + state.renderingRotationSpeed * context.partialTicks()) / 3, 0, 1, 0));
        poseStack.translate(-0.5, 0, -0.5);

        ModelFeatureRenderer.CrumblingOverlay breakingOverlay = context.breakingOverlay();
        state.ringRenderState.submit(poseStack, output, context.packedLight(), breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(), 0);

        poseStack.translate(0, state.ringOffset, 0);

        state.ringRenderState.submit(poseStack, output, context.packedLight(), breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(), 0);

        poseStack.popPose();

        if(state.shouldHighlightArea){
            RandomSource random = context.randomSource(state.pos.hashCode());
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
            float alpha = 0.2f;

            RenderUtils.submitShape(output, poseStack, state.operatingArea, red, green, blue, 1, true);
            RenderUtils.submitShapeSides(output, poseStack, state.operatingArea, red, green, blue, alpha, true);
        }
    }

    public static class State {
        final BlockModelRenderState ringRenderState = new BlockModelRenderState();
        double ringOffset;
        int renderingTickCount;
        double renderingRotation, renderingRotationSpeed;
        boolean shouldHighlightArea;
        BlockShape operatingArea;
        BlockPos pos;
    }
}
