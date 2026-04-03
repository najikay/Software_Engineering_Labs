package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Entry point for the User Management application.
 *
 * Reads user records from stdin, validates each one, collects the valid
 * users into a list, sorts them alphabetically by username, and prints the
 * result to stdout.  Validation errors are written to stderr so they do not
 * interfere with the expected stdout output.
 *
 * Run with:  java UsersApp < users.txt > out.txt
 */
public class UsersApp {

    public static void main(String[] args) {

        ArrayList<User> users = new ArrayList<>();

        // ----------------------------------------------------------------
        // Read and validate users from stdin
        // ----------------------------------------------------------------
        Scanner reader = new Scanner(System.in);

        while (reader.hasNextLine()) {
            String line = reader.nextLine();

            // Skip blank lines
            if (line.isBlank()) continue;

            // Split on any run of whitespace to get [username, password]
            String[] parts = line.trim().split("\\s+");

            String username = parts.length > 0 ? parts[0] : "";
            String password = parts.length > 1 ? parts[1] : "";

            try {
                users.add(new User(username, password));
            } catch (IllegalArgumentException e) {
                // Print the offending line and the reason to stderr
                System.err.println(line + " --> " + e.getMessage());
            }
        }

        reader.close();

        // ----------------------------------------------------------------
        // Sort valid users alphabetically by username
        // ----------------------------------------------------------------
        Collections.sort(users, (u1, u2) -> u1.getName().compareTo(u2.getName()));

        // ----------------------------------------------------------------
        // Print sorted users to stdout (format: "username password")
        // ----------------------------------------------------------------
        for (User user : users) {
            System.out.println(user);
        }
    }
}