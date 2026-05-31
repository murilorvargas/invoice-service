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
        String walletKey = SetupTools.createWallet(requesterKey, null, null, null, null);

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
        assertThat(postResponse.jsonPath().getString("status")).isEqualTo("ACTIVE");

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].status")).isEqualTo("ACTIVE");
    }

    @Test
    void createCard_walletOwner_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCnpj();
        String walletKey = SetupTools.createWallet(requesterKey, null, documentNumber, null, null);

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
        assertThat(postResponse.jsonPath().getString("status")).isEqualTo("ACTIVE");

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
    }

    @Test
    void createCard_differentOwner_naturalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, null, null, null, null);

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
        assertThat(postResponse.jsonPath().getString("status")).isEqualTo("ACTIVE");

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
    }

    @Test
    void createCard_differentOwner_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String walletKey = SetupTools.createWallet(requesterKey, null, null, null, null);

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
        assertThat(postResponse.jsonPath().getString("status")).isEqualTo("ACTIVE");

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_CARDS, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].cardKey")).isEqualTo(cardKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(ownerDocumentNumber);
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
        String walletKey = SetupTools.createWallet(walletOwnerKey, null, null, null, null);

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
