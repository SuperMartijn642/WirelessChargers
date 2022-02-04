package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.block.BaseTileEntity;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlockEntity extends BaseTileEntity implements ITickable, IEnergyStorage {

    private static final int SEARCH_BLOCKS_PER_TICK = 5;

    private static final Set<EnumFacing> CAPABILITY_DIRECTIONS;

    static{
        Set<EnumFacing> directions = new HashSet<>();
        directions.add(null);
        directions.addAll(Arrays.asList(EnumFacing.values()));
        CAPABILITY_DIRECTIONS = Collections.unmodifiableSet(directions);
    }

    public static class BasicBlockChargerEntity extends ChargerBlockEntity {

        public BasicBlockChargerEntity(){
            super(ChargerType.BASIC_WIRELESS_BLOCK_CHARGER);
        }
    }

    public static class AdvancedBlockChargerEntity extends ChargerBlockEntity {

        public AdvancedBlockChargerEntity(){
            super(ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER);
        }
    }

    public static class BasicPlayerChargerEntity extends ChargerBlockEntity {

        public BasicPlayerChargerEntity(){
            super(ChargerType.BASIC_WIRELESS_PLAYER_CHARGER);
        }
    }

    public static class AdvancedPlayerChargerEntity extends ChargerBlockEntity {

        public AdvancedPlayerChargerEntity(){
            super(ChargerType.ADVANCED_WIRELESS_PLAYER_CHARGER);
        }
    }

    public final ChargerType type;
    private int energy;
    private boolean highlightArea;
    private RedstoneMode redstoneMode = RedstoneMode.DISABLED;
    private boolean isRedstonePowered;
    private int blockSearchX, blockSearchY, blockSearchZ;
    private final Map<BlockPos,EnumFacing> chargeableBlocks = new LinkedHashMap<>();
    public int renderingTickCount = 0;
    public float renderingRotationSpeed, renderingRotation;

    public ChargerBlockEntity(ChargerType type){
        super();
        this.type = type;
    }

    @Override
    public void update(){
        this.renderingTickCount++;

        if(!this.redstoneMode.canOperate(this.isRedstonePowered)){
            this.renderingRotationSpeed = Math.max(0, this.renderingRotationSpeed - 0.02f);
            this.renderingRotation += this.renderingRotationSpeed;
            return;
        }

        this.renderingRotationSpeed = Math.min(this.renderingRotationSpeed + 0.02f, this.getEnergyFillPercentage());
        this.renderingRotation += this.renderingRotationSpeed;

        boolean spawnParticles = false;

        if(this.type.canChargeBlocks){
            // find blocks with the energy capability
            for(int i = 0; i < SEARCH_BLOCKS_PER_TICK; i++){
                BlockPos offset = new BlockPos(this.blockSearchX, this.blockSearchY, this.blockSearchZ);
                BlockPos pos = this.pos.add(offset);

                if(!pos.equals(this.pos)){
                    TileEntity entity = this.world.getTileEntity(pos);
                    boolean canAcceptEnergy = false;
                    for(EnumFacing direction : CAPABILITY_DIRECTIONS){
                        IEnergyStorage capability;
                        if(entity != null && !(entity instanceof ChargerBlockEntity) && (capability = entity.getCapability(CapabilityEnergy.ENERGY, direction)) != null && capability.canReceive()){
                            this.chargeableBlocks.put(offset, direction);
                            canAcceptEnergy = true;
                            break;
                        }
                    }
                    if(!canAcceptEnergy)
                        this.chargeableBlocks.remove(offset);
                }

                int range = this.type.range.get();
                this.blockSearchX++;
                if(this.blockSearchX > range){
                    this.blockSearchX = -range;
                    this.blockSearchZ++;
                    if(this.blockSearchZ > range){
                        this.blockSearchZ = -range;
                        this.blockSearchY++;
                        if(this.blockSearchY > range)
                            this.blockSearchY = -range;
                    }
                }
            }

            // charge block in the list
            if(this.energy > 0){
                Set<BlockPos> toRemove = new HashSet<>();
                for(Map.Entry<BlockPos,EnumFacing> entry : this.chargeableBlocks.entrySet()){
                    TileEntity tile = this.world.getTileEntity(this.pos.add(entry.getKey()));
                    IEnergyStorage capability;
                    if(tile != null && (capability = tile.getCapability(CapabilityEnergy.ENERGY, entry.getValue())) != null){
                        final int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                        int transferred = capability.receiveEnergy(toTransfer, false);
                        if(transferred > 0){
                            spawnParticles = true;
                            this.energy -= transferred;
                            this.dataChanged();
                            if(this.energy <= 0)
                                break;
                        }
                    }else
                        toRemove.add(entry.getKey());
                }
                toRemove.forEach(this.chargeableBlocks::remove);
            }
        }

        // Charge players' items
        if(this.type.canChargePlayers && this.energy > 0){
            List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getOperatingArea());
            loop:
            for(EntityPlayer player : players){
                int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                // Check Curios/Baubles slots
                IItemHandlerModifiable handler = ModCompatibility.baubles.getCuriosStacks(player);
                for(int i = 0; i < handler.getSlots(); i++){
                    ItemStack stack = handler.getStackInSlot(i);
                    if(!stack.isEmpty()){
                        IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if(capability != null){
                            final int max = toTransfer;
                            int transferred = capability.receiveEnergy(max, false);
                            if(transferred > 0){
                                handler.setStackInSlot(i, stack);
                                spawnParticles = true;
                                this.energy -= transferred;
                                this.dataChanged();
                                if(this.energy <= 0)
                                    break loop;
                                toTransfer -= transferred;
                                if(toTransfer <= 0)
                                    continue loop;
                            }
                        }
                    }
                }
                InventoryPlayer inventory = player.inventory;
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(!stack.isEmpty()){
                        IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if(capability != null){
                            final int max = toTransfer;
                            int transferred = capability.receiveEnergy(max, false);
                            if(transferred > 0){
                                inventory.setInventorySlotContents(i, stack);
                                spawnParticles = true;
                                this.energy -= transferred;
                                this.dataChanged();
                                if(this.energy <= 0)
                                    break loop;
                                toTransfer -= transferred;
                                if(toTransfer <= 0)
                                    continue loop;
                            }
                        }
                    }
                }
            }
        }

        if(spawnParticles && this.world.isRemote && this.world.rand.nextDouble() <= this.getEnergyFillPercentage()){
            double x = this.pos.getX() + 0.5 + this.world.rand.nextFloat() * 0.8 - 0.4;
            double y = this.pos.getY() + 0.7 + this.world.rand.nextFloat() * 0.8 - 0.4;
            double z = this.pos.getZ() + 0.5 + this.world.rand.nextFloat() * 0.8 - 0.4;
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public float getEnergyFillPercentage(){
        return Math.min(1, Math.max(0, (float)this.energy / this.type.capacity.get()));
    }

    public AxisAlignedBB getOperatingArea(){
        return new AxisAlignedBB(this.pos).grow(this.type.range.get());
    }

    public void setRedstonePowered(boolean powered){
        if(this.isRedstonePowered != powered){
            this.isRedstonePowered = powered;
            this.dataChanged();
        }
    }

    public RedstoneMode getRedstoneMode(){
        return this.redstoneMode;
    }

    public void cycleRedstoneMode(){
        this.redstoneMode = RedstoneMode.fromIndex((this.redstoneMode.index + 1) % RedstoneMode.values().length);
        this.dataChanged();
    }

    public boolean isAreaHighlighted(){
        return this.highlightArea;
    }

    public void toggleHighlightArea(){
        this.highlightArea = !this.highlightArea;
        this.dataChanged();
    }

    @Override
    protected NBTTagCompound writeData(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("energy", this.energy);
        compound.setBoolean("highlightArea", this.highlightArea);
        compound.setInteger("redstoneMode", this.redstoneMode.index);
        compound.setBoolean("isRedstonePowered", this.isRedstonePowered);
        if(this.type.canChargeBlocks){
            compound.setInteger("blockSearchX", this.blockSearchX);
            compound.setInteger("blockSearchY", this.blockSearchX);
            compound.setInteger("blockSearchZ", this.blockSearchX);
            int[] arr = new int[this.chargeableBlocks.size() * 4];
            int index = 0;
            for(Map.Entry<BlockPos,EnumFacing> entry : this.chargeableBlocks.entrySet()){
                arr[index] = entry.getKey().getX();
                arr[index + 1] = entry.getKey().getY();
                arr[index + 2] = entry.getKey().getZ();
                arr[index + 3] = entry.getValue() == null ? -1 : entry.getValue().getIndex();
                index++;
            }
            compound.setIntArray("chargeableBlocks", arr);
        }
        return compound;
    }

    @Override
    public NBTTagCompound writeItemStackData(){
        NBTTagCompound compound = this.writeData();
        if(compound.getInteger("energy") <= 0 && compound.getInteger("redstoneMode") == 2)
            return null;

        compound.removeTag("highlightArea");
        compound.removeTag("isRedstonePowered");
        if(this.type.canChargeBlocks){
            compound.removeTag("blockSearchX");
            compound.removeTag("blockSearchY");
            compound.removeTag("blockSearchZ");
            compound.removeTag("chargeableBlocks");
        }
        return compound;
    }

    @Override
    protected void readData(NBTTagCompound compound){
        this.energy = compound.getInteger("energy");
        this.highlightArea = compound.getBoolean("highlightArea");
        this.redstoneMode = RedstoneMode.fromIndex(compound.getInteger("redstoneMode"));
        this.isRedstonePowered = compound.getBoolean("isRedstonePowered");
        if(this.type.canChargeBlocks && compound.hasKey("chargeableBlocks")){
            this.blockSearchX = compound.getInteger("blockSearchX");
            this.blockSearchY = compound.getInteger("blockSearchY");
            this.blockSearchZ = compound.getInteger("blockSearchZ");
            int[] arr = compound.getIntArray("chargeableBlocks");
            this.chargeableBlocks.clear();
            for(int i = 0; i < arr.length / 4; i++)
                this.chargeableBlocks.put(
                    new BlockPos(arr[i], arr[i + 1], arr[i + 2]),
                    arr[i + 3] == -1 ? null : EnumFacing.getFront(arr[i + 3])
                );
        }
    }

    @Override
    public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing side){
        if(cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP)
            return true;
        return super.hasCapability(cap, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side){
        if(cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP)
            return CapabilityEnergy.ENERGY.cast(this);
        return super.getCapability(cap, side);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate){
        int received = Math.min(maxReceive, Math.min(this.type.capacity.get() - this.energy, this.type.transferRate.get() * 10));
        if(!simulate){
            this.energy += received;
            this.dataChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate){
        return 0;
    }

    @Override
    public int getEnergyStored(){
        return this.energy;
    }

    @Override
    public int getMaxEnergyStored(){
        return this.type.capacity.get();
    }

    @Override
    public boolean canExtract(){
        return false;
    }

    @Override
    public boolean canReceive(){
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return this.highlightArea ? this.getOperatingArea() : new AxisAlignedBB(this.pos);
    }

    public enum RedstoneMode {
        HIGH(0), LOW(1), DISABLED(2);

        public final int index;

        RedstoneMode(int index){
            this.index = index;
        }

        public boolean canOperate(boolean isPowered){
            return this == DISABLED || (isPowered ? this == HIGH : this == LOW);
        }

        public static RedstoneMode fromIndex(int index){
            for(RedstoneMode mode : values())
                if(mode.index == index)
                    return mode;
            return DISABLED;
        }
    }
}
