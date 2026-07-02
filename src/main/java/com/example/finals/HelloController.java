package com.example.finals;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        DBDriver driver = new DBDriver();
        driver.connect();
        driver.insert(new Product(1, "controller", 50));
    }
}
