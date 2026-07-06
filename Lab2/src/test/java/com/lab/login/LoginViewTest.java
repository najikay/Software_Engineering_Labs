package com.lab.login;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * TestFX unit tests for the Lab 2 login screen (Lab 9 - Task 3).
 */
@ExtendWith(ApplicationExtension.class)
public class LoginViewTest {

    private static final String VALID_USERNAME = "student@haifa.il";
    private static final String VALID_PASSWORD = "Pass123!";

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Inject a single known valid user instead of reading Users.txt,
        // so the tests are self-contained and independent of external files
        LoginController controller = fxmlLoader.getController();
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(VALID_USERNAME, VALID_PASSWORD));
        controller.setUsers(users);

        stage.setScene(scene);
        stage.show();
    }

    // 1) On startup, the username and password fields are empty
    @Test
    void test_fields_are_empty_on_startup(FxRobot robot) {
        FxAssert.verifyThat("#username", TextInputControlMatchers.hasText(""));
        FxAssert.verifyThat("#password", TextInputControlMatchers.hasText(""));
    }

    // 2) On startup, the login button shows the text "login"
    @Test
    void test_login_button_text(FxRobot robot) {
        FxAssert.verifyThat("#loginButton", LabeledMatchers.hasText("login"));
    }

    // 3) On startup, no error message is shown
    @Test
    void test_error_label_empty_on_startup(FxRobot robot) {
        FxAssert.verifyThat("#errorLabel", LabeledMatchers.hasText(""));
    }

    /**
     * Fills the login form and presses the login button.
     * The interaction runs on the JavaFX Application Thread via robot.interact(),
     * which is reliable on any machine (including headless/CI environments).
     */
    private void login(FxRobot robot, String user, String pass) {
        TextField usernameField = robot.lookup("#username").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup("#password").queryAs(PasswordField.class);
        Button loginButton = robot.lookup("#loginButton").queryButton();

        robot.interact(() -> {
            usernameField.setText(user);
            passwordField.setText(pass);
            loginButton.fire();
        });
    }

    // 4) Logging in with wrong credentials shows the error message
    @Test
    void test_wrong_credentials_show_error(FxRobot robot) {
        login(robot, "wrong@mail.com", "Wrong123!");

        FxAssert.verifyThat("#errorLabel", LabeledMatchers.hasText("user or password do not match"));
    }

    // 5) Pressing login with empty fields also shows the error message
    @Test
    void test_empty_fields_show_error(FxRobot robot) {
        login(robot, "", "");

        FxAssert.verifyThat("#errorLabel", LabeledMatchers.hasText("user or password do not match"));
    }

    // 6) Logging in with correct credentials opens the welcome screen
    //    with a personalized greeting
    @Test
    void test_successful_login_shows_welcome(FxRobot robot) {
        login(robot, VALID_USERNAME, VALID_PASSWORD);

        FxAssert.verifyThat("#welcomeLabel", LabeledMatchers.hasText("Welcome, " + VALID_USERNAME + "!"));
    }
}
