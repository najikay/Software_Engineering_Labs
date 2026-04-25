package com.lab.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class LoginApp extends Application {

    // ==========================================
    // CONFIGURATION PATHS
    // ==========================================
    private static final String DEFAULT_USERS_FILE = "Users.txt";
    // ==========================================

    @Override
    public void start(Stage stage) throws IOException {
        // Grab the file path from run arguments (or use the default config above)
        java.util.List<String> args = getParameters().getRaw();
        String filePath = args.isEmpty() ? DEFAULT_USERS_FILE : args.get(0);

        // Load users using the separated logic
        ArrayList<User> validUsers = UsersApp.loadUsers(filePath);

        FXMLLoader fxmlLoader = new FXMLLoader(LoginApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Inject the users into the controller
        LoginController controller = fxmlLoader.getController();
        controller.setUsers(validUsers);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}