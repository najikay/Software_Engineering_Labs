package org.example;

import java.util.regex.Pattern;

/**
 * Represents a system user with a validated username (email) and password.
 * Follows encapsulation principles — fields are private and only accessible via getters.
 */
public class User {

    // -------------------------------------------------------------------------
    // Validation constants
    // -------------------------------------------------------------------------

    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 12;

    /**
     * Valid email format:
     *   local part  : letters, digits, and the characters: . _ - + %
     *   @           : separator
     *   domain part : must start with letter or digit, then letters/digits/./- allowed
     *   .           : separator
     *   TLD         : at least 2 letters only
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._\\-+%]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$"
    );

    /**
     * Allowed characters in a password: any printable non-whitespace ASCII character
     * (codes 33–126). This covers letters, digits, and all allowed special characters.
     */
    private static final Pattern PASSWORD_VALID_CHARS = Pattern.compile(
            "^[\\x21-\\x7E]+$"
    );

    // Patterns used to verify that each required character type is present
    private static final Pattern HAS_LETTER  = Pattern.compile("[a-zA-Z]");
    private static final Pattern HAS_DIGIT   = Pattern.compile("[0-9]");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*()]");

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String username;
    private final String password;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new User only if both the username and password pass all validation rules.
     * Throws an IllegalArgumentException with a descriptive message otherwise.
     *
     * @param username the user's email address
     * @param password the user's password
     */
    public User(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        this.username = username;
        this.password = password;
    }

    // -------------------------------------------------------------------------
    // Validation helpers
    // -------------------------------------------------------------------------

    /**
     * Validates the username field.
     * Checks length before format so the more specific length message is shown first.
     */
    private static void validateUsername(String username) {
        if (username.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }
        if (!EMAIL_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }
    }

    /**
     * Validates the password field.
     * Checks length first, then character legality, then required character types.
     */
    private static void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }

        // All characters must be from the allowed set, and at least one of each
        // required type (letter, digit, special character) must be present.
        boolean validChars    = PASSWORD_VALID_CHARS.matcher(password).matches();
        boolean hasLetter     = HAS_LETTER.matcher(password).find();
        boolean hasDigit      = HAS_DIGIT.matcher(password).find();
        boolean hasSpecial    = HAS_SPECIAL.matcher(password).find();

        if (!validChars || !hasLetter || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("Please enter a valid password");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // -------------------------------------------------------------------------
    // String representation
    // -------------------------------------------------------------------------

    /** Returns the user in the same format as the input file: "username password" */
    @Override
    public String toString() {
        return username + " " + password;
    }
}