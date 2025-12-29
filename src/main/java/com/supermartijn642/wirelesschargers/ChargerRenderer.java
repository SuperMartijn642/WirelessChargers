package com.supermartijn642.wirelesschargers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.render.CustomBlockEntityRenderer;
import com.supermartijn642.core.render.RenderUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;

import java.util.Random;

/**
 * Created 7/9/2021 by SuperMartijn642
 */
public class ChargerRenderer implements CustomBlockEntityRenderer<ChargerBlockEntity,ChargerRenderer.State> {

    private static final Random RANDOM = new Random();

    @Override
    public State createStateHolder(){
        return new State();
    }

    @Override
    public void updateState(State state, ChargerBlockEntity entity, UpdateContext context){
        state.ringModel = WirelessChargersClient.getRingModel(entity.type);
        state.ringOffset = entity.type.modelType.ringYOffset;
        state.renderingTickCount = entity.renderingTickCount;
        state.renderingRotation = entity.renderingRotation;
        state.renderingRotationSpeed = entity.renderingRotationSpeed;
        state.shouldHighlightArea = entity.isAreaHighlighted();
        if(state.shouldHighlightArea){
            BlockPos pos = entity.getBlockPos();
            state.operatingArea = entity.getOperatingArea().move(-pos.getX(), -pos.getY(), -pos.getZ()).inflate(0.05);
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
        output.submitBlockModel(poseStack, RenderTypes.solidMovingBlock(), state.ringModel, 1, 1, 1, context.packedLight(), breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(), 0);

        poseStack.translate(0, state.ringOffset, 0);

        output.submitBlockModel(poseStack, RenderTypes.solidMovingBlock(), state.ringModel, 1, 1, 1, context.packedLight(), breakingOverlay == null ? OverlayTexture.NO_OVERLAY : breakingOverlay.progress(), 0);

        poseStack.popPose();

        if(state.shouldHighlightArea){
            RANDOM.setSeed(state.pos.hashCode());
            float red = RANDOM.nextFloat();
            float green = RANDOM.nextFloat();
            float blue = RANDOM.nextFloat();
            float alpha = 0.2f;

            RenderUtils.renderBox(poseStack, state.operatingArea, red, green, blue, true);
            RenderUtils.renderBoxSides(poseStack, state.operatingArea, red, green, blue, alpha, true);
        }
    }

    public static class State {
        BlockStateModel ringModel;
        double ringOffset;
        int renderingTickCount;
        double renderingRotation, renderingRotationSpeed;
        boolean shouldHighlightArea;
        AABB operatingArea;
        BlockPos pos;
    }
}
