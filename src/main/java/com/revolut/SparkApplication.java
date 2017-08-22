package com.revolut;


import com.revolut.moneytransfer.controller.AccountController;
import com.revolut.moneytransfer.controller.UserController;
import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.UserDao;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.UserService;

public class SparkApplication {

    public static void main(String[] args) {
        UserDao userDAO = new UserDao();
        UserService userService = new UserService(userDAO);
        new UserController(userService);
        new AccountController(new AccountService(new AccountDao(), userDAO));
    }
}
