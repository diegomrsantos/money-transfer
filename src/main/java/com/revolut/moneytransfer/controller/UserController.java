package com.revolut.moneytransfer.controller;

import com.revolut.moneytransfer.controller.util.Response;
import com.revolut.moneytransfer.domain.User;
import com.revolut.moneytransfer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.revolut.moneytransfer.controller.util.JsonUtil.json;
import static spark.Spark.*;

public class UserController {

  private final Logger logger = LoggerFactory.getLogger(UserController.class);
 
  public UserController(final UserService userService) {

      after((req, res) -> {
          res.type("application/json");
      });

      path("/users", () -> {

          post("/create", (req, res) -> {
              String firstName = req.queryParams("firstname");
              String lastName = req.queryParams("lastname");

              try {

                  User user = userService.create(firstName, lastName);
                  res.status(201);
                  return user;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when creating the user");
            }

          }, json());

          put("/:id", (req, res) -> {
              String id = req.params(":id");
              String firstName = req.queryParams("firstname");
              String lastName = req.queryParams("lastname");

              try {

                  User user = userService.update(new User(Long.valueOf(id), firstName, lastName));
                  res.status(201);
                  return user;

              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  new Response("There was an error when creating the user");
              }

          }, json());

          get("/list", (req, res) -> {

              try {
                  List<User> users = userService.getAll();
                  res.status(200);
                  return users;
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(501);
                  return  "There was an error";

              }
          }, json());

          get("/:id", (req, res) -> {
              String id = req.params(":id");
              try {
                  User user = userService.findById(Long.valueOf(id));
                  res.status(200);
                  return user;
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(400);
                  return new Response("No user with id '%s' found", id.toString());
              }
          }, json());

          delete("/:id", (req, res) -> {
              String id = req.params(":id");
              try {
                  userService.delete(Long.valueOf(id));
                  res.status(200);
                  return new Response("User with id '%s' has been deleted", id);
              } catch (Exception e) {

                  logger.error(e.getMessage(), e);
                  res.status(400);
                  return new Response("No user with id '%s' found", id.toString());
              }
          }, json());

    });

  }
}