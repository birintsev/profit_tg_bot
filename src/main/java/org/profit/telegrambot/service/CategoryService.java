package org.profit.telegrambot.service;


import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.util.InlineKeyboardUtil;

import java.sql.*;
import java.util.NoSuchElementException;

import static org.profit.telegrambot.container.ComponentContainer.my_telegram_bot;

public class CategoryService {
    public static Category getCategoryById(Integer id) {

        loadCategoryList();

        for (Category category : Database.categoryList) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    public static void loadCategoryList() {
        Connection connection = Database.getConnection();
        if (connection != null) {

            try (Statement statement = connection.createStatement()) {

                Database.categoryList.clear();

                String query = " SELECT * FROM category ";

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    boolean deleted = resultSet.getBoolean("deleted");

                    Category category = new Category(id, name, deleted);

                    Database.categoryList.add(category);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public static void addCategory(Category category) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " INSERT INTO category(name, id)" + " VALUES(?, ?); ";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                loadCategoryList();
                category.setId(Database.categoryList.stream()
                        .mapToInt(Category::getId)
                        .max().orElseThrow(NoSuchElementException::new) + 1);
                preparedStatement.setString(1, category.getName());
                preparedStatement.setInt(2, category.getId());

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println(executeUpdate);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        loadCategoryList();
    }

    public static void deleteCategory(Integer id) {
        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " DELETE FROM category WHERE id = ?; ";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, id);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        ProductService.deleteProduct(id, "category_id");
        loadCategoryList();
    }

    public static void renameCategory(int categoryId, String newName, String chatId){

        Connection connection = Database.getConnection();
        if (connection != null) {

            String query = " UPDATE category SET name = ? WHERE id = ?; ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, newName);
                preparedStatement.setInt(2, categoryId);

                int executeUpdate = preparedStatement.executeUpdate();
                System.out.println("executeUpdate = " + executeUpdate);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        loadCategoryList();
        my_telegram_bot.sendMessage(chatId, "Category name was changed", InlineKeyboardUtil.categoryShow());
    }
}

