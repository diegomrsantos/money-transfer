package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.domain.User;

import java.util.concurrent.atomic.AtomicLong;

/*
 * In memory Dao.
 */
public class UserDao extends AbstractDao<User> {

    private AtomicLong idGenerator;

    public UserDao() {
        super();
        idGenerator = new AtomicLong(1);
    }

    public User create(User user) {

        return super.create(new User(idGenerator.getAndIncrement(), user.getFirstName(), user.getLastName()));
    }
}