module com.example.monopoly_tm {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.sql;
    requires java.desktop;


    opens com.example.monopoly_tm to javafx.fxml;
    exports com.example.monopoly_tm;
}