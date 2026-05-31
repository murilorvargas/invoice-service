package com.invoice.invoiceservice.tests.integration;

import com.invoice.invoiceservice.tests.utils.DocumentHandlers;
import com.invoice.invoiceservice.tests.utils.SetupTools;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TestPostCreateCard extends BaseIntegrationTest {

    private static final String POST_CARD = "/wallets/{walletKey}/cards";
    private static final String GET_CARDS = "/wallets/{walletKey}/cards";

    @Test
    void createCard_walletOwner_naturalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String requestControlKey = UUID.randomUUID().toString();
        BigDecimal monthlyLimit = new BigDecimal("5000.00");

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "monthlyLimitAmount", monthlyLimit
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat(postResponse.jsonPath().getFloat("monthlyLimitAmount")).isEqualTo(5000.0f);
        assertThat(postResponse.jsonPath().getFloat("usedMonthlyLimitAmount")).isEqualTo(0.0f);

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].cardStatus")).isEqualTo("ACTIVE");
        assertThat(getResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(5000.0f);
        assertThat(getResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createCard_walletOwner_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCnpj();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), documentNumber, null, null);

        String requestControlKey = UUID.randomUUID().toString();
        BigDecimal monthlyLimit = new BigDecimal("10000.00");

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "monthlyLimitAmount", monthlyLimit
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat(postResponse.jsonPath().getFloat("monthlyLimitAmount")).isEqualTo(10000.0f);
        assertThat(postResponse.jsonPath().getFloat("usedMonthlyLimitAmount")).isEqualTo(0.0f);

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
        assertThat(getResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(10000.0f);
        assertThat(getResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createCard_differentOwner_naturalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String requestControlKey = UUID.randomUUID().toString();
        String ownerDocumentNumber = DocumentHandlers.generateCpf();
        BigDecimal monthlyLimit = new BigDecimal("3000.00");

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Maria Silva",
                "documentNumber", ownerDocumentNumber
            ),
            "monthlyLimitAmount", monthlyLimit
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat(postResponse.jsonPath().getFloat("monthlyLimitAmount")).isEqualTo(3000.0f);
        assertThat(postResponse.jsonPath().getFloat("usedMonthlyLimitAmount")).isEqualTo(0.0f);

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(3000.0f);
        assertThat(getResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createCard_differentOwner_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String requestControlKey = UUID.randomUUID().toString();
        String ownerDocumentNumber = DocumentHandlers.generateCnpj();
        BigDecimal monthlyLimit = new BigDecimal("15000.00");

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Empresa do Parceiro LTDA",
                "documentNumber", ownerDocumentNumber
            ),
            "monthlyLimitAmount", monthlyLimit
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat(postResponse.jsonPath().getFloat("monthlyLimitAmount")).isEqualTo(15000.0f);
        assertThat(postResponse.jsonPath().getFloat("usedMonthlyLimitAmount")).isEqualTo(0.0f);

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(getResponse.jsonPath().getFloat("data[0].monthlyLimitAmount")).isEqualTo(15000.0f);
        assertThat(getResponse.jsonPath().getFloat("data[0].usedMonthlyLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createCard_walletOwner_noMonthlyLimit_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String requestControlKey = UUID.randomUUID().toString();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat((Object) postResponse.jsonPath().get("monthlyLimitAmount")).isNull();
        assertThat((Object) postResponse.jsonPath().get("usedMonthlyLimitAmount")).isNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].cardStatus")).isEqualTo("ACTIVE");
        assertThat((Object) getResponse.jsonPath().get("data[0].monthlyLimitAmount")).isNull();
        assertThat((Object) getResponse.jsonPath().get("data[0].usedMonthlyLimitAmount")).isNull();
    }

    @Test
    void createCard_differentOwner_noMonthlyLimit_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String requestControlKey = UUID.randomUUID().toString();
        String ownerDocumentNumber = DocumentHandlers.generateCnpj();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Empresa Parceira LTDA",
                "documentNumber", ownerDocumentNumber
            )
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(201)
            .extract().response();

        String cardKey = postResponse.jsonPath().getString("cardKey");
        assertThat(cardKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(postResponse.jsonPath().getString("cardStatus")).isEqualTo("ACTIVE");
        assertThat((Object) postResponse.jsonPath().get("monthlyLimitAmount")).isNull();
        assertThat((Object) postResponse.jsonPath().get("usedMonthlyLimitAmount")).isNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(ownerDocumentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].cardStatus")).isEqualTo("ACTIVE");
        assertThat((Object) getResponse.jsonPath().get("data[0].monthlyLimitAmount")).isNull();
        assertThat((Object) getResponse.jsonPath().get("data[0].usedMonthlyLimitAmount")).isNull();
    }

    @Test
    void createCard_walletNotFound() {
        String requesterKey = UUID.randomUUID().toString();
        String nonExistentWalletKey = UUID.randomUUID().toString();

        String requestControlKey = UUID.randomUUID().toString();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "monthlyLimitAmount", 5000.00
        );

        Response response = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, nonExistentWalletKey)
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

    @Test
    void createCard_requesterDoesNotOwnWallet() {
        String walletOwnerKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(walletOwnerKey, UUID.randomUUID().toString(), DocumentHandlers.generateCpf(), null, null);

        String differentRequesterKey = UUID.randomUUID().toString();
        String requestControlKey = UUID.randomUUID().toString();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "monthlyLimitAmount", 5000.00
        );

        Response response = given()
            .header("SELECTED-USER", differentRequesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_CARD, walletKey)
        .then()
            .statusCode(404)
            .extract().response();

        assertThat(response.jsonPath().getString("code")).isEqualTo("INV00001");
    }

}
