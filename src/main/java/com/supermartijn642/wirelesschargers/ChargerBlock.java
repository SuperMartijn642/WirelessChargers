package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.EnergyFormat;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlock extends BaseBlock {

    public static final PropertyBool RING = PropertyBool.create("ring");

    public final ChargerType type;

    public ChargerBlock(ChargerType type){
        super(type.getRegistryName(), true, Properties.create(Material.IRON, EnumDyeColor.GRAY).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2f));
        this.type = type;
        this.setUnlocalizedName("wirelesschargers." + type.getRegistryName());
        this.setCreativeTab(WirelessChargers.GROUP);
        this.setDefaultState(this.getDefaultState().withProperty(RING, false));
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
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return this.type.createTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(world.isRemote)
            ClientProxy.openChargerScreen(TextComponents.block(this).get(), pos);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag){
        ITextComponent range = TextComponents.number(this.type.range.get() * 2 + 1).color(TextFormatting.GOLD).get();

        // blocks
        if(this.type.canChargeBlocks){
            list.add(TextComponents.translation("wirelesschargers.charger.info.blocks", range).color(TextFormatting.YELLOW).format());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_blocks", transferRate).color(TextFormatting.GRAY).format());
        }

        // players
        if(this.type.canChargePlayers){
            list.add(TextComponents.translation("wirelesschargers.charger.info.players", range).color(TextFormatting.YELLOW).format());

            ITextComponent transferRate = TextComponents.string(EnergyFormat.formatEnergy(this.type.transferRate.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnitPerTick()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.transfer_rate_players", transferRate).color(TextFormatting.GRAY).format());
        }

        // stored energy
        int energy = stack.getTagCompound() == null ? 0 : stack.getTagCompound().getCompoundTag("tileData").getInteger("energy");
        if(energy > 0){
            ITextComponent energyText = TextComponents.string(EnergyFormat.formatEnergy(energy)).color(TextFormatting.GOLD).get();
            ITextComponent capacity = TextComponents.string(EnergyFormat.formatEnergy(this.type.capacity.get())).color(TextFormatting.GOLD).string(" " + EnergyFormat.formatUnit()).color(TextFormatting.GRAY).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.stored_energy", energyText, capacity).color(TextFormatting.GRAY).format());
        }

        // redstone mode
        int redstoneMode = stack.getTagCompound() == null ? 2 : stack.getTagCompound().getCompoundTag("tileData").hasKey("redstoneMode") ? stack.getTagCompound().getCompoundTag("tileData").getInteger("redstoneMode") : 2;
        if(redstoneMode != 2){
            ChargerBlockEntity.RedstoneMode mode = ChargerBlockEntity.RedstoneMode.fromIndex(redstoneMode);
            ITextComponent value = TextComponents.translation("wirelesschargers.screen.redstone_" + mode.name().toLowerCase(Locale.ROOT)).color(TextFormatting.GOLD).get();
            list.add(TextComponents.translation("wirelesschargers.charger.info.redstone_mode", value).color(TextFormatting.GRAY).format());
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side){
        return side != EnumFacing.UP;
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

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, RING);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;
    }
}
