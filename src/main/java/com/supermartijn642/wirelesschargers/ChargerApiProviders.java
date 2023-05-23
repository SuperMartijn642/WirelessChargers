package com.supermartijn642.wirelesschargers;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created 23/05/2023 by SuperMartijn642
 */
public class ChargerApiProviders {

    public static void register(){
        for(ChargerType type : ChargerType.values())
            EnergyStorage.SIDED.registerForBlockEntity((entity, direction) -> new ChargerEnergyStorage(entity), type.getBlockEntityType());
    }

    private static class ChargerEnergyStorage extends SnapshotParticipant<Integer> implements EnergyStorage {

        private final ChargerBlockEntity entity;

        private ChargerEnergyStorage(ChargerBlockEntity entity){
            this.entity = entity;
        }

        @Override
        protected Integer createSnapshot(){
            return this.entity.getEnergyStored();
        }

        @Override
        protected void readSnapshot(Integer snapshot){
            this.entity.setEnergyStored(snapshot);
        }

        @Override
        public boolean supportsInsertion(){
            return true;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction){
            StoragePreconditions.notNegative(maxAmount);
            maxAmount = Math.min(maxAmount, Integer.MAX_VALUE / 2);
            this.updateSnapshots(transaction);
            return this.entity.receiveEnergy((int)maxAmount, false);
        }

        @Override
        public boolean supportsExtraction(){
            return false;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction){
            return 0;
        }

        @Override
        public long getAmount(){
            return this.entity.getEnergyStored();
        }

        @Override
        public long getCapacity(){
            return this.entity.getMaxEnergyStored();
        }
    }
}
