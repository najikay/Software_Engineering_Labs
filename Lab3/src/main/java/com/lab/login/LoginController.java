package com.lab.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class LoginController {

    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private ArrayList<User> validUsers = new ArrayList<>();

    // Thread-safe collection to map a valid username to its current state
    private ConcurrentHashMap<String, UserState> userStates = new ConcurrentHashMap<>();

    public void setUsers(ArrayList<User> users) {
        this.validUsers = users;
        // Initialize state tracking only for valid users
        for (User u : users) {
            userStates.put(u.getName(), new UserState());
        }
    }

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        String inputUsername = username.getText();
        String inputPassword = password.getText();

        User matchedUser = null;
        boolean isValidUsername = false;

        // Verify if the username exists in our valid user list
        for (User user : validUsers) {
            if (user.getName().equals(inputUsername)) {
                isValidUsername = true;
                if (user.getPassword().equals(inputPassword)) {
                    matchedUser = user;
                }
                break;
            }
        }

        // If the email isn't in the system at all, just show a generic error
        if (!isValidUsername) {
            errorLabel.setText("User does not exist.");
            return;
        }

        // Fetch the state for this specific valid user
        UserState state = userStates.get(inputUsername);

        if (matchedUser != null) {
            // CREDENTIALS ARE CORRECT - Start Thread B
            CheckBlockThread checkThread = new CheckBlockThread(state);
            checkThread.start();

            try {
                checkThread.join(); // Wait for the thread to finish checking
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (checkThread.isUserBlocked()) {
                long remaining = Launcher.blockTimeSecs - ((System.currentTimeMillis() - state.blockTime) / 1000);
                errorLabel.setText("Blocked! Please wait " + remaining + " more seconds.");
            } else {
                errorLabel.setText("");
                // Switch scene on successful, unblocked login
                WelcomeController welcomeCtrl = com.lab.login.utils.SceneSwitcher.switchSceneAndCloseCurrent(
                        event, "/com/lab/login/welcome-view.fxml", "Welcome"
                );
                if (welcomeCtrl != null) {
                    welcomeCtrl.setUsername(inputUsername);
                }
            }
        } else {
            // CREDENTIALS ARE INCORRECT - Start Thread A
            UpdateAttemptsThread updateThread = new UpdateAttemptsThread(state);
            updateThread.start();

            try {
                updateThread.join(); // Wait for the thread to update the data
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state.blockTime > 0 && (System.currentTimeMillis() - state.blockTime) < (Launcher.blockTimeSecs * 1000L)) {
                errorLabel.setText("Max attempts reached! Blocked for " + Launcher.blockTimeSecs + " seconds.");
            } else {
                errorLabel.setText("Incorrect password. Attempt " + state.attempts + " of " + Launcher.maxAttempts);
            }
        }
    }

    // ==========================================
    // HELPER CLASSES & THREADS
    // ==========================================

    /**
     * Holds the mutable state for a single user.
     * Stores the attempts and the timestamp of the block
     */
    public static class UserState {
        public int attempts = 0;
        public long blockTime = 0;
    }

    /**
     * Thread A: Updates the failed attempts. If the max is reached, records the block time
     */
    class UpdateAttemptsThread extends Thread {
        private final UserState state;

        public UpdateAttemptsThread(UserState state) {
            this.state = state;
        }

        @Override
        public void run() {
            // Synchronize on the specific user's state to prevent race conditions
            synchronized (state) {
                // If a previous block has naturally expired, reset everything before incrementing
                if (state.blockTime > 0 && (System.currentTimeMillis() - state.blockTime) >= (Launcher.blockTimeSecs * 1000L)) {
                    state.attempts = 0;
                    state.blockTime = 0;
                }

                // If currently blocked, do not increment further
                if (state.blockTime > 0) {
                    return;
                }

                state.attempts++;

                // If max attempts reached, lock the user by recording the timestamp
                if (state.attempts >= Launcher.maxAttempts) {
                    state.blockTime = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     * Thread B: Checks if a user is currently blocked.
     */
    class CheckBlockThread extends Thread {
        private final UserState state;
        private boolean isBlocked;

        public CheckBlockThread(UserState state) {
            this.state = state;
        }

        @Override
        public void run() {
            // Synchronize on the specific user's state
            synchronized (state) {
                if (state.blockTime > 0) {
                    long elapsedMillis = System.currentTimeMillis() - state.blockTime;

                    // Check if the block duration has passed
                    if (elapsedMillis < (Launcher.blockTimeSecs * 1000L)) {
                        isBlocked = true;
                    } else {
                        // Time served: unblock the user and reset attempts
                        isBlocked = false;
                        state.attempts = 0;
                        state.blockTime = 0;
                    }
                } else {
                    isBlocked = false;
                    state.attempts = 0; // Reset attempts on successful login
                }
            }
        }

        public boolean isUserBlocked() {
            return isBlocked;
        }
    }
}