module com.lab.login {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.lab.login to javafx.fxml;
    exports com.lab.login;
}