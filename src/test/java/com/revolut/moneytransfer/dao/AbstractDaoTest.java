package com.revolut.moneytransfer.dao;


import com.revolut.moneytransfer.domain.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AbstractDaoTest {

    private AbstractDao abstractDaoMock;

    @Before
    public void setUp(){
        this.abstractDaoMock =
                mock(AbstractDao.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    public void createAndFindByCommitTest(){

        User user1 = createUser();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.entityMap.get(user1.getId()));
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void createAndRollBackTest(){

        User user1 = new User(1L, "user1Name", "user1Lastname");

        abstractDaoMock.beginTransaction();
        abstractDaoMock.create(user1);
        abstractDaoMock.rollback();

        abstractDaoMock.beginTransaction();
        assertEquals(false, abstractDaoMock.entityMap.containsKey(user1.getId()));
    }

    @Test
    public void findByIdTest(){

        User user1 = createUser();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        ReentrantReadWriteLock reentrantReadLock = (ReentrantReadWriteLock) abstractDaoMock.lockMap.get(user1.getId());

        assertEquals(1, reentrantReadLock.getReadHoldCount());
        assertEquals(true, ((Transaction)abstractDaoMock.transactions.get()).getLockedEntities().contains(reentrantReadLock.readLock()));

        abstractDaoMock.commit();
        assertEquals(0, reentrantReadLock.getReadHoldCount());
        assertEquals(null, abstractDaoMock.transactions.get());
    }

    @Test
    public void findByIdAndLockTest(){

        User user1 = createUser();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findByIdAndLock(user1.getId()));
        ReentrantReadWriteLock reentrantReadLock = (ReentrantReadWriteLock) abstractDaoMock.lockMap.get(user1.getId());

        assertEquals(1, reentrantReadLock.getWriteHoldCount());
        assertEquals(true, ((Transaction)abstractDaoMock.transactions.get()).getLockedEntities().contains(reentrantReadLock.writeLock()));

        abstractDaoMock.commit();
        assertEquals(0, reentrantReadLock.getWriteHoldCount());
        assertEquals(null, abstractDaoMock.transactions.get());
    }

    @Test
    public void findByIdsAndLockTest(){

        User user1 = new User(1L, "user1Name", "user1Lastname");
        User user2 = new User(2L, "user2Name", "user2Lastname");
        abstractDaoMock.beginTransaction();
        abstractDaoMock.create(user1);
        abstractDaoMock.create(user2);
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        Map map = abstractDaoMock.findByIdsAndLock(user2.getId(), user1.getId());
        assertEquals(2, map.size());
        assertEquals(true, map.containsKey(user1.getId()));
        assertEquals(true, map.containsKey(user2.getId()));
        ReentrantReadWriteLock reentrantReadLock1 = (ReentrantReadWriteLock) abstractDaoMock.lockMap.get(user1.getId());
        ReentrantReadWriteLock reentrantReadLock2 = (ReentrantReadWriteLock) abstractDaoMock.lockMap.get(user2.getId());

        assertEquals(1, reentrantReadLock1.getWriteHoldCount());
        assertEquals(true, ((Transaction)abstractDaoMock.transactions.get()).getLockedEntities().contains(reentrantReadLock1.writeLock()));

        assertEquals(1, reentrantReadLock2.getWriteHoldCount());
        assertEquals(true, ((Transaction)abstractDaoMock.transactions.get()).getLockedEntities().contains(reentrantReadLock2.writeLock()));

        abstractDaoMock.commit();
        assertEquals(0, reentrantReadLock1.getWriteHoldCount());
        assertEquals(0, reentrantReadLock2.getWriteHoldCount());
        assertEquals(null, abstractDaoMock.transactions.get());
    }

    @Test
    public void updateCommitTest(){

        User user1 = new User(1L, "user1Name", "user1Lastname");
        User user1Updated = new User(1L, "user1NameUpdated", "user1LastnameUpdated");

        abstractDaoMock.beginTransaction();
        abstractDaoMock.create(user1);
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        abstractDaoMock.update(user1Updated);
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        assertEquals(user1Updated, abstractDaoMock.findById(user1Updated.getId()));
        abstractDaoMock.commit();
    }

    @Test
    public void updateRollbackTest(){

        User user1 = createUser();
        User user1Updated = new User(1L, "user1NameUpdated", "user1LastnameUpdated");

        abstractDaoMock.beginTransaction();
        abstractDaoMock.update(user1Updated);
        abstractDaoMock.rollback();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        abstractDaoMock.commit();
    }

    @Test
    public void deleteCommitTest(){

        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Entity with id 1 not found.");

        User user1 = createUser();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        abstractDaoMock.delete(user1.getId());
        abstractDaoMock.commit();

        abstractDaoMock.beginTransaction();
        try {
            assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        } finally {
            abstractDaoMock.rollback();
        }
    }

    @Test
    public void deleteRollbackTest(){

        User user1 = createUser();

        abstractDaoMock.beginTransaction();
        abstractDaoMock.delete(user1.getId());
        abstractDaoMock.rollback();

        abstractDaoMock.beginTransaction();
        assertEquals(user1, abstractDaoMock.findById(user1.getId()));
        abstractDaoMock.commit();

    }

    private User createUser() {
        User user1 = new User(1L, "user1Name", "user1Lastname");

        abstractDaoMock.beginTransaction();
        abstractDaoMock.create(user1);
        abstractDaoMock.commit();
        return user1;
    }
}
