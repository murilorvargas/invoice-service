package com.invoice.invoiceservice.tests.utils;

import io.restassured.http.ContentType;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> payload = new HashMap<>(Map.of(
            "requestControlKey", requestControlKey
        ));
        if (owner != null) {
            payload.put("owner", owner);
        }
        if (monthlyLimitAmount != null) {
            payload.put("monthlyLimitAmount", monthlyLimitAmount);
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

    public static String createCardEntry(
        String requesterKey,
        String walletKey,
        String cardKey,
        String requestControlKey,
        Double amount,
        Integer numberOfInstallments,
        String cardEntryType,
        String merchantName
    ) {
        Double effectiveAmount = amount != null ? amount : 100.00;
        Integer effectiveInstallments = numberOfInstallments != null ? numberOfInstallments : 1;
        String effectiveType = cardEntryType != null ? cardEntryType : "PURCHASE";
        String effectiveMerchantName = merchantName != null ? merchantName : "Test Merchant";

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "amount", effectiveAmount,
            "numberOfInstallments", effectiveInstallments,
            "cardEntryType", effectiveType,
            "cardEntryData", Map.of("merchantName", effectiveMerchantName)
        );

        return given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/wallets/{walletKey}/cards/{cardKey}/card_entries", walletKey, cardKey)
        .then()
            .statusCode(201)
            .extract().response()
            .jsonPath().getString("cardEntryKey");
    }
}
