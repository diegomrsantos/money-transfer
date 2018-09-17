package com.revolut.moneytransfer.application.resource;

import static com.revolut.moneytransfer.application.resource.util.JsonUtil.json;
import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.moneytransfer.application.resource.util.Response;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.exception.MoneyTransferException;
import com.revolut.moneytransfer.domain.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TransferResources {

    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int INTERNAL_SERVER_ERRO = 500;
    private static Logger logger = LoggerFactory.getLogger(TransferResources.class);

    public TransferResources(final TransferService transferService) {

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
                    res.status(CREATED);
                    return executedTransfer;

                } catch (MoneyTransferException e) {

                    logger.warn(e.getMessage(), e);
                    res.status(BAD_REQUEST);
                    return new Response(e.getMessage());
                } catch (Exception e) {

                    logger.error(e.getMessage(), e);
                    res.status(INTERNAL_SERVER_ERRO);
                    return new Response("An expected error occurred.");
                }

            }, json());
        });
    }
}
