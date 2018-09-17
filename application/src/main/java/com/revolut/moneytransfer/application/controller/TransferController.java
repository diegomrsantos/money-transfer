package com.revolut.moneytransfer.application.controller;

import static com.revolut.moneytransfer.application.controller.util.JsonUtil.json;
import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.moneytransfer.application.controller.util.Response;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.exception.MoneyTransferException;
import com.revolut.moneytransfer.domain.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TransferController {

    private static Logger logger = LoggerFactory.getLogger(TransferController.class);

    public TransferController(final TransferService transferService) {

        after((req, res) -> {
            res.type("application/json");
        });

        path("/transfers", () -> {

            post("", (req, res) -> {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestBody = objectMapper.readValue(req.body(), HashMap.class);
                try {
                    final Transfer transfer = new Transfer(Long.valueOf(requestBody.get("fromAccountId")),
                            Long.valueOf(requestBody.get("toAccountId")),
                            new BigDecimal(requestBody.get("amount"))
                    );
                    final Transfer executedTransfer = transferService.transferMoney(transfer);
                    res.status(201);
                    return executedTransfer;

                } catch (MoneyTransferException e) {

                    logger.warn(e.getMessage(), e);
                    res.status(400);
                    return new Response(e.getMessage());
                } catch (Exception e) {

                    logger.error(e.getMessage(), e);
                    res.status(501);
                    return new Response("An expected error occurred.");
                }

            }, json());
        });
    }
}
