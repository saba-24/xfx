package com.example.finals;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    private DBDriver driver = new DBDriver();
    @Override
    public void start(Stage stage){
            Text title = new Text("Enter product details\nChart will be updated on page refresh");

            TextField idField = new TextField();
            idField.setPromptText("Product ID");

            TextField nameField = new TextField();
            nameField.setPromptText("Product Name");

            TextField priceField = new TextField();
            priceField.setPromptText("Price");

            Button addButton = new Button("Add Product");
            PieChart chart = driver.getChart();
            VBox root = new VBox(10, title, idField, nameField, priceField, addButton, chart);
            root.setPadding(new Insets(20));

            addButton.setOnAction(e -> {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    int price = Integer.parseInt(priceField.getText());

                    Product newProduct = new Product(id, name, price);
                    driver.insert(newProduct);
                    idField.clear();
                    nameField.clear();
                    priceField.clear();
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input: Please enter numeric values for ID and Price.");
                }
            });

            stage.setTitle("Add Product");
            stage.setScene(new Scene(root, 500, 550));
            stage.show();
    }
}
