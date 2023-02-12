package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    private static final String PATH = "api/orders/";

    @Step("Создание заказа с токеном")
    public ValidatableResponse createOrderWithToken(String accessToken, String ingredient) {
        return given()
                .header("authorization", "bearer " + accessToken)
                .spec(getSpec())
                .body(ingredient)
                .when()
                .post(PATH)
                .then();
    }

    @Step("Создание заказа без токена")
    public ValidatableResponse createOrderWithoutToken(String ingredient) {
        return given()
                .spec(getSpec())
                .body(ingredient)
                .when()
                .post(PATH)
                .then();
    }

    @Step("Получение списка заказов с токеном")
    public ValidatableResponse getOrderListWithToken(String accessToken) {
        return given()
                .header("authorization", "bearer " + accessToken)
                .spec(getSpec())
                .when()
                .get(PATH)
                .then();
    }

    @Step("Получение списка заказов без токена")
    public ValidatableResponse getOrderListWithoutToken() {
        return given()
                .spec(getSpec())
                .when()
                .get(PATH)
                .then();
    }
}
