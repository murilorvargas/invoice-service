package com.invoice.invoiceservice.tests.utils;

import io.restassured.http.ContentType;

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
        String effectiveRequesterKey = requesterKey != null ? requesterKey : UUID.randomUUID().toString();
        String effectiveRequestControlKey = requestControlKey != null ? requestControlKey : UUID.randomUUID().toString();
        String effectiveDocumentNumber = documentNumber != null ? documentNumber : DocumentHandlers.generateCpf();
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
            "requestControlKey", effectiveRequestControlKey,
            "owner", Map.of(
                "name", "Test Owner",
                "documentNumber", effectiveDocumentNumber
            ),
            "invoiceConfiguration", effectiveInvoiceConfig,
            "walletLimit", Map.of(
                "limitAmount", effectiveLimitAmount
            )
        );

        return given()
            .header("SELECTED-USER", effectiveRequesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/wallets")
        .then()
            .statusCode(201)
            .extract().response()
            .jsonPath().getString("walletKey");
    }
}
