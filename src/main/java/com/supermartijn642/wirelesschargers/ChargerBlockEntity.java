package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
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
public class ChargerBlockEntity extends BaseBlockEntity implements TickableBlockEntity, IEnergyStorage {

    private static final int SEARCH_BLOCKS_PER_TICK = 5;

    private static final List<EnumFacing> CAPABILITY_DIRECTIONS;

    static{
        List<EnumFacing> directions = new ArrayList<>(7);
        directions.add(null);
        directions.addAll(Arrays.asList(EnumFacing.values()));
        CAPABILITY_DIRECTIONS = Collections.unmodifiableList(directions);
    }

    public final ChargerType type;
    private int energy;
    private boolean highlightArea;
    private RedstoneMode redstoneMode = RedstoneMode.DISABLED;
    private boolean isRedstonePowered;
    private int blockSearchX, blockSearchY, blockSearchZ;
    private final Map<BlockPos,List<EnumFacing>> chargeableBlocks = new LinkedHashMap<>();
    public int renderingTickCount = 0;
    public float renderingRotationSpeed, renderingRotation;

    public ChargerBlockEntity(ChargerType type){
        super(type.getBlockEntityType());
        this.type = type;
    }

    @Override
    public void update(){
        if(this.world.isRemote){
            this.renderingTickCount++;
            if(!this.redstoneMode.canOperate(this.isRedstonePowered)){
                this.renderingRotationSpeed = Math.max(0, this.renderingRotationSpeed - 0.02f);
                this.renderingRotation += this.renderingRotationSpeed;
                return;
            }
            this.renderingRotationSpeed = Math.min(this.renderingRotationSpeed + 0.02f, this.getEnergyFillPercentage());
            this.renderingRotation += this.renderingRotationSpeed;
        }else{
            boolean spawnParticles = false;
            if(this.type.canChargeBlocks){
                List<EnumFacing> chargeableDirections = new ArrayList<>(CAPABILITY_DIRECTIONS.size());
                // find blocks with the energy capability
                for(int i = 0; i < SEARCH_BLOCKS_PER_TICK; i++){
                    BlockPos offset = new BlockPos(this.blockSearchX, this.blockSearchY, this.blockSearchZ);
                    BlockPos pos = this.pos.add(offset);

                    if(!pos.equals(this.pos)){
                        TileEntity entity = this.world.getTileEntity(pos);
                        if(entity != null && !(entity instanceof ChargerBlockEntity)){
                            for(EnumFacing direction : CAPABILITY_DIRECTIONS){
                                IEnergyStorage storage = entity.getCapability(CapabilityEnergy.ENERGY, direction);
                                if(storage != null && storage.canReceive())
                                    chargeableDirections.add(direction);
                            }
                            if(chargeableDirections.isEmpty())
                                this.chargeableBlocks.remove(offset);
                            else{
                                if(!chargeableDirections.equals(this.chargeableBlocks.get(offset)))
                                    this.chargeableBlocks.put(offset, new ArrayList<>(chargeableDirections));
                                chargeableDirections.clear();
                            }
                        }
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
                if(this.energy > 0 && this.redstoneMode.canOperate(this.isRedstonePowered)){
                    Set<BlockPos> toRemove = new HashSet<>();
                    for(Map.Entry<BlockPos,List<EnumFacing>> entry : this.chargeableBlocks.entrySet()){
                        TileEntity entity = this.world.getTileEntity(this.pos.add(entry.getKey()));
                        if(entity != null && !(entity instanceof ChargerBlockEntity)){
                            final int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                            int transferred = 0;
                            for(EnumFacing direction : entry.getValue()){
                                IEnergyStorage storage = entity.getCapability(CapabilityEnergy.ENERGY, direction);
                                if(storage != null && storage.canReceive()){
                                    transferred += storage.receiveEnergy(toTransfer - transferred, false);
                                    if(transferred >= toTransfer)
                                        break;
                                }else{
                                    toRemove.add(entry.getKey());
                                    break;
                                }
                            }
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
            if(this.type.canChargePlayers && this.energy > 0 && this.redstoneMode.canOperate(this.isRedstonePowered)){
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

            if(spawnParticles && this.world instanceof WorldServer && this.world.rand.nextDouble() <= this.getEnergyFillPercentage()){
                double x = this.pos.getX() + 0.5 + this.world.rand.nextFloat() * 0.8 - 0.4;
                double y = this.pos.getY() + 0.7 + this.world.rand.nextFloat() * 0.8 - 0.4;
                double z = this.pos.getZ() + 0.5 + this.world.rand.nextFloat() * 0.8 - 0.4;
                ((WorldServer)this.world).spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 1, 0, 0, 0, 0d);
            }
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
            for(Map.Entry<BlockPos,List<EnumFacing>> entry : this.chargeableBlocks.entrySet()){
                arr[index] = entry.getKey().getX();
                arr[index + 1] = entry.getKey().getY();
                arr[index + 2] = entry.getKey().getZ();
                int sides = entry.getValue().contains(null) ? 1 : 0;
                for(EnumFacing side : entry.getValue())
                    sides |= 1 << (side == null ? 0 : side.ordinal() + 1);
                arr[index + 3] = sides;
                index += 4;
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
            this.chargeableBlocks.clear();
            int[] arr = compound.getIntArray("chargeableBlocks");
            List<EnumFacing> directions = new ArrayList<>(CAPABILITY_DIRECTIONS.size());
            for(int i = 0; i < arr.length / 4; i++){
                BlockPos pos = new BlockPos(arr[i * 4], arr[i * 4 + 1], arr[i * 4 + 2]);
                int sides = arr[i * 4 + 3];
                for(EnumFacing side : CAPABILITY_DIRECTIONS){
                    if(((sides >> (side == null ? 0 : side.ordinal() + 1)) & 1) == 1)
                        directions.add(side);
                }
                this.chargeableBlocks.put(pos, new ArrayList<>(directions));
                directions.clear();
            }
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
        int received = Math.min(maxReceive, Math.min(this.type.capacity.get() - this.energy, this.type.transferRate.get() * 100));
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
