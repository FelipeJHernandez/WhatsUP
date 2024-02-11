module com.example.whatsup2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.whatsup2 to javafx.fxml;
    exports com.example.whatsup2;
}