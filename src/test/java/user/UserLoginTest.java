package user;

import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class UserLoginTest {
    User user;
    UserClient userClient;
    String accessToken;

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
        userClient.createNewUser(user);
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void loginUserWithCorrectData() {
        ValidatableResponse response = userClient.loginUser(user);
        response.assertThat().statusCode(SC_OK);
        accessToken = response.extract().path("accessToken").toString().substring(6).trim();
    }

    @Test
    @DisplayName("Авторизация с неверными логином и паролем")
    public void loginUserWithIncorrectData() {
        user.setEmail(user.getEmail() + "randomwords");
        user.setPassword(user.getPassword() + "123456789");
        ValidatableResponse response = userClient.loginUser(user);
        response.assertThat().statusCode(SC_UNAUTHORIZED);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
