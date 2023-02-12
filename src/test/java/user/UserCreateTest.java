package user;

import clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class UserCreateTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUserWithCorrectData() {
        ValidatableResponse response = userClient.createNewUser(user);
        response.assertThat().statusCode(SC_OK);
        accessToken = response.extract().path("accessToken").toString().substring(6).trim();
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createAlreadyExistUser() {
        userClient.createNewUser(user);
        ValidatableResponse responseNumberTwo = userClient.createNewUser(user);
        responseNumberTwo.assertThat().statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание пользователя без заполнения одного из обязательных полей")
    public void createUserWithoutName() {
        user.setName(null);
        ValidatableResponse response = userClient.createNewUser(user);
        response.assertThat().statusCode(SC_FORBIDDEN);
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }
}
