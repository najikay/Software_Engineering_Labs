package com.lab.login;

import javafx.application.Application;
import java.util.Scanner;

public class Launcher {
    // Static variables to hold our runtime configurations
    public static int maxAttempts;
    public static int blockTimeSecs;

    public static void main(String[] args) {
        // Read parameters from the command line at runtime 
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter maximum failed login attempts: ");
        maxAttempts = scanner.nextInt(); // Parameter 1 

        System.out.print("Enter block duration in seconds: ");
        blockTimeSecs = scanner.nextInt(); // Parameter 2

        // Launch the UI only after parameters are received 
        Application.launch(LoginApp.class, args);
    }
}