package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlock extends BaseBlock implements EntityBlock, SimpleWaterloggedBlock {

    public final ChargerType type;

    public ChargerBlock(ChargerType type){
        super(type.getRegistryName(), true, Properties.of(Material.METAL, DyeColor.GRAY).strength(2f));
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return this.type.createTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType){
        return ForgeRegistries.BLOCK_ENTITIES.getKey(entityType).getNamespace().equals("wirelesschargers") ?
            (world2, pos, state2, entity) -> ((ChargerBlockEntity)entity).tick() : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult){
        if(world.isClientSide)
            ClientProxy.openChargerScreen(TextComponents.block(this).get(), pos);
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag){
        Component range = TextComponents.number(this.type.range.get() * 2 + 1).color(ChatFormatting.GOLD).get();

        // blocks
        if(this.type.canChargeBlocks){
            list.add(TextComponents.translation("wirelesschargers.charger.info.blocks", range).color(ChatFormatting.YELLOW).get());

            Component transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(ChatFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_blocks", transferRate).color(ChatFormatting.GRAY).get());
        }

        // players
        if(this.type.canChargePlayers){
            list.add(TextComponents.translation("wirelesschargers.charger.info.players", range).color(ChatFormatting.YELLOW).get());

            Component transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(ChatFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_players", transferRate).color(ChatFormatting.GRAY).get());
        }

        // stored energy
        int energy = stack.getTag() == null ? 0 : stack.getTag().getCompound("tileData").getInt("energy");
        if(energy > 0){
            Component energyText = TextComponents.string(EnergyFormat.formatEnergy(energy)).color(ChatFormatting.GOLD).get();
            Component capacity = TextComponents.string(EnergyFormat.formatEnergy(this.type.capacity.get())).color(ChatFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(ChatFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.stored_energy", energyText, capacity).color(ChatFormatting.GRAY).get());
        }

        // redstone mode
        int redstoneMode = stack.getTag() == null ? 2 : stack.getTag().getCompound("tileData").contains("redstoneMode") ? stack.getTag().getCompound("tileData").getInt("redstoneMode") : 2;
        if(redstoneMode != 2){
            ChargerBlockEntity.RedstoneMode mode = ChargerBlockEntity.RedstoneMode.fromIndex(redstoneMode);
            Component value = TextComponents.translation("wirelesschargers.screen.redstone_" + mode.name().toLowerCase(Locale.ROOT)).color(ChatFormatting.GOLD).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.redstone_mode", value).color(ChatFormatting.GRAY).get());
        }
    }

//    @Override TODO put this back once https://github.com/MinecraftForge/MinecraftForge/pull/7927 gets merged
//    public boolean canRedstoneConnectTo(BlockState state, @Nullable Direction side){
//        return side != Direction.UP;
//    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean p_220069_6_){
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof ChargerBlockEntity)
            ((ChargerBlockEntity)entity).setRedstonePowered(world.hasNeighborSignal(pos));
    }
}
