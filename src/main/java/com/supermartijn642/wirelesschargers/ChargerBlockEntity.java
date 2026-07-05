package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.block.BaseBlockEntity;
import com.supermartijn642.core.block.TickableBlockEntity;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

/**
 * Created 7/8/2021 by SuperMartijn642
 */
public class ChargerBlockEntity extends BaseBlockEntity implements TickableBlockEntity, EnergyStorage {

    private static final int SEARCH_BLOCKS_PER_TICK = 5;
    private static final Set<Direction> CAPABILITY_DIRECTIONS = EnumSet.allOf(Direction.class);

    private final SnapshotParticipant<Integer> snapshotParticipant = new SnapshotParticipant<>() {
        @Override
        protected Integer createSnapshot(){
            return ChargerBlockEntity.this.energy;
        }

        @Override
        protected void readSnapshot(Integer snapshot){
            ChargerBlockEntity.this.energy = snapshot;
        }

        @Override
        protected void onFinalCommit(){
            ChargerBlockEntity.this.dataChanged();
        }
    };

    public final ChargerType type;
    private int energy;
    private boolean highlightArea;
    private RedstoneMode redstoneMode = RedstoneMode.DISABLED;
    private boolean isRedstonePowered;
    private int blockSearchX, blockSearchY, blockSearchZ;
    private final Map<BlockPos,List<Direction>> chargeableBlocks = new LinkedHashMap<>();
    public int renderingTickCount = 0;
    public float renderingRotationSpeed, renderingRotation;

    public ChargerBlockEntity(ChargerType type, BlockPos pos, BlockState state){
        super(type.getBlockEntityType(), pos, state);
        this.type = type;
    }

