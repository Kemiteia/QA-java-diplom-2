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

public class CreateOrderWithAuthTest {
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
    @DisplayName("Создание заказа с авторизацией и списком ингредиентов")
    public void createOrderWithAuthAndIngredients() {
        ValidatableResponse responseCreateAuth1 = orderClient.createOrderWithToken(accessToken, correctIngredients);
        Assert.assertEquals(SC_OK, responseCreateAuth1.extract().statusCode());
        Assert.assertEquals(true, responseCreateAuth1.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без списка ингредиентов")
    public void createOrderWithAuthWithoutIngredients() {
        ValidatableResponse responseCreateAuth2 = orderClient.createOrderWithToken(accessToken, emptyIngredients);
        Assert.assertEquals(SC_BAD_REQUEST, responseCreateAuth2.extract().statusCode());
        Assert.assertEquals(false, responseCreateAuth2.extract().path("success"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хэшем ингредиентов")
    public void createOrderWithAuthAndWrongIngredients() {
        ValidatableResponse responseCreateAuth3 = orderClient.createOrderWithToken(accessToken, wrongIngredients);
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, responseCreateAuth3.extract().statusCode());
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
