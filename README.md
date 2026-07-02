# app

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

# dbdriver

package com.example.finals;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DBDriver {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/finals";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection connection;
    private Statement statement;
    public void connect(){
        try{
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void insert(Product product){
        try {
            this.connect();
            String sql = "INSERT INTO PRODUCTS(ID, PRICE, NAME) VALUES (" + product.id + ", " + product.price + ", '" + product.name + "')";
            this.execute(sql);
            this.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void select(int id){
        try{
            String sql = "SELECT * FROM PRODUCTS WHERE ID = " + id;
            this.execute(sql);
            this.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Product> selectAll(){
        try{
            this.connect();
            ArrayList<Product> res = new ArrayList<>();
            String sql = "SELECT * FROM PRODUCTS";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                Product p = new Product(rs.getInt("ID"), rs.getString("NAME"), rs.getInt("PRICE"));
                res.add(p);
            }
            this.close();
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    public HashMap<String, Integer> selectCount(){
//        try{
//            this.connect();
//            HashMap<String, Integer> res = new HashMap<>();
//            String sql = "SELECT NAME, COUNT(ID) AS TOTAL FROM PRODUCTS GROUP BY NAME";
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()){
//                res.put(rs.getString("NAME"), rs.getInt("TOTAL"));
//            }
//            this.close();
//            return res;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
public Map<String, Integer> selectCount() {
    try{
        String sql = "SELECT NAME, COUNT(ID) AS TOTAL FROM PRODUCTS GROUP BY NAME";
        List<Map.Entry<String, Integer>> results = new ArrayList<>();
        this.connect();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            results.add(new AbstractMap.SimpleEntry<>(
                    rs.getString("NAME"),
                    rs.getInt("TOTAL")
            ));
        }
        return results.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    } catch (SQLException e) {
        throw new RuntimeException("Database error", e);
    }
}
    public PieChart getChart(){
        try{
            Map<String, Integer> products = this.selectCount();
            PieChart chart = new PieChart();
            products.forEach((key, value) -> {
                chart.getData().add(new PieChart.Data(key, value));
            });
            return chart;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void execute(String sql){
        try{
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void close(){
        try{
            connection.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

