package order;

import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class CreateOrderWithoutAuthTest {
    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    String correctIngredients = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa70\"]\n}";
    String emptyIngredients = "{\n\"ingredients\": []\n}";
    String wrongIngredients = "{\n\"ingredients\": [\"randomingredients1\",\"randomingredients2\"]\n}";

    @Before
    public void setUp() {
        user = User.generateUser();
        orderClient = new OrderClient();
        userClient = new UserClient();

        ValidatableResponse createUserResponse = userClient.createNewUser(user);
        accessToken = createUserResponse.extract().path("accessToken").toString().substring(6).trim();
    }

    @Test
    @DisplayName("Создание заказа без авторизации, со списком ингредиентов")
    public void createOrderWithoutAuthWithIngredients() {
        ValidatableResponse responseCreateWithoutAuth1 = orderClient.createOrderWithoutToken(correctIngredients);
        Assert.assertEquals(SC_OK, responseCreateWithoutAuth1.extract().statusCode());
        Assert.assertEquals(true, responseCreateWithoutAuth1.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и списка ингредиентов")
    public void createOrderWithoutAuthAndIngredients() {
        ValidatableResponse responseCreateWithoutAuth2 = orderClient.createOrderWithoutToken(emptyIngredients);
        Assert.assertEquals(SC_BAD_REQUEST, responseCreateWithoutAuth2.extract().statusCode());
        Assert.assertEquals(false, responseCreateWithoutAuth2.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, с неверным хэшем ингредиентов")
    public void createOrderWithoutAuthAndWrongIngredients() {
        ValidatableResponse responseCreateWithoutAuth3 = orderClient.createOrderWithoutToken(wrongIngredients);
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, responseCreateWithoutAuth3.extract().statusCode());
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
