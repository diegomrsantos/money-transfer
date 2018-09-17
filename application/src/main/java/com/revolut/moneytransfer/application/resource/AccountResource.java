package com.revolut.moneytransfer.application.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.moneytransfer.application.resource.util.Response;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.revolut.moneytransfer.application.resource.util.JsonUtil.json;
import static spark.Spark.*;

public class AccountResource {

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private final Logger logger = LoggerFactory.getLogger(AccountResource.class);

  public AccountResource(final AccountService accountService) {

      after((req, res) -> {
          res.type("application/json");
      });

      path("/accounts", () -> {

          post("", (req, res) -> {
              ObjectMapper objectMapper = new ObjectMapper();
              Map<String,String> requestBody = objectMapper.readValue(req.body(), HashMap.class);
              try {

                  Account account = accountService.create(Long.valueOf(requestBody.get("userId")));
                  res.status(CREATED);
                  return account;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(INTERNAL_SERVER_ERROR);
                  return  new Response("There was an unexpected error when creating the account");
              }

          }, json());

          get("/:accountId", (req, res) -> {
              String accountId = req.params(":accountId");
              try {
                  Optional<Account> account = accountService.findById(Long.valueOf(accountId));
                  if (account.isPresent()) {
                      res.status(OK);
                      return account.get();
                  } else {
                      res.status(NOT_FOUND);
                      return new Response("No account with id '%s' has been found", accountId);
                  }

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(INTERNAL_SERVER_ERROR);
                  return new Response("There was an unexpected error when getting account '%s'.");
              }
          }, json());

          post("/:accountId/deposit", (req, res) -> {

              String accountId = req.params(":accountId");
              ObjectMapper objectMapper = new ObjectMapper();
              Map<String,String> requestBody = objectMapper.readValue(req.body(), HashMap.class);

              try {
                  accountService.deposit(Long.valueOf(accountId), new BigDecimal(requestBody.get("amount")));
                  res.status(OK);
                  return new Response("The operation has been executed");

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(INTERNAL_SERVER_ERROR);
                  return  new Response("There was an unexpected error when depositing into account '%s'.", accountId);
              }

          }, json());

          delete("/:accountId", (req, res) -> {
              String accountId = req.params(":accountId");
              try {
                  boolean hasBeenDeleted = accountService.delete(Long.valueOf(accountId));
                  if (hasBeenDeleted) {
                      res.status(OK);
                      return new Response("Account '%s' has been deleted", accountId);
                  } else {
                      res.status(NOT_FOUND);
                      return new Response("No account with id '%s' has been found", accountId);
                  }
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(INTERNAL_SERVER_ERROR);
                  return new Response("There was an unexpected error deleting account '%s'", accountId);
              }
          }, json());
    });

  }
}