package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlockEntity extends BaseBlockEntity implements TickableBlockEntity {

    private static final int SEARCH_BLOCKS_PER_TICK = 5;
    private static final Set<Direction> CAPABILITY_DIRECTIONS = EnumSet.allOf(Direction.class);

    public final ChargerType type;
    private int energy;
    private boolean highlightArea;
    private RedstoneMode redstoneMode = RedstoneMode.DISABLED;
    private boolean isRedstonePowered;
    private int blockSearchX, blockSearchY, blockSearchZ;
    private final Map<BlockPos,Direction> chargeableBlocks = new LinkedHashMap<>();
    public int renderingTickCount = 0;
    public float renderingRotationSpeed, renderingRotation;

    public ChargerBlockEntity(ChargerType type, BlockPos pos, BlockState state){
        super(type.getBlockEntityType(), pos, state);
        this.type = type;
    }

    @SuppressWarnings("UnstableApiUsage")
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
                BlockPos pos = this.worldPosition.offset(offset);

                if(!pos.equals(this.worldPosition)){
                    BlockEntity entity = this.level.getBlockEntity(pos);
                    boolean canAcceptEnergy = false;
                    for(Direction direction : CAPABILITY_DIRECTIONS){
                        if(entity != null && !(entity instanceof ChargerBlockEntity)){
                            EnergyStorage storage = EnergyStorage.SIDED.find(this.level, pos, entity.getBlockState(), entity, direction);
                            if(storage != null && storage.supportsInsertion()){
                                this.chargeableBlocks.put(offset, direction);
                                canAcceptEnergy = true;
                                break;
                            }
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
                for(Map.Entry<BlockPos,Direction> entry : this.chargeableBlocks.entrySet()){
                    BlockEntity tile = this.level.getBlockEntity(this.worldPosition.offset(entry.getKey()));
                    EnergyStorage storage;
                    if(tile != null && (storage = EnergyStorage.SIDED.find(this.level, tile.getBlockPos(), tile.getBlockState(), tile, entry.getValue())) != null){
                        try(Transaction transaction = Transaction.openOuter()){
                            final int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                            int transferred = (int)storage.insert(toTransfer, transaction);
                            if(transferred > 0){
                                spawnParticles = true;
                                this.energy -= transferred;
                                this.dataChanged();
                                transaction.commit();
                                if(this.energy <= 0)
                                    break;
                            }
                        }
                    }else
                        toRemove.add(entry.getKey());
                }
                toRemove.forEach(this.chargeableBlocks::remove);
            }
        }

        // Charge players' items
        if(this.type.canChargePlayers && this.energy > 0){
            List<Player> players = this.level.getEntitiesOfClass(Player.class, this.getOperatingArea());
            loop:
            for(Player player : players){
                int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                // Check Curios/Baubles slots
                if(CommonUtils.isModLoaded("trinkets")){
                    Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                    if(component.isPresent()){
                        for(Tuple<SlotReference,ItemStack> slot : component.get().getAllEquipped()){
                            ItemStack stack = slot.getB();
                            EnergyStorage storage;
                            if(!stack.isEmpty() && (storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack))) != null){
                                try(Transaction transaction = Transaction.openOuter()){
                                    int transferred = (int)storage.insert(toTransfer, transaction);
                                    if(transferred > 0){
                                        spawnParticles = true;
                                        this.energy -= transferred;
                                        this.dataChanged();
                                        transaction.commit();
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
                // Check player inventory
                Inventory inventory = player.getInventory();
                PlayerInventoryStorage inventoryStorage = PlayerInventoryStorage.of(player);
                for(int i = 0; i < inventory.getContainerSize(); i++){
                    ItemStack stack = inventory.getItem(i);
                    if(!stack.isEmpty()){
                        EnergyStorage storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofPlayerSlot(player, inventoryStorage.getSlot(i)));
                        if(storage != null && storage.supportsInsertion()){
                            try(Transaction transaction = Transaction.openOuter()){
                                int transferred = (int)storage.insert(toTransfer, transaction);
                                if(transferred > 0){
                                    spawnParticles = true;
                                    this.energy -= transferred;
                                    this.dataChanged();
                                    transaction.commit();
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
        }

        if(spawnParticles && this.level.isClientSide && this.level.getRandom().nextDouble() <= this.getEnergyFillPercentage()){
            double x = this.worldPosition.getX() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
            double y = this.worldPosition.getY() + 0.7 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
            double z = this.worldPosition.getZ() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
            this.level.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public float getEnergyFillPercentage(){
        return Math.min(1, Math.max(0, (float)this.energy / this.type.capacity.get()));
    }

    public AABB getOperatingArea(){
        return new AABB(this.worldPosition).inflate(this.type.range.get());
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
    protected CompoundTag writeData(){
        CompoundTag compound = new CompoundTag();
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
    public CompoundTag writeItemStackData(){
        CompoundTag compound = this.writeData();
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
    protected void readData(CompoundTag compound){
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

    public int receiveEnergy(int maxReceive, boolean simulate){
        int received = Math.min(maxReceive, Math.min(this.type.capacity.get() - this.energy, this.type.transferRate.get() * 10));
        if(!simulate){
            this.energy += received;
            this.dataChanged();
        }
        return received;
    }

    public int getEnergyStored(){
        return this.energy;
    }

    public int getMaxEnergyStored(){
        return this.type.capacity.get();
    }

    public void setEnergyStored(int energy){
        this.energy = energy;
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
