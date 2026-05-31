package com.invoice.invoiceservice.tests.utils;

import io.restassured.http.ContentType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class SetupTools {

    public static String createWallet(
        String requesterKey,
        String requestControlKey,
        String documentNumber,
        Map<String, Object> invoiceConfiguration,
        Double limitAmount
    ) {
        Map<String, Object> effectiveInvoiceConfig = invoiceConfiguration != null ? invoiceConfiguration : Map.of(
            "closingFixedDay", 15,
            "dueType", "FIXED_DAY",
            "dueFixedDay", 20,
            "dueOffsetMonths", 0,
            "finePercentage", 0.02,
            "interestPercentage", 0.02,
            "revolvingInterestPercentage", 0.02
        );
        double effectiveLimitAmount = limitAmount != null ? limitAmount : 10000.00;

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Test Owner",
                "documentNumber", documentNumber
            ),
            "invoiceConfiguration", effectiveInvoiceConfig,
            "walletLimit", Map.of(
                "limitAmount", effectiveLimitAmount
            )
        );

        return given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/wallets")
        .then()
            .statusCode(201)
            .extract().response()
            .jsonPath().getString("walletKey");
    }

    public static String createCard(
        String requesterKey,
        String walletKey,
        String requestControlKey,
        Map<String, Object> owner,
        Double monthlyLimitAmount
    ) {
        Double effectiveMonthlyLimit = monthlyLimitAmount != null ? monthlyLimitAmount : 5000.00;

        Map<String, Object> payload = new HashMap<>(Map.of(
            "requestControlKey", requestControlKey,
            "monthlyLimitAmount", effectiveMonthlyLimit
        ));
        if (owner != null) {
            payload.put("owner", owner);
        }

        return given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/wallets/{walletKey}/cards", walletKey)
        .then()
            .statusCode(201)
            .extract().response()
            .jsonPath().getString("cardKey");
    }
}
