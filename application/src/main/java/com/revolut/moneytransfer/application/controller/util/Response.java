package com.revolut.moneytransfer.application.controller.util;

public class Response {
  private String message;
 
  public Response(String message, String... args) {
    this.message = String.format(message, args);
  }
 
  public Response(Exception e) {
    this.message = e.getMessage();
  }
 
  public String getMessage() {
    return this.message;
  }
}