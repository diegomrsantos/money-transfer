package com.revolut.moneytransfer.dao;


import com.revolut.moneytransfer.domain.Entity;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class Transaction<T extends Entity> {

    private List<T> entitiesToRestore;
    private List<T> entitiesToDelete;
    private List<Lock> lockedEntities;

    public Transaction() {
        this.entitiesToRestore = new ArrayList<>();
        this.entitiesToDelete = new ArrayList<>();
        this.lockedEntities = new ArrayList<>();
    }

    public void addEntityToRestoreWhenRollingBack(T entity){
        this.entitiesToRestore.add(entity);
    }

    public List<T> getEntitiesToRestoreWhenRollingBack() {
        return entitiesToRestore;
    }

    public void addEntityToDeleteWhenRollingBack(T entity){
        this.entitiesToDelete.add(entity);
    }

    public List<T> getEntitiesToDeleteWhenRollingBack() {
        return entitiesToDelete;
    }

    public void addLockedEntity(Lock lock){
        this.lockedEntities.add(lock);
    }

    public List<Lock> getLockedEntities() {
        return lockedEntities;
    }

    public void close(){
        this.entitiesToRestore.clear();
        this.entitiesToDelete.clear();
        this.lockedEntities.clear();
    }
}
