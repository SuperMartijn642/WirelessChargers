package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import com.supermartijn642.wirelesschargers.compat.ModCompatibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
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

    private static final Set<Direction> CAPABILITY_DIRECTIONS;

    static{
        Set<Direction> directions = new HashSet<>();
        directions.add(null);
        directions.addAll(Arrays.asList(Direction.values()));
        CAPABILITY_DIRECTIONS = Collections.unmodifiableSet(directions);
    }

    private final LazyOptional<IEnergyStorage> capability = LazyOptional.of(() -> this);
    public final ChargerType type;
    private int energy;
    private boolean highlightArea;
    private RedstoneMode redstoneMode = RedstoneMode.DISABLED;
    private boolean isRedstonePowered;
    private int blockSearchX, blockSearchY, blockSearchZ;
    private final Map<BlockPos,Direction> chargeableBlocks = new LinkedHashMap<>();
    public int renderingTickCount = 0;
    public float renderingRotationSpeed, renderingRotation;

    public ChargerBlockEntity(ChargerType type){
        super(type.getBlockEntityType());
        this.type = type;
    }

    @Override
    public void update(){
        if(this.level.isClientSide){
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
                // find blocks with the energy capability
                for(int i = 0; i < SEARCH_BLOCKS_PER_TICK; i++){
                    BlockPos offset = new BlockPos(this.blockSearchX, this.blockSearchY, this.blockSearchZ);
                    BlockPos pos = this.worldPosition.offset(offset);

                    if(!pos.equals(this.worldPosition)){
                        TileEntity entity = this.level.getBlockEntity(pos);
                        boolean canAcceptEnergy = false;
                        for(Direction direction : CAPABILITY_DIRECTIONS){
                            if(entity != null && !(entity instanceof ChargerBlockEntity) && entity.getCapability(CapabilityEnergy.ENERGY, direction).map(IEnergyStorage::canReceive).orElse(false)){
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
                if(this.energy > 0 && this.redstoneMode.canOperate(this.isRedstonePowered)){
                    Set<BlockPos> toRemove = new HashSet<>();
                    for(Map.Entry<BlockPos,Direction> entry : this.chargeableBlocks.entrySet()){
                        TileEntity tile = this.level.getBlockEntity(this.worldPosition.offset(entry.getKey()));
                        LazyOptional<IEnergyStorage> optional;
                        if(tile != null && (optional = tile.getCapability(CapabilityEnergy.ENERGY, entry.getValue())).isPresent()){
                            final int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                            int transferred = optional.map(storage -> storage.receiveEnergy(toTransfer, false)).orElse(0);
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
                List<PlayerEntity> players = this.level.getEntitiesOfClass(PlayerEntity.class, this.getOperatingArea());
                loop:
                for(PlayerEntity player : players){
                    int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                    // Check Curios/Baubles slots
                    IItemHandlerModifiable handler = ModCompatibility.curios.getCuriosStacks(player);
                    for(int i = 0; i < handler.getSlots(); i++){
                        ItemStack stack = handler.getStackInSlot(i);
                        if(!stack.isEmpty()){
                            LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
                            final int max = toTransfer;
                            int transferred = optional.map(storage -> storage.receiveEnergy(max, false)).orElse(0);
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
                    // Check player inventory
                    PlayerInventory inventory = player.inventory;
                    for(int i = 0; i < inventory.getContainerSize(); i++){
                        ItemStack stack = inventory.getItem(i);
                        if(!stack.isEmpty()){
                            LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
                            final int max = toTransfer;
                            int transferred = optional.map(storage -> storage.receiveEnergy(max, false)).orElse(0);
                            if(transferred > 0){
                                inventory.setItem(i, stack);
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

            if(spawnParticles && this.level instanceof ServerWorld && this.level.getRandom().nextDouble() <= this.getEnergyFillPercentage()){
                double x = this.worldPosition.getX() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                double y = this.worldPosition.getY() + 0.7 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                double z = this.worldPosition.getZ() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                ((ServerWorld)this.level).sendParticles(RedstoneParticleData.REDSTONE, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    public float getEnergyFillPercentage(){
        return Math.min(1, Math.max(0, (float)this.energy / this.type.capacity.get()));
    }

    public AxisAlignedBB getOperatingArea(){
        return new AxisAlignedBB(this.worldPosition).inflate(this.type.range.get());
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
    protected CompoundNBT writeData(){
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("energy", this.energy);
        compound.putBoolean("highlightArea", this.highlightArea);
        compound.putInt("redstoneMode", this.redstoneMode.index);
        compound.putBoolean("isRedstonePowered", this.isRedstonePowered);
        if(this.type.canChargeBlocks){
            compound.putInt("blockSearchX", this.blockSearchX);
            compound.putInt("blockSearchY", this.blockSearchX);
            compound.putInt("blockSearchZ", this.blockSearchX);
            int[] arr = new int[this.chargeableBlocks.size() * 4];
            int index = 0;
            for(Map.Entry<BlockPos,Direction> entry : this.chargeableBlocks.entrySet()){
                arr[index] = entry.getKey().getX();
                arr[index + 1] = entry.getKey().getY();
                arr[index + 2] = entry.getKey().getZ();
                arr[index + 3] = entry.getValue() == null ? -1 : entry.getValue().get3DDataValue();
                index++;
            }
            compound.putIntArray("chargeableBlocks", arr);
        }
        return compound;
    }

    @Override
    public CompoundNBT writeItemStackData(){
        CompoundNBT compound = this.writeData();
        if(compound.getInt("energy") <= 0 && compound.getInt("redstoneMode") == 2)
            return null;

        compound.remove("highlightArea");
        compound.remove("isRedstonePowered");
        if(this.type.canChargeBlocks){
            compound.remove("blockSearchX");
            compound.remove("blockSearchY");
            compound.remove("blockSearchZ");
            compound.remove("chargeableBlocks");
        }
        return compound;
    }

    @Override
    protected void readData(CompoundNBT compound){
        this.energy = compound.getInt("energy");
        this.highlightArea = compound.getBoolean("highlightArea");
        this.redstoneMode = RedstoneMode.fromIndex(compound.getInt("redstoneMode"));
        this.isRedstonePowered = compound.getBoolean("isRedstonePowered");
        if(this.type.canChargeBlocks && compound.contains("chargeableBlocks")){
            this.blockSearchX = compound.getInt("blockSearchX");
            this.blockSearchY = compound.getInt("blockSearchY");
            this.blockSearchZ = compound.getInt("blockSearchZ");
            int[] arr = compound.getIntArray("chargeableBlocks");
            this.chargeableBlocks.clear();
            for(int i = 0; i < arr.length / 4; i++)
                this.chargeableBlocks.put(
                    new BlockPos(arr[i], arr[i + 1], arr[i + 2]),
                    arr[i + 3] == -1 ? null : Direction.from3DDataValue(arr[i + 3])
                );
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
        if(cap == CapabilityEnergy.ENERGY && side != Direction.UP)
            return this.capability.cast();
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
    public void onChunkUnloaded(){
        this.capability.invalidate();
        super.onChunkUnloaded();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){
        return this.highlightArea ? this.getOperatingArea() : new AxisAlignedBB(this.worldPosition);
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
