package com.revolut.moneytransfer.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.moneytransfer.application.controller.util.Response;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.revolut.moneytransfer.application.controller.util.JsonUtil.json;
import static spark.Spark.*;

public class AccountController {

  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  public AccountController(final AccountService accountService) {

      after((req, res) -> {
          res.type("application/json");
      });

      path("/accounts", () -> {

          post("", (req, res) -> {
              ObjectMapper objectMapper = new ObjectMapper();
              Map<String,String> requestBody = objectMapper.readValue(req.body(), HashMap.class);
              try {

                  Account account = accountService.create(Long.valueOf(requestBody.get("userId")));
                  res.status(201);
                  return account;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when creating the account");
              }

          }, json());

          get("/:accountId", (req, res) -> {
              String accountId = req.params(":accountId");
              try {
                  Optional<Account> account = accountService.findById(Long.valueOf(accountId));
                  if (account.isPresent()) {
                      res.status(200);
                      return account.get();
                  } else {
                      res.status(404);
                      return new Response("No account with id '%s' found", accountId);
                  }

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(500);
                  return new Response("An unexpected error occurred.");
              }
          }, json());

          post("/:accountId/deposit", (req, res) -> {

              String accountId = req.params(":accountId");
              ObjectMapper objectMapper = new ObjectMapper();
              Map<String,String> requestBody = objectMapper.readValue(req.body(), HashMap.class);

              try {
                  accountService.deposit(Long.valueOf(accountId), new BigDecimal(requestBody.get("amount")));
                  res.status(200);
                  return new Response("The operation has been executed");

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when depositing into account %s.", accountId);
              }

          }, json());

          delete("/:accountId", (req, res) -> {
              String accountId = req.params(":accountId");
              try {
                  accountService.delete(Long.valueOf(accountId));
                  res.status(200);
                  return String.format("Account with id '%s' has been deleted", accountId);
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(400);
                  return new Response("No account with id '%f' found", accountId);
              }
          });

          get("", (req, res) -> {

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