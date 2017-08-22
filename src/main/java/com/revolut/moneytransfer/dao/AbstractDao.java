package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.dao.interfaces.TransactionalDAO;
import com.revolut.moneytransfer.domain.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractDao<T extends Entity> implements TransactionalDAO<T> {

    protected final Map<Long, T> entityMap;
    protected ThreadLocal<Transaction<T>> transactions;
    protected final Map<Long, ReentrantReadWriteLock> lockMap = new ConcurrentHashMap<>();

    public AbstractDao() {
        this.entityMap = new ConcurrentHashMap<>();
        this.transactions = new ThreadLocal<>();
    }

    public T create(T entity) {

        entityMap.putIfAbsent(entity.getId(), entity);
        lockMap.computeIfAbsent(entity.getId(), (k) -> new ReentrantReadWriteLock());
        getCurrentTransaction().addEntityToDeleteWhenRollingBack(entity);
        return entity;
    }

    public T findById(Long id) {
        ReadWriteLock lock = getLock(id);
        return find(id, lock.readLock());
    }

    public T findByIdAndLock(Long id) {
        ReadWriteLock lock = getLock(id);
        return find(id, lock.writeLock());
    }

    private ReadWriteLock getLock(Long id){
        ReentrantReadWriteLock reentrantReadWriteLock = lockMap.get(id);
        if (null == reentrantReadWriteLock) {
            throw new RuntimeException(String.format("Entity with id %s not found.", id));
        }
        return reentrantReadWriteLock;
    }

    private T find(Long id, Lock lock){
        Transaction<T> currentTransaction = getCurrentTransaction();
        lock.lock();
        T entity = entityMap.get(id);
        currentTransaction.addLockedEntity(lock);
        return entity;
    }

    @Override
    public Map<Long, T> findByIdsAndLock(Long... ids) {
        return Arrays.stream(ids)
                .sorted()
                .map(id -> findByIdAndLock(id))
                .collect(Collectors.toMap(e-> e.getId(), Function.identity()));
    }

    public T update(T entity) {

        T existingEntity = findByIdAndLock(entity.getId());
        getCurrentTransaction().addEntityToRestoreWhenRollingBack(existingEntity);
        entityMap.put(entity.getId(), entity);
        return entity;
    }

    public void delete(Long id) {
        T existingEntity = findByIdAndLock(id);
        getCurrentTransaction().addEntityToRestoreWhenRollingBack(existingEntity);
        lockMap.remove(id);
        entityMap.remove(id);
    }

    public List<T> getAll() {
        return entityMap.keySet()
                .stream()
                .sorted()
                .map(id-> findById(id))
                .collect(Collectors.toList());

    }

    @Override
    public void beginTransaction() {
        Transaction<T> transaction = transactions.get();
        if(transaction != null){
            throw new IllegalStateException("There an open transaction already");
        }
        transactions.set(new Transaction<>());
    }

    @Override
    public void rollback() {
        Transaction<T> currentTransaction = getCurrentTransaction();
        currentTransaction.getEntitiesToRestoreWhenRollingBack().stream().forEach(e-> {
            entityMap.put(e.getId(), e);
            lockMap.put(e.getId(), new ReentrantReadWriteLock());
        });
        currentTransaction.getEntitiesToDeleteWhenRollingBack().stream().forEach(e-> {
            entityMap.remove(e.getId());
            lockMap.remove(e.getId());
        });
        currentTransaction.getLockedEntities().stream().forEach(lock -> lock.unlock());
        currentTransaction.close();
        transactions.remove();
    }

    @Override
    public void commit() {
        Transaction<T> currentTransaction = getCurrentTransaction();
        currentTransaction.getLockedEntities().stream().forEach(lock -> lock.unlock());
        currentTransaction.close();
        transactions.remove();
    }

    private Transaction<T> getCurrentTransaction(){
        Transaction<T> transaction = transactions.get();
        if(transaction == null){
            throw new IllegalStateException("There is no open transaction");
        }
        return transaction;
    }

}