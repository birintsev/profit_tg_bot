package org.profit.telegrambot.service;

import org.profit.telegrambot.container.ComponentContainer;
import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.model.Product;
import org.profit.telegrambot.util.InlineKeyboardUtil;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;

import static org.profit.telegrambot.ShopBot.conf;


public class ProductService {

    public static void loadProductList() {
        Connection connection = Database.getConnection();
        if (connection != null) {

            try (Statement statement = connection.createStatement()) {

                Database.productList.clear();

                String query = " SELECT * FROM product; ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int categoryId = resultSet.getInt("category_id");
                    double price = resultSet.getDouble("price");
                    String image = resultSet.getString("image");
                    String description = resultSet.getString("description");

                    Product product = new Product(id, categoryId, name, price, image, description);

                    Database.productList.add(product);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void showProductList(String chatId, int categoryId){
        Connection connection = Database.getConnection();
        if (connection != null) {
            ProductService.loadProductList();

            for (Product product : Database.productList) {
                if ((chatId.equals(conf.getProperty("adminId"))) && product.getCategoryId().equals(categoryId)){
                    ComponentContainer.my_telegram_bot.sendPhoto(chatId, String.format("""
                                    🏆 Category: %s
                                    💻 Product: %s
                                    💸 Cost: %s
                                    ID: %s
                                    Description: %s""",
                                    Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getId(),
                                    product.getDescription()),
                                    product.getImage(),
                                    InlineKeyboardUtil.deleteOrUpdateProduct(product.getId()));

                } else if (product.getCategoryId().equals(categoryId)){
                    ComponentContainer.my_telegram_bot.sendPhoto(chatId, String.format("""
                                    💻 Product: %s
                                    💸 Cost: %s
                                    Description: %s""",
                                    product.getName(),
                                    product.getPrice(),
                                    product.getDescription()),
                                    product.getImage(),
                                    InlineKeyboardUtil.addToCart(product.getId()));

                }
            }
        }
    }

    public static void addProduct(Product product) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " INSERT INTO product(name, id, category_id, price, image, description)" +
                    " VALUES(?, ?, ?, ?, ?, ?); ";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                loadProductList();
                product.setId(Database.productList.size() + 1);

                preparedStatement.setString(1, product.getName());
                preparedStatement.setInt(2, product.getId());
                preparedStatement.setInt(3, product.getCategoryId());
                preparedStatement.setDouble(4, product.getPrice());
                preparedStatement.setString(5, product.getImage());
                preparedStatement.setString(6, product.getDescription());

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println(executeUpdate);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        loadProductList();
    }

    public static void deleteProduct(Integer id, String pointer) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = null;

            if (Objects.equals(pointer, "category_id")){
                query = " DELETE FROM product WHERE category_id = ? ;";
            } else if (Objects.equals(pointer, "product_id")){
                query = " DELETE FROM product WHERE id = ? ;";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, id);
                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        loadProductList();
    }

    public static Product getProductById(Integer productId) {
        Optional<Product> optional = Database.productList.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst();
        return optional.orElse(null);
    }

    public static void renameProduct(Integer id, String text, Integer categoryId) {
        Connection connection = Database.getConnection();
        if (connection != null) {


            String query = " UPDATE product SET name = ? WHERE id = ? AND category_id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, text);
                preparedStatement.setInt(2, id);
                preparedStatement.setInt(3, categoryId);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        loadProductList();
    }


    public static void updateProductPrice(Integer id, Double productPrice) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " UPDATE product SET price = ? WHERE id = ? ;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setDouble(1, productPrice);
                preparedStatement.setInt(2, id);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        loadProductList();
    }

    public static void updateProductDescription(Integer id, String text){
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " UPDATE product SET description = ? WHERE id = ? ;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, text);
                preparedStatement.setInt(2, id);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        loadProductList();
    }
}
