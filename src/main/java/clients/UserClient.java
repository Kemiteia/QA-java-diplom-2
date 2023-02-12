package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    private static final String PATH = "api/auth/";

    @Step("Создание нового пользователя")
    public ValidatableResponse createNewUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH + "register/")
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("authorization", "bearer " + accessToken)
                .spec(getSpec())
                .when()
                .delete(PATH + "user/")
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(User user) {
        return  given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(PATH + "login/")
                .then();
    }

    @Step("Изменение данных о пользователе с токеном")
    public ValidatableResponse changeUserDataWithToken(String accessToken, User user) {
        return given()
                .header("authorization", "bearer " + accessToken)
                .spec(getSpec())
                .body(user)
                .when()
                .patch(PATH + "user/")
                .then();
    }

    @Step("Изменение данных о пользователе без токена")
    public ValidatableResponse changeUserDataWithoutToken(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .patch(PATH + "user/")
                .then();
    }
}
