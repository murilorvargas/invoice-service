package com.invoice.invoiceservice.tests.integration;

import com.invoice.invoiceservice.tests.utils.DocumentHandlers;
import com.invoice.invoiceservice.tests.utils.SetupTools;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TestPostCreateCardEntry extends BaseIntegrationTest {

    private static final String POST_CARD_ENTRY = "/wallets/{walletKey}/cards/{cardKey}/card_entries";
    private static final String GET_CARD_ENTRIES = "/wallets/{walletKey}/cards/{cardKey}/card_entries";
    private static final String GET_CARDS = "/wallets/{walletKey}/cards";
    private static final String GET_WALLET_BY_KEY = "/wallets/{walletKey}";
    private static final String GET_INVOICES = "/wallets/{walletKey}/invoices";

    @Test
    void createCardEntry_singleInstallment_success() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 1000.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, 500.00);

        String requestControlKey = UUID.randomUUID().toString();
        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "amount", 100.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardEntryKey = postResponse.jsonPath().getString("cardEntryKey");
        assertThat(cardEntryKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getFloat("amount")).isEqualTo(100.0f);
        assertThat(postResponse.jsonPath().getString("cardEntryType")).isEqualTo("PURCHASE");
        assertThat(postResponse.jsonPath().getString("cardEntryStatus")).isEqualTo("PROCESSING_CONCLUSION");

        Thread.sleep(1000);

        Response getCardEntriesResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("page", 1)
        .when()
            .get(GET_CARD_ENTRIES, walletKey, cardKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryKey")).isEqualTo(cardEntryKey);
        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryStatus")).isEqualTo("CONCLUDED");

        Response getCardsResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("cardKey", cardKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardsResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(500.0f);
        assertThat(getCardsResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(100.0f);

        Response getWalletResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getWalletResponse.jsonPath().getFloat("walletLimits[0].usedLimitAmount")).isEqualTo(100.0f);

        Response getInvoicesResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_INVOICES, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        List<Map<String, Object>> invoices = getInvoicesResponse.jsonPath().getList("data");
        assertThat(invoices).hasSize(1);
        assertThat(getInvoicesResponse.jsonPath().getFloat("data[0].amount")).isEqualTo(100.0f);
        assertThat(getInvoicesResponse.jsonPath().getString("data[0].invoiceStatus")).isEqualTo("OPENED");

        List<Map<String, Object>> invoiceItems = getInvoicesResponse.jsonPath().getList("data[0].invoiceItems");
        assertThat(invoiceItems).hasSize(1);
        assertThat(getInvoicesResponse.jsonPath().getString("data[0].invoiceItems[0].description")).isEqualTo("Amazon");
        assertThat(getInvoicesResponse.jsonPath().getFloat("data[0].invoiceItems[0].amount")).isEqualTo(100.0f);
    }

    @Test
    void createCardEntry_multipleInstallments_success() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 1000.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, 500.00);

        String requestControlKey = UUID.randomUUID().toString();
        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "amount", 300.00,
            "numberOfInstallments", 3,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Samsung")
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardEntryKey = postResponse.jsonPath().getString("cardEntryKey");
        assertThat(cardEntryKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getFloat("amount")).isEqualTo(300.0f);
        assertThat(postResponse.jsonPath().getString("cardEntryType")).isEqualTo("PURCHASE");
        assertThat(postResponse.jsonPath().getString("cardEntryStatus")).isEqualTo("PROCESSING_CONCLUSION");

        Thread.sleep(1000);

        Response getCardEntriesResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("page", 1)
        .when()
            .get(GET_CARD_ENTRIES, walletKey, cardKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryKey")).isEqualTo(cardEntryKey);
        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryStatus")).isEqualTo("CONCLUDED");

        Response getCardsResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("cardKey", cardKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardsResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(500.0f);
        assertThat(getCardsResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(300.0f);

        Response getInvoicesResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_INVOICES, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        List<Map<String, Object>> invoices = getInvoicesResponse.jsonPath().getList("data");
        assertThat(invoices).hasSize(3);
        assertThat(getInvoicesResponse.jsonPath().getFloat("data[0].amount")).isEqualTo(100.0f);
        assertThat(getInvoicesResponse.jsonPath().getFloat("data[1].amount")).isEqualTo(100.0f);
        assertThat(getInvoicesResponse.jsonPath().getFloat("data[2].amount")).isEqualTo(100.0f);

        assertThat(getInvoicesResponse.jsonPath().getString("data[0].invoiceItems[0].description")).isEqualTo("Samsung - 1/3");
        assertThat(getInvoicesResponse.jsonPath().getString("data[1].invoiceItems[0].description")).isEqualTo("Samsung - 2/3");
        assertThat(getInvoicesResponse.jsonPath().getString("data[2].invoiceItems[0].description")).isEqualTo("Samsung - 3/3");
    }

    @Test
    void createCardEntry_withdraw_success() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 1000.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, 500.00);

        String requestControlKey = UUID.randomUUID().toString();
        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "amount", 200.00,
            "numberOfInstallments", 1,
            "cardEntryType", "WITHDRAW",
            "cardEntryData", Map.of("merchantName", "ATM Saque")
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardEntryKey = postResponse.jsonPath().getString("cardEntryKey");
        assertThat(cardEntryKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getFloat("amount")).isEqualTo(200.0f);
        assertThat(postResponse.jsonPath().getString("cardEntryType")).isEqualTo("WITHDRAW");
        assertThat(postResponse.jsonPath().getString("cardEntryStatus")).isEqualTo("PROCESSING_CONCLUSION");

        Thread.sleep(1000);

        Response getCardEntriesResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("page", 1)
        .when()
            .get(GET_CARD_ENTRIES, walletKey, cardKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryKey")).isEqualTo(cardEntryKey);
        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryType")).isEqualTo("WITHDRAW");
        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryStatus")).isEqualTo("CONCLUDED");

        Response getCardsResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("cardKey", cardKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardsResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(500.0f);
        assertThat(getCardsResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(200.0f);
    }

    @Test
    void createCardEntry_noCardMonthlyLimit_success() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 1000.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, null);

        String requestControlKey = UUID.randomUUID().toString();
        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "amount", 800.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Shopee")
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardEntryKey = postResponse.jsonPath().getString("cardEntryKey");
        assertThat(cardEntryKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getFloat("amount")).isEqualTo(800.0f);
        assertThat(postResponse.jsonPath().getString("cardEntryStatus")).isEqualTo("PROCESSING_CONCLUSION");

        Thread.sleep(1000);

        Response getCardEntriesResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("page", 1)
        .when()
            .get(GET_CARD_ENTRIES, walletKey, cardKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryKey")).isEqualTo(cardEntryKey);
        assertThat(getCardEntriesResponse.jsonPath().getString("data[0].cardEntryStatus")).isEqualTo("CONCLUDED");

        Response getWalletResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getWalletResponse.jsonPath().getFloat("walletLimits[0].usedLimitAmount")).isEqualTo(800.0f);

        Response getCardsResponse = given()
            .header("SELECTED-USER", requesterKey)
            .queryParam("cardKey", cardKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat((Object) getCardsResponse.jsonPath().get("data[0].monthlyLimitAmount")).isNull();
        assertThat((Object) getCardsResponse.jsonPath().get("data[0].usedMonthlyLimitAmount")).isNull();
    }

    @Test
    void createCardEntry_walletNotFound() {
        String requesterKey = UUID.randomUUID().toString();

        Map<String, Object> payload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 100.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

    @Test
    void createCardEntry_requesterDoesNotOwnWallet() {
        String walletOwnerKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(walletOwnerKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);
        String cardKey = SetupTools.createCard(walletOwnerKey, walletKey, UUID.randomUUID().toString(), null, null);

        Map<String, Object> payload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 100.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", UUID.randomUUID().toString())
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

    @Test
    void createCardEntry_cardNotFound() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        Map<String, Object> payload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 100.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, UUID.randomUUID().toString())
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

    @Test
    void createCardEntry_cardDoesNotBelongToWallet() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey1 = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);
        String walletKey2 = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);
        String cardKey = SetupTools.createCard(requesterKey, walletKey2, UUID.randomUUID().toString(), null, null);

        Map<String, Object> payload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 100.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD_ENTRY, walletKey1, cardKey)
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

    @Test
    void createCardEntry_cardMonthlyLimitExceeded() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 1000.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, 100.00);

        Map<String, Object> firstPayload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 60.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(firstPayload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201);

        Thread.sleep(1000);

        Map<String, Object> secondPayload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 60.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(secondPayload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(422)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00005");
    }

    @Test
    void createCardEntry_walletLimitExceeded() throws InterruptedException {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, 100.00);
        String cardKey = SetupTools.createCard(requesterKey, walletKey, UUID.randomUUID().toString(), null, 500.00);

        Map<String, Object> firstPayload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 70.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(firstPayload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(201);

        Thread.sleep(1000);

        Map<String, Object> secondPayload = Map.of(
            "requestControlKey", UUID.randomUUID().toString(),
            "amount", 70.00,
            "numberOfInstallments", 1,
            "cardEntryType", "PURCHASE",
            "cardEntryData", Map.of("merchantName", "Amazon")
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(secondPayload)
        .when()
            .post(POST_CARD_ENTRY, walletKey, cardKey)
        .then()
            .statusCode(422)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00006");
    }
}
