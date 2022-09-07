package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.core.block.BlockProperties;
import com.supermartijn642.core.block.EntityHoldingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlock extends BaseBlock implements EntityHoldingBlock {

    public final ChargerType type;

    public ChargerBlock(ChargerType type){
        super(true, BlockProperties.create(Material.IRON, EnumDyeColor.GRAY).destroyTime(2).explosionResistance(2));
        this.type = type;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos){
        return this.type.modelType.outlineShape.simplify();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos){
        return this.type.modelType.collisionShape.simplify();
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
        for(AxisAlignedBB box : this.type.modelType.collisionShape.toBoxes())
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

    @Override
    public TileEntity createNewBlockEntity(){
        return this.type.createBlockEntity();
    }

    @Override
    protected InteractionFeedback interact(IBlockState state, World level, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation){
        if(level.isRemote)
            WirelessChargersClient.openChargerScreen(TextComponents.block(this).get(), level, pos);
        return InteractionFeedback.SUCCESS;
    }

    @Override
    protected void appendItemInformation(ItemStack stack, IBlockAccess level, Consumer<ITextComponent> info, boolean advanced){
        ITextComponent range = TextComponents.number(this.type.range.get() * 2 + 1).color(TextFormatting.GOLD).get();

        // blocks
        if(this.type.canChargeBlocks){
            info.accept(TextComponents.translation("wirelesschargers.charger.info.blocks", range).color(TextFormatting.YELLOW).get());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_blocks", transferRate).color(TextFormatting.GRAY).get());
        }

        // players
        if(this.type.canChargePlayers){
            info.accept(TextComponents.translation("wirelesschargers.charger.info.players", range).color(TextFormatting.YELLOW).get());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_players", transferRate).color(TextFormatting.GRAY).get());
        }

        // stored energy
        int energy = stack.getTagCompound() == null ? 0 : stack.getTagCompound().getCompoundTag("tileData").getInteger("energy");
        if(energy > 0){
            ITextComponent energyText = TextComponents.string(EnergyFormat.formatEnergy(energy)).color(TextFormatting.GOLD).get();
            ITextComponent capacity = TextComponents.string(EnergyFormat.formatEnergy(this.type.capacity.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(TextFormatting.GRAY).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.stored_energy", energyText, capacity).color(TextFormatting.GRAY).get());
        }

        // redstone mode
        int redstoneMode = stack.getTagCompound() == null ? 2 : stack.getTagCompound().getCompoundTag("tileData").hasKey("redstoneMode") ? stack.getTagCompound().getCompoundTag("tileData").getInteger("redstoneMode") : 2;
        if(redstoneMode != 2){
            ChargerBlockEntity.RedstoneMode mode = ChargerBlockEntity.RedstoneMode.fromIndex(redstoneMode);
            ITextComponent value = TextComponents.translation("wirelesschargers.screen.redstone_" + mode.name().toLowerCase(Locale.ROOT)).color(TextFormatting.GOLD).get();
            info.accept(TextComponents.translation("wirelesschargers.charger.info.redstone_mode", value).color(TextFormatting.GRAY).get());
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing direction){
        return direction != EnumFacing.UP;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos){
        TileEntity entity = world.getTileEntity(pos);
        if(entity instanceof ChargerBlockEntity)
            ((ChargerBlockEntity)entity).setRedstonePowered(world.isBlockPowered(pos));
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return face.getAxis() == EnumFacing.Axis.Y ?
            face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED :
            BlockFaceShape.CENTER;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
}
