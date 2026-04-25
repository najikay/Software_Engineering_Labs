package com.lab.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UsersApp {
    /**
     * Reads a users file from the given path, validates each line,
     * and returns a list of only the valid users.
     */
    public static ArrayList<User> loadUsers(String filePath) {
        ArrayList<User> users = new ArrayList<>();
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) continue;

                String[] parts = line.trim().split("\\s+");
                String uname = parts.length > 0 ? parts[0] : "";
                String pass = parts.length > 1 ? parts[1] : "";

                try {
                    users.add(new User(uname, pass));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipped invalid user in file: " + uname);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + filePath);
        }
        return users;
    }
}