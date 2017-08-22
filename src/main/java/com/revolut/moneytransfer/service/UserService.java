package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.interfaces.TransactionalDAO;
import com.revolut.moneytransfer.domain.User;

import java.util.Collections;
import java.util.List;

public class UserService {

    private TransactionalDAO<User> userDAO;

    public UserService(TransactionalDAO<User> userDAO) {
        this.userDAO = userDAO;
    }

    public User create(String firstName, String lastName) {

        User user = null;
        try {
            userDAO.beginTransaction();
            user = userDAO.create(new User(firstName, lastName));
            userDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
        }
        return user;
    }

    public User findById(Long id) {
        User user = null;
        try {
            userDAO.beginTransaction();
            user = userDAO.findById(id);
            userDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
        }
        return user;
    }

    public User update(User entity) {

        User user = null;
        try {
            userDAO.beginTransaction();
            user = userDAO.update(entity);
            userDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
        }
        return user;

    }

    public void delete(Long id) {
        try {
            userDAO.beginTransaction();
            userDAO.delete(id);
            userDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
            throw e;
        }
    }

    public List<User> getAll() {
        List<User> userList = Collections.EMPTY_LIST;
        try {
            userDAO.beginTransaction();
            userList= userDAO.getAll();
            userDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
            throw e;
        }
        return userList;
    }
}
