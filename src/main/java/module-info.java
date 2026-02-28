module com.example.demo {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires bcrypt;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.dao.impl to javafx.fxml;

    exports com.example.demo;
}