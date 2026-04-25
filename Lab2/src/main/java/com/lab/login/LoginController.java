package com.lab.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class LoginController {

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private ArrayList<User> validUsers = new ArrayList<>();

    // This is called by LoginApp to inject the loaded users
    public void setUsers(ArrayList<User> users) {
        this.validUsers = users;
    }

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        String inputUsername = username.getText();
        String inputPassword = password.getText();
        boolean isMatch = false;

        for (User user : validUsers) {
            if (user.getName().equals(inputUsername) && user.getPassword().equals(inputPassword)) {
                isMatch = true;
                break;
            }
        }

        if (isMatch) {
            errorLabel.setText("");

            // Open the new window and grab its controller
            WelcomeController welcomeCtrl = com.lab.login.utils.SceneSwitcher.switchSceneAndCloseCurrent(
                    event, "/com/lab/login/welcome-view.fxml", "Welcome"
            );

            // Pass the personalized name!
            if (welcomeCtrl != null) {
                welcomeCtrl.setUsername(inputUsername);
            }
        } else {
            errorLabel.setText("user or password do not match");
        }
    }
}