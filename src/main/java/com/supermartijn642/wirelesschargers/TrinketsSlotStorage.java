package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.util.Pair;
import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

/**
 * Created 26/04/2024 by SuperMartijn642
 */
public class TrinketsSlotStorage extends SnapshotParticipant<Pair<ItemVariant,Integer>> implements SingleSlotStorage<ItemVariant> {

    private final SlotReference slot;
    private ItemVariant item;
    private int count;

    public TrinketsSlotStorage(SlotReference slot, ItemStack stack){
        this.slot = slot;
        this.item = ItemVariant.of(stack);
        this.count = stack.getCount();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction){
        if(maxAmount >= 1 && this.item.isBlank()){
            this.updateSnapshots(transaction);
            this.item = resource;
            this.count = 1;
            return 1;
        }
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction){
        if(maxAmount >= 1 && this.count > 0 && this.item.equals(resource)){
            this.updateSnapshots(transaction);
            this.count--;
            if(this.count == 0)
                this.item = ItemVariant.blank();
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank(){
        return this.item.isBlank();
    }

    @Override
    public ItemVariant getResource(){
        return this.item;
    }

    @Override
    public long getAmount(){
        return this.count;
    }

    @Override
    public long getCapacity(){
        return 1;
    }

    @Override
    protected Pair<ItemVariant,Integer> createSnapshot(){
        return Pair.of(this.item,this.count);
    }

    @Override
    protected void readSnapshot(Pair<ItemVariant,Integer> snapshot){
        this.item = snapshot.left();
        this.count = snapshot.right();
    }

    @Override
    protected void onFinalCommit(){
        this.slot.inventory().setItem(this.slot.index(), this.item.toStack(this.count));
    }
}
