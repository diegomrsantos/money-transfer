package com.revolut.moneytransfer.controller;

import com.revolut.moneytransfer.controller.util.Response;
import com.revolut.moneytransfer.domain.Account;
import com.revolut.moneytransfer.service.AccountService;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

import static com.revolut.moneytransfer.controller.util.JsonUtil.json;
import static spark.Spark.*;

public class AccountController {

  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  public AccountController(final AccountService accountService) {

      after((req, res) -> {
          res.type("application/json");
      });

      path("/accounts", () -> {

          post("/:userId", (req, res) -> {
              String userId = req.params(":userId");
              try {

                  Account account = accountService.create(userId);
                  res.status(201);
                  return account;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when creating the account");
              }

          }, json());

          get("/:id", (req, res) -> {
              String id = req.params(":id");
              try {
                  Account account = accountService.findById(Long.valueOf(id));
                  res.status(200);
                  return account;
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(400);
                  return new Response("No account with id '%f' found", id.toString());
              }
          }, json());

          put("/:id/deposit", (req, res) -> {
              String id = req.params(":id");
              String value = req.queryParams("value");

              try {
                  Account account = accountService.deposit(Long.valueOf(id), Money.of(new BigDecimal(value), "EUR"));
                  res.status(200);
                  return account;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when depositing into account %s.", id);
              }

          }, json());

          delete("/:id", (req, res) -> {
              String id = req.params(":id");
              try {
                  accountService.delete(Long.valueOf(id));
                  res.status(200);
                  return String.format("Account with id '%s' has been deleted", id);
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(400);
                  return new Response("No account with id '%f' found", id.toString());
              }
          });

          put("/transfer/:fromAccountId/:toAccountId/:value", (req, res) -> {
              String fromAccountId = req.params(":fromAccountId");
              String toAccountId = req.params(":toAccountId");
              String value = req.params("value");

              try {
                  accountService.transferMoney(
                          Long.valueOf(fromAccountId), Long.valueOf(toAccountId), Money.of(new BigDecimal(value), "EUR"));
                  res.status(200);
                  return "ok";

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when transfering %s from %s to account %s."
                          , value, fromAccountId, toAccountId);
              }

          }, json());

          get("/list", (req, res) -> {

              try {
                  List<Account> accountList = accountService.getAll();
                  res.status(200);
                  return accountList;
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  "There was an error when trying to get the accounts";

              }
          }, json());

    });

  }
}