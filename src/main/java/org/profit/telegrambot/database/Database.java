package org.profit.telegrambot.database;

import org.profit.telegrambot.model.CartProduct;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.model.Customer;
import org.profit.telegrambot.model.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.profit.telegrambot.ShopBot.conf;

public interface Database {
    List<Customer> customerList = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();
    List<CartProduct> cartProducts = new ArrayList<>();

    static Connection getConnection() {

        Connection conn;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(conf.getProperty("dbUrl"),
                                               conf.getProperty("dbUsername"),
                                               conf.getProperty("dbPassword"));
            if (conn != null) {
                System.out.println("Connection worked");
            } else {
                System.out.println("Connection failed");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return conn;
    }
}
