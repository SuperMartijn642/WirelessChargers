package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.EntityHoldingBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlock extends BaseBlock implements EntityHoldingBlock, SimpleWaterloggedBlock {

    public final ChargerType type;

    public ChargerBlock(ChargerType type){
        super(true, BlockProperties.create().mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).destroyTime(2).explosionResistance(2));
        this.type = type;

        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
        return this.type.modelType.outlineShape.getUnderlying();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_){
        return this.type.modelType.collisionShape.getUnderlying();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state){
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockEntity createNewBlockEntity(BlockPos pos, BlockState state){
        return this.type.createBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    protected InteractionFeedback interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, Direction hitSide, Vec3 hitLocation){
        if(level.isClientSide)
            WirelessChargersClient.openChargerScreen(TextComponents.block(this).get(), level, pos);
        return InteractionFeedback.SUCCESS;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, Consumer<Component> info, boolean advanced){
        Component range = TextComponents.number(this.type.range.get() * 2 + 1).color(ChatFormatting.GOLD).get();

        // blocks
        if(this.type.canChargeBlocks){
            info.accept(TextComponents.translation("wirelesschargers.charger.info.blocks", range).color(ChatFormatting.YELLOW).get());

            Component transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(ChatFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_blocks", transferRate).color(ChatFormatting.GRAY).get());
        }

        // players
        if(this.type.canChargePlayers){
            info.accept(TextComponents.translation("wirelesschargers.charger.info.players", range).color(ChatFormatting.YELLOW).get());

            Component transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(ChatFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_players", transferRate).color(ChatFormatting.GRAY).get());
        }

        // stored energy
        CompoundTag tag = stack.get(BaseBlock.TILE_DATA);
        int energy = tag == null ? 0 : tag.getInt("energy");
        if(energy > 0){
            Component energyText = TextComponents.string(EnergyFormat.formatEnergy(energy)).color(ChatFormatting.GOLD).get();
            Component capacity = TextComponents.string(EnergyFormat.formatEnergy(this.type.capacity.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(ChatFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.stored_energy", energyText, capacity).color(ChatFormatting.GRAY).get());
        }

        // redstone mode
        int redstoneMode = tag == null || !tag.contains("redstoneMode") ? 2 : tag.getInt("redstoneMode");
        if(redstoneMode != 2){
            ChargerBlockEntity.RedstoneMode mode = ChargerBlockEntity.RedstoneMode.fromIndex(redstoneMode);
            Component value = TextComponents.translation("wirelesschargers.screen.redstone_" + mode.name().toLowerCase(Locale.ROOT)).color(ChatFormatting.GOLD).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.redstone_mode", value).color(ChatFormatting.GRAY).get());
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction){
        return direction != Direction.UP;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean p_220069_6_){
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof ChargerBlockEntity)
            ((ChargerBlockEntity)entity).setRedstonePowered(world.hasNeighborSignal(pos));
    }
}