    @Override
    public void update(){
        if(this.level.isClientSide()){
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
                List<Direction> chargeableDirections = new ArrayList<>(CAPABILITY_DIRECTIONS.size());
                // find blocks with the energy capability
                for(int i = 0; i < SEARCH_BLOCKS_PER_TICK; i++){
                    BlockPos offset = new BlockPos(this.blockSearchX, this.blockSearchY, this.blockSearchZ);
                    BlockPos pos = this.worldPosition.offset(offset);

                    if(!pos.equals(this.worldPosition)){
                        BlockEntity entity = this.level.getBlockEntity(pos);
                        if(entity != null && !(entity instanceof ChargerBlockEntity)){
                            for(Direction direction : CAPABILITY_DIRECTIONS){
                                EnergyStorage storage = EnergyStorage.SIDED.find(this.level, pos, entity.getBlockState(), entity, direction);
                                if(storage != null && storage.supportsInsertion())
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
                    for(Map.Entry<BlockPos,List<Direction>> entry : this.chargeableBlocks.entrySet()){
                        BlockEntity entity = this.level.getBlockEntity(this.worldPosition.offset(entry.getKey()));
                        if(entity != null && !(entity instanceof ChargerBlockEntity)){
                            final int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                            int transferred = 0;
                            for(Direction direction : entry.getValue()){
                                EnergyStorage storage = EnergyStorage.SIDED.find(this.level, entity.getBlockPos(), entity.getBlockState(), entity, direction);
                                if(storage != null && storage.supportsInsertion()){
                                    try(Transaction transaction = Transaction.openOuter()){
                                        transferred += (int)storage.insert(toTransfer - transferred, transaction);
                                        transaction.commit();
                                    }
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
                List<Player> players = this.level.getEntitiesOfClass(Player.class, this.getOperatingArea());
                loop:
                for(Player player : players){
                    int toTransfer = Math.min(this.energy, this.type.transferRate.get());
                    // Check Curios/Baubles slots
                    if(CommonUtils.isModLoaded("trinkets")){
                        TrinketAttachment component = TrinketsApi.getAttachment(player);
                        if(component != null){
                            for(TrinketSlotAccess slot : component.allEquipped(false)){
                                ItemStack stack = slot.get();
                                if(stack.isEmpty())
                                    continue;
                                EnergyStorage storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(new TrinketsSlotStorage(slot)));
                                if(storage != null){
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
                        EnergyStorage storage = ContainerItemContext.ofPlayerSlot(player, inventoryStorage.getSlot(i)).find(EnergyStorage.ITEM);
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

            if(spawnParticles && this.level instanceof ServerLevel && this.level.getRandom().nextDouble() <= this.getEnergyFillPercentage()){
                double x = this.worldPosition.getX() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                double y = this.worldPosition.getY() + 0.7 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                double z = this.worldPosition.getZ() + 0.5 + this.level.getRandom().nextFloat() * 0.8 - 0.4;
                ((ServerLevel)this.level).sendParticles(DustParticleOptions.REDSTONE, x, y, z, 1, 0, 0, 0, 0);
            }
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
    protected void writeData(ValueOutput output){
        output.putInt("energy", this.energy);
        output.putBoolean("highlightArea", this.highlightArea);
        output.putInt("redstoneMode", this.redstoneMode.index);
        output.putBoolean("isRedstonePowered", this.isRedstonePowered);
        if(this.type.canChargeBlocks){
            output.putInt("blockSearchX", this.blockSearchX);
            output.putInt("blockSearchY", this.blockSearchX);
            output.putInt("blockSearchZ", this.blockSearchX);
            int[] arr = new int[this.chargeableBlocks.size() * 4];
            int index = 0;
            for(Map.Entry<BlockPos,List<Direction>> entry : this.chargeableBlocks.entrySet()){
                arr[index] = entry.getKey().getX();
                arr[index + 1] = entry.getKey().getY();
                arr[index + 2] = entry.getKey().getZ();
                int sides = entry.getValue().contains(null) ? 1 : 0;
                for(Direction side : entry.getValue())
                    sides |= 1 << (side == null ? 0 : side.ordinal() + 1);
                arr[index + 3] = sides;
                index += 4;
            }
            output.putIntArray("chargeableBlocks", arr);
        }
    }

    @Override
    public void writeItemStackData(ValueOutput output){
        if(this.energy <= 0 && this.redstoneMode == RedstoneMode.DISABLED)
            return;

        this.writeData(output);
        output.discard("highlightArea");
        output.discard("isRedstonePowered");
        if(this.type.canChargeBlocks){
            output.discard("blockSearchX");
            output.discard("blockSearchY");
            output.discard("blockSearchZ");
            output.discard("chargeableBlocks");
        }
    }

    @Override
    protected void readData(ValueInput input){
        this.energy = input.getIntOr("energy", 0);
        this.highlightArea = input.getBooleanOr("highlightArea", false);
        this.redstoneMode = RedstoneMode.fromIndex(input.getIntOr("redstoneMode", 0));
        this.isRedstonePowered = input.getBooleanOr("isRedstonePowered", false);
        if(this.type.canChargeBlocks && input.getIntArray("chargeableBlocks").isPresent()){
            this.blockSearchX = input.getIntOr("blockSearchX", 0);
            this.blockSearchY = input.getIntOr("blockSearchY", 0);
            this.blockSearchZ = input.getIntOr("blockSearchZ", 0);
            this.chargeableBlocks.clear();
            int[] arr = input.getIntArray("chargeableBlocks").orElseGet(() -> new int[0]);
            List<Direction> directions = new ArrayList<>(CAPABILITY_DIRECTIONS.size());
            for(int i = 0; i < arr.length / 4; i++){
                BlockPos pos = new BlockPos(arr[i * 4], arr[i * 4 + 1], arr[i * 4 + 2]);
                int sides = arr[i * 4 + 3];
                for(Direction side : CAPABILITY_DIRECTIONS){
                    if(((sides >> (side == null ? 0 : side.ordinal() + 1)) & 1) == 1)
                        directions.add(side);
                }
                this.chargeableBlocks.put(pos, new ArrayList<>(directions));
                directions.clear();
            }
        }
    }

    @Override
    public long insert(long amount, TransactionContext transaction){
        StoragePreconditions.notNegative(amount);
        int received = (int)Math.min(amount, Math.min(this.type.capacity.get() - this.energy, this.type.transferRate.get() * 100));
        if(received > 0){
            this.snapshotParticipant.updateSnapshots(transaction);
            this.energy += received;
        }
        return received;
    }

    @Override
    public long extract(long amount, TransactionContext transaction){
        return 0;
    }

    @Override
    public long getAmount(){
        return this.energy;
    }

    @Override
    public long getCapacity(){
        return this.type.capacity.get();
    }

    @Override
    public boolean supportsInsertion(){
        return true;
    }

    @Override
    public boolean supportsExtraction(){
        return false;
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
