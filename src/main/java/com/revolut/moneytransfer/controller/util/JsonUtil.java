package com.revolut.moneytransfer.controller.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import spark.ResponseTransformer;

public class JsonUtil {
 
  public static String toJson(Object object) throws JsonProcessingException {
    //return new Gson().toJson(object);
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      return ow.writeValueAsString(object);
  }
 
  public static ResponseTransformer json() {
    return JsonUtil::toJson;
  }
}