package user;

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

public class UserUpdateDataTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
        ValidatableResponse responseCreate = userClient.createNewUser(user);
        responseCreate.assertThat().statusCode(SC_OK);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        responseLogin.assertThat().statusCode(SC_OK);
        accessToken = responseLogin.extract().path("accessToken").toString().substring(6).trim();
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void UpdateUserDataWithAuth() {
        user.setEmail(user.getEmail() + "randomwords");
        user.setName(user.getName() + "randomname");
        ValidatableResponse responseUpdate = userClient.changeUserDataWithToken(accessToken, user);
        Assert.assertEquals(SC_OK, responseUpdate.extract().statusCode());
        Assert.assertEquals(true, responseUpdate.extract().path("success"));
        responseUpdate.assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void UpdateUserDataWithoutAuth() {
        user.setEmail(user.getEmail() + "randomwords");
        user.setName(user.getName() + "randomname");
        ValidatableResponse responseUpdate = userClient.changeUserDataWithoutToken(user);
        Assert.assertEquals(SC_UNAUTHORIZED, responseUpdate.extract().statusCode());
        Assert.assertEquals(false, responseUpdate.extract().path("success"));
        responseUpdate.assertThat().statusCode(SC_UNAUTHORIZED);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
