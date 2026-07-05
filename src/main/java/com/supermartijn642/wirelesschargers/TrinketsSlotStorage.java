package com.supermartijn642.wirelesschargers;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

/**
 * Created 26/04/2024 by SuperMartijn642
 */
public class TrinketsSlotStorage extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {

    private final TrinketSlotAccess slot;

    public TrinketsSlotStorage(TrinketSlotAccess slot){
        this.slot = slot;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction){
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemStack stack = this.slot.get();
        if(!stack.isEmpty() && !resource.matches(stack))
            return 0;
        ItemStack newStack = resource.toStack(1);
        int inserted = (int)Math.min(Integer.MAX_VALUE, Math.min(this.slot.maxStackSize(newStack), stack.getCount() + maxAmount) - stack.getCount());
        if(inserted > 0){
            this.updateSnapshots(transaction);
            newStack.setCount(stack.getCount() + inserted);
            this.slot.set(newStack);
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction){
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemStack stack = this.slot.get();
        if(stack.isEmpty() || !resource.matches(stack))
            return 0;
        int extracted = (int)Math.min(stack.getCount(), maxAmount);
        if(extracted > 0){
            this.updateSnapshots(transaction);
            stack = stack.copy();
            stack.shrink(extracted);
            this.slot.set(stack);
        }
        return extracted;
    }

    @Override
    public boolean isResourceBlank(){
        return this.slot.get().isEmpty();
    }

    @Override
    public ItemVariant getResource(){
        return ItemVariant.of(this.slot.get());
    }

    @Override
    public long getAmount(){
        return this.slot.get().getCount();
    }

    @Override
    public long getCapacity(){
        return this.slot.maxStackSize(this.slot.get());
    }

    @Override
    protected ItemStack createSnapshot(){
        return this.slot.get().copy();
    }

    @Override
    protected void readSnapshot(ItemStack snapshot){
        this.slot.set(snapshot);
    }
}
