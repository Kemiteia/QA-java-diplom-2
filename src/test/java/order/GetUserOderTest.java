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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class GetUserOderTest {
    User user;
    OrderClient orderClient;
    UserClient userClient;
    String accessToken;
    String correctIngredients = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa70\"]\n}";

    @Before
    public void setUp() {
        user = User.generateUser();
        orderClient = new OrderClient();
        userClient = new UserClient();

        ValidatableResponse createUserResponse = userClient.createNewUser(user);
        accessToken = createUserResponse.extract().path("accessToken").toString().substring(6).trim();
        orderClient.createOrderWithToken(accessToken, correctIngredients);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    public void getOrderFromAnAuthUser() {
        ValidatableResponse responseAuthUser = orderClient.getOrderListWithToken(accessToken);
        Assert.assertEquals(SC_OK, responseAuthUser.extract().statusCode());
        Assert.assertEquals(true, responseAuthUser.extract().path("success"));
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    public void getOrderFromAnUnauthUser() {
        ValidatableResponse responseUnauthUser = orderClient.getOrderListWithoutToken();
        Assert.assertEquals(SC_UNAUTHORIZED, responseUnauthUser.extract().statusCode());
        Assert.assertEquals(false, responseUnauthUser.extract().path("success"));
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
