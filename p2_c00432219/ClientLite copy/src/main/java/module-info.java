module com.example.clientlite {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.clientlite to javafx.fxml;
    exports com.example.clientlite;
}