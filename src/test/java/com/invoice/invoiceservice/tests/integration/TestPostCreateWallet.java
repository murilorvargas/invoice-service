package com.invoice.invoiceservice.tests.integration;

import com.invoice.invoiceservice.tests.utils.DocumentHandlers;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TestPostCreateWallet extends BaseIntegrationTest {

    private static final String POST_WALLET = "/wallets";
    private static final String GET_WALLETS = "/wallets";
    private static final String GET_WALLET_BY_KEY = "/wallets/{walletKey}";

    @Test
    void createWallet_naturalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String requestControlKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCpf();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "João Silva",
                "documentNumber", documentNumber
            ),
            "invoiceConfiguration", Map.of(
                "closingFixedDay", 15,
                "dueType", "FIXED_DAY",
                "dueFixedDay", 20,
                "dueOffsetMonths", 0,
                "finePercentage", 0.02,
                "interestPercentage", 0.02,
                "revolvingInterestPercentage", 0.02
            ),
            "walletLimit", Map.of(
                "limitAmount", 10000.00
            )
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_WALLET)
        .then()
            .statusCode(201)
            .extract().response();

        String walletKey = postResponse.jsonPath().getString("walletKey");
        assertThat(walletKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);
        assertThat(postResponse.jsonPath().getString("walletStatus")).isNotNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLETS)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].walletKey")).isEqualTo(walletKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].walletStatus")).isNotNull();

        Response getByKeyResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getByKeyResponse.jsonPath().getString("walletKey")).isEqualTo(walletKey);
        assertThat(getByKeyResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getByKeyResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);

        List<Map<String, Object>> walletLimits = getByKeyResponse.jsonPath().getList("walletLimits");
        assertThat(walletLimits).hasSize(1);
        assertThat(walletLimits.getFirst().get("limitAmount")).isEqualTo(10000.0f);
        assertThat(walletLimits.getFirst().get("usedLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createWallet_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String requestControlKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCnpj();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Empresa LTDA",
                "documentNumber", documentNumber
            ),
            "invoiceConfiguration", Map.of(
                "closingFixedDay", 15,
                "dueType", "FIXED_DAY",
                "dueFixedDay", 20,
                "dueOffsetMonths", 0,
                "finePercentage", 0.02,
                "interestPercentage", 0.02,
                "revolvingInterestPercentage", 0.02
            ),
            "walletLimit", Map.of(
                "limitAmount", 10000.00
            )
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_WALLET)
        .then()
            .statusCode(201)
            .extract().response();

        String walletKey = postResponse.jsonPath().getString("walletKey");
        assertThat(walletKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);
        assertThat(postResponse.jsonPath().getString("walletStatus")).isNotNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLETS)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].walletKey")).isEqualTo(walletKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].walletStatus")).isNotNull();

        Response getByKeyResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getByKeyResponse.jsonPath().getString("walletKey")).isEqualTo(walletKey);
        assertThat(getByKeyResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getByKeyResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);

        List<Map<String, Object>> walletLimits = getByKeyResponse.jsonPath().getList("walletLimits");
        assertThat(walletLimits).hasSize(1);
        assertThat(walletLimits.getFirst().get("limitAmount")).isEqualTo(10000.0f);
        assertThat(walletLimits.getFirst().get("usedLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createWallet_ruleDue_naturalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String requestControlKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCpf();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "João Silva",
                "documentNumber", documentNumber
            ),
            "invoiceConfiguration", Map.of(
                "closingFixedDay", 15,
                "dueType", "RULE",
                "dueDaysAfterClosing", 10,
                "finePercentage", 0.02,
                "interestPercentage", 0.02,
                "revolvingInterestPercentage", 0.02
            ),
            "walletLimit", Map.of(
                "limitAmount", 10000.00
            )
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_WALLET)
        .then()
            .statusCode(201)
            .extract().response();

        String walletKey = postResponse.jsonPath().getString("walletKey");
        assertThat(walletKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);
        assertThat(postResponse.jsonPath().getString("walletStatus")).isNotNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLETS)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].walletKey")).isEqualTo(walletKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].walletStatus")).isNotNull();

        Response getByKeyResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getByKeyResponse.jsonPath().getString("walletKey")).isEqualTo(walletKey);
        assertThat(getByKeyResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getByKeyResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);

        List<Map<String, Object>> walletLimits = getByKeyResponse.jsonPath().getList("walletLimits");
        assertThat(walletLimits).hasSize(1);
        assertThat(walletLimits.getFirst().get("limitAmount")).isEqualTo(10000.0f);
        assertThat(walletLimits.getFirst().get("usedLimitAmount")).isEqualTo(0.0f);
    }

    @Test
    void createWallet_ruleDue_legalPerson_success() {
        String requesterKey = UUID.randomUUID().toString();
        String requestControlKey = UUID.randomUUID().toString();
        String documentNumber = DocumentHandlers.generateCnpj();

        Map<String, Object> payload = Map.of(
            "requestControlKey", requestControlKey,
            "owner", Map.of(
                "name", "Empresa LTDA",
                "documentNumber", documentNumber
            ),
            "invoiceConfiguration", Map.of(
                "closingFixedDay", 15,
                "dueType", "RULE",
                "dueDaysAfterClosing", 10,
                "finePercentage", 0.02,
                "interestPercentage", 0.02,
                "revolvingInterestPercentage", 0.02
            ),
            "walletLimit", Map.of(
                "limitAmount", 10000.00
            )
        );

        Response postResponse = given()
            .header("SELECTED-USER", requesterKey)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post(POST_WALLET)
        .then()
            .statusCode(201)
            .extract().response();

        String walletKey = postResponse.jsonPath().getString("walletKey");
        assertThat(walletKey).isNotNull();
        assertThat(postResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(postResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);
        assertThat(postResponse.jsonPath().getString("walletStatus")).isNotNull();

        Response getResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLETS)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getResponse.jsonPath().getString("data[0].walletKey")).isEqualTo(walletKey);
        assertThat(getResponse.jsonPath().getString("data[0].requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getResponse.jsonPath().getString("data[0].documentNumber")).isEqualTo(documentNumber);
        assertThat(getResponse.jsonPath().getString("data[0].walletStatus")).isNotNull();

        Response getByKeyResponse = given()
            .header("SELECTED-USER", requesterKey)
        .when()
            .get(GET_WALLET_BY_KEY, walletKey)
        .then()
            .statusCode(200)
            .extract().response();

        assertThat(getByKeyResponse.jsonPath().getString("walletKey")).isEqualTo(walletKey);
        assertThat(getByKeyResponse.jsonPath().getString("requestControlKey")).isEqualTo(requestControlKey);
        assertThat(getByKeyResponse.jsonPath().getString("documentNumber")).isEqualTo(documentNumber);

        List<Map<String, Object>> walletLimits = getByKeyResponse.jsonPath().getList("walletLimits");
        assertThat(walletLimits).hasSize(1);
        assertThat(walletLimits.getFirst().get("limitAmount")).isEqualTo(10000.0f);
        assertThat(walletLimits.getFirst().get("usedLimitAmount")).isEqualTo(0.0f);
    }
}
