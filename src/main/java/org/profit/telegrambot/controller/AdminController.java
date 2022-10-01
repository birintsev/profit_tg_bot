package org.profit.telegrambot.controller;


import org.profit.telegrambot.enums.AdminStatus;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.model.Product;
import org.profit.telegrambot.service.CategoryService;
import org.profit.telegrambot.service.ProductService;
import org.profit.telegrambot.util.InlineKeyboardUtil;
import org.profit.telegrambot.util.KeyboardUtil;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;

import static org.profit.telegrambot.ShopBot.conf;
import static org.profit.telegrambot.container.ComponentContainer.*;

public class AdminController {
    public void handleMessage(String chatId, Message message) {
        if (message.hasText()) {
            handleText(chatId, message);
        } else if (message.hasPhoto()) {
            handlePhoto(chatId, message);
        }
    }

    private void handlePhoto(String chatId, Message message) {
        List<PhotoSize> photoSizeList = message.getPhoto();

        if (stepMap.containsKey(chatId)) {
            Product product = productMap.get(chatId);

            if (stepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_PRICE)) {

                product.setImage(photoSizeList.get(photoSizeList.size() - 1).getFileId());

                stepMap.put(chatId, AdminStatus.IMAGE_SENT);
                my_telegram_bot.sendMessage(chatId, "Enter product description: ", (InlineKeyboardMarkup) null);

            }
        }
    }

    public void handleCallBack(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        int messageId = message.getMessageId();

        if (data.startsWith("category")) {

            int categoryId = Integer.parseInt(data.split("/")[1]);
            categoryIdMap.put(chatId, categoryId);
            my_telegram_bot.deleteMessage(chatId, messageId);

            switch (stepMap.get(chatId)){
                case CLICKED_ADD_PRODUCT -> {

                    my_telegram_bot.sendMessage(chatId,
                            "Enter the product name:",
                            (InlineKeyboardMarkup) null);
                    stepMap.put(chatId, AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT);
                    Product product = productMap.get(chatId);
                    product.setCategoryId(categoryId);
                }
                case DELETE_PRODUCT -> {

                    ProductService.showProductList(chatId, categoryId);
                    my_telegram_bot.sendMessage(chatId,
                            "Enter the id of the product you want to delete:",
                            (InlineKeyboardMarkup) null);
                }
                case SHOW_PRODUCTS -> {

                    my_telegram_bot.sendMessage(chatId,
                            "Products from category %s:".formatted(
                                    CategoryService.
                                    getCategoryById(categoryId).
                                    getName()),
                                    (InlineKeyboardMarkup) null);
                    ProductService.showProductList(chatId, categoryId);
                    my_telegram_bot.sendMessage(chatId,
                            "Back?",
                            InlineKeyboardUtil.backToCategories());
                }
                case UPDATE_PRODUCT -> {

                    ProductService.showProductList(chatId, categoryId);
                    my_telegram_bot.sendMessage(chatId,
                            "Choose from the menu:",
                            InlineKeyboardUtil.updateProduct());
                    categoryIdMap.put(chatId, categoryId);
                }
                case DELETE_CATEGORY -> {

                    my_telegram_bot.sendMessage(chatId,
                            "Are you sure you want to delete this category?",
                            InlineKeyboardUtil.confirmDeleteCategory());
                }
                case RENAME_CATEGORY -> {

                    my_telegram_bot.sendMessage(chatId,
                            "Enter a new name:",
                            (InlineKeyboardMarkup) null);
                }
            }

        } else if (data.startsWith("delete_product/")) {
            int productId = Integer.parseInt(data.split("/")[1]);

            my_telegram_bot.deleteMessage(chatId, messageId);
            ProductService.deleteProduct(productId, "product_id");
            productMap.remove(chatId);
            my_telegram_bot.sendMessage(chatId,
                    "The product has been removed",
                    (InlineKeyboardMarkup) null);
        } else if (data.startsWith("update_product/")) {
            productIdMap.put(chatId, Integer.parseInt(data.split("/")[1]));
            my_telegram_bot.editMessage(chatId,
                    "Choose what you want to change:",
                    messageId,
                    InlineKeyboardUtil.updateProduct());
        } else if (data.startsWith("change_")){

            String [] arr = data.split("_");
            String toChange = arr[1];

            Product product = ProductService.getProductById(productIdMap.get(chatId));
            my_telegram_bot.sendPhoto(chatId, String.format("""
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
                     null);

            switch (toChange) {
                case "name" ->{
                    stepMap.put(chatId, AdminStatus.CHANGE_NAME);
                    my_telegram_bot.sendMessage(chatId,
                            "Enter a new name:",
                            (InlineKeyboardMarkup) null);
                }
                case "description" ->{
                    stepMap.put(chatId, AdminStatus.CHANGE_DESCRIPTION);
                    my_telegram_bot.sendMessage(chatId,
                            "Enter a new description:",
                            (InlineKeyboardMarkup) null);
                }
                case "price" ->{
                    stepMap.put(chatId, AdminStatus.CHANGE_PRICE);
                    my_telegram_bot.sendMessage(chatId,
                            "Enter a new price:",
                            (InlineKeyboardMarkup) null);
                }
            }

        } else {
            my_telegram_bot.deleteMessage(chatId, messageId);

             switch (data) {
                 case "add_new" -> {
                     my_telegram_bot.sendMessage(chatId,
                             "Choose what you want to add:",
                             InlineKeyboardUtil.addNew());
                 }
                 case "add_category" -> {
                     stepMap.put(chatId, AdminStatus.CLICKED_ADD_CATEGORY);
                     my_telegram_bot.sendMessage(chatId,
                             "Enter a category name: ",
                             (InlineKeyboardMarkup) null);

                 }
                 case "rename_category" -> {
                     stepMap.put(chatId, AdminStatus.RENAME_CATEGORY);
                     my_telegram_bot.sendMessage(chatId,
                             "Enter the category you want to rename:",
                             InlineKeyboardUtil.categoryShow());

                 }
                 case "delete_category" -> {
                     stepMap.put(chatId, AdminStatus.DELETE_CATEGORY);
                     my_telegram_bot.sendMessage(chatId,
                             "Enter the category you want to delete:",
                             InlineKeyboardUtil.categoryShow());

                 }
                 case "delete_category_commit" -> {
                     CategoryService.deleteCategory(categoryIdMap.get(chatId));
                     my_telegram_bot.sendMessage(chatId,
                             "Category was deleted",
                             InlineKeyboardUtil.categoryShow());
                     stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);
                 }
                 case "delete_category_cancel", "to_main_menu" ->{
                     my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());
                 }
                 case "show_category_list" -> {
                     my_telegram_bot.sendMessage(chatId,
                             "All categories:",
                             InlineKeyboardUtil.categoryShow());
                     stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);

                 }
                 case "add_category_commit" -> {
                     Category category = categoryMap.get(chatId);
                     CategoryService.addCategory(category);

                     categoryMap.remove(chatId);
                     stepMap.remove(chatId);
                     productMap.remove(chatId);

                     my_telegram_bot.sendMessage(chatId,
                             category.getName() + " saved.\n\n" + "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "add_category_cancel" -> {
                     categoryMap.remove(chatId);
                     stepMap.remove(chatId);

                     my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "add_product" -> {
                     my_telegram_bot.sendMessage(chatId,
                             "Choose one of the categories:",
                             InlineKeyboardUtil.categoryShowForProducts());

                     productMap.remove(chatId);
                     stepMap.put(chatId, AdminStatus.CLICKED_ADD_PRODUCT);
                     productMap.put(chatId, new Product());

                 }
                 case "add_product_commit" -> {
                     Product product = productMap.get(chatId);
                     ProductService.addProduct(product);

                     productMap.remove(chatId);
                     stepMap.remove(chatId);
                     categoryMap.remove(chatId);

                     my_telegram_bot.sendMessage(chatId,
                             product.getName() + " saved.\n\n" + "Select an action:",
                             InlineKeyboardUtil.backToCategories());

                 }
                 case "add_product_cancel" -> {
                     productMap.remove(chatId);
                     stepMap.remove(chatId);

                     my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "show_product_list" -> {
                     my_telegram_bot.sendMessage(chatId,
                             "Choose one of the categories:",
                             InlineKeyboardUtil.categoryShowForProducts());
                     stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);

                 }
             }
         }
    }
    private void handleText(String chatId, Message message) {
        String text = message.getText();

        if (text.equals("/start")) {
            my_telegram_bot.sendMessage(chatId, "Select an action:", KeyboardUtil.adminMenu());

        } else if (text.equals("Categories and Products\nCRUD")) {
            my_telegram_bot.sendMessage(chatId, "Select an action:", InlineKeyboardUtil.categoryCRUDMenu());
            System.out.println(message.getFrom().getId());

        } else if (stepMap.containsKey(chatId)) {

            Product product = productMap.get(chatId);
            switch (stepMap.get(chatId)) {
                case SELECT_CATEGORY_FOR_ADD_PRODUCT -> {

                    product.setName(text);
                    stepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_NAME);
                    my_telegram_bot.sendMessage(chatId,
                            "Enter the price of the product:",
                            (InlineKeyboardMarkup) null);
                }
                case ENTERED_PRODUCT_NAME -> {

                    double price = 0;
                    try {
                        price = Double.parseDouble(text.trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (price <= 0) {
                        my_telegram_bot.sendMessage(chatId,
                                "The price was entered incorrectly, please re-enter the price:",
                                (InlineKeyboardMarkup) null);
                    } else {
                        product.setPrice(price);
                        stepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_PRICE);
                        my_telegram_bot.sendMessage(chatId,
                                "Send a picture of the product:",
                                (InlineKeyboardMarkup) null);
                    }
                }
                case IMAGE_SENT -> {

                    product.setDescription(text);
                    my_telegram_bot.sendPhoto(chatId, String.format("""
                                Category: %s
                                Product: %s\s
                                Cost: %s $
                                Description: %s""",
                                    Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getDescription()),
                                    product.getImage(), null);

                    my_telegram_bot.sendMessage(chatId,
                            "Add the following product to the database?",
                            InlineKeyboardUtil.confirmAddProduct());
                }
                case CHANGE_NAME -> {

                    ProductService.renameProduct(productIdMap.get(chatId), text, categoryIdMap.get(chatId));
                    crudStepMap.remove(chatId);
                    my_telegram_bot.sendMessage(chatId,
                            "The product name was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CHANGE_PRICE -> {

                    ProductService.updateProductPrice(productIdMap.get(chatId), Double.parseDouble(text));
                    crudStepMap.remove(chatId);
                    my_telegram_bot.sendMessage(chatId,
                            "The product price was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CHANGE_DESCRIPTION -> {

                    ProductService.updateProductDescription(productIdMap.get(chatId), text);
                    crudStepMap.remove(chatId);
                    my_telegram_bot.sendMessage(chatId,
                            "The product description was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CLICKED_ADD_CATEGORY -> {

                    categoryMap.put(chatId, new Category(null, text, false));
                    stepMap.remove(chatId);
                    my_telegram_bot.sendMessage(chatId,
                            "Add category " + text + " ?",
                            InlineKeyboardUtil.confirmAddCategory());
                }
                case RENAME_CATEGORY -> {

                    CategoryService.renameCategory(categoryIdMap.get(chatId), text, chatId);
                    stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);
                }
            }

        }
    }
    public static void notificationToAdmin(String text, Message message) {

        long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        my_telegram_bot.sendMessage(conf.getProperty("adminId"),
                String.format("A message from @%s: \n%s.\n\nUserId: %s", userName, text, userId),
                (InlineKeyboardMarkup) null);
    }
}
