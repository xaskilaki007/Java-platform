module com.example.demoApp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}