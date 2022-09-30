package org.profit.telegrambot.controller;

import org.profit.telegrambot.container.ComponentContainer;
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

        if (ComponentContainer.stepMap.containsKey(chatId)) {
            Product product = ComponentContainer.productMap.get(chatId);

            if (ComponentContainer.stepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_PRICE)) {

                product.setImage(photoSizeList.get(photoSizeList.size() - 1).getFileId());

                ComponentContainer.stepMap.put(chatId, AdminStatus.IMAGE_SENT);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Enter product description: ", (InlineKeyboardMarkup) null);

            }
        }
    }

    public void handleCallBack(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        int messageId = message.getMessageId();

        if (data.startsWith("category")) {

            int categoryId = Integer.parseInt(data.split("/")[1]);
            ComponentContainer.categoryIdMap.put(chatId, categoryId);
            ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);

            switch (ComponentContainer.stepMap.get(chatId)){
                case CLICKED_ADD_PRODUCT -> {

                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter the product name:",
                            (InlineKeyboardMarkup) null);
                    ComponentContainer.stepMap.put(chatId, AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT);
                    Product product = ComponentContainer.productMap.get(chatId);
                    product.setCategoryId(categoryId);
                }
                case DELETE_PRODUCT -> {

                    ProductService.showProductList(chatId, categoryId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter the id of the product you want to delete:",
                            (InlineKeyboardMarkup) null);
                }
                case SHOW_PRODUCTS -> {

                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Products from category %s:".formatted(
                                    CategoryService.
                                    getCategoryById(categoryId).
                                    getName()),
                                    (InlineKeyboardMarkup) null);
                    ProductService.showProductList(chatId, categoryId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Back?",
                            InlineKeyboardUtil.backToCategories());
                }
                case UPDATE_PRODUCT -> {

                    ProductService.showProductList(chatId, categoryId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Choose from the menu:",
                            InlineKeyboardUtil.updateProduct());
                    ComponentContainer.categoryIdMap.put(chatId, categoryId);
                }
                case DELETE_CATEGORY -> {

                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Are you sure you want to delete this category?",
                            InlineKeyboardUtil.confirmDeleteCategory());
                }
                case RENAME_CATEGORY -> {

                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter a new name:",
                            (InlineKeyboardMarkup) null);
                }
            }

        } else if (data.startsWith("delete_product/")) {
            int productId = Integer.parseInt(data.split("/")[1]);

            ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
            ProductService.deleteProduct(productId, "product_id");
            ComponentContainer.productMap.remove(chatId);
            ComponentContainer.my_telegram_bot.sendMessage(chatId,
                    "The product has been removed",
                    (InlineKeyboardMarkup) null);
        } else if (data.startsWith("update_product/")) {
            ComponentContainer.productIdMap.put(chatId, Integer.parseInt(data.split("/")[1]));
            ComponentContainer.my_telegram_bot.editMessage(chatId,
                    "Choose what you want to change:",
                    messageId,
                    InlineKeyboardUtil.updateProduct());
        } else if (data.startsWith("change_")){

            String [] arr = data.split("_");
            String toChange = arr[1];

            Product product = ProductService.getProductById(ComponentContainer.productIdMap.get(chatId));
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
                     null);

            switch (toChange) {
                case "name" ->{
                    ComponentContainer.stepMap.put(chatId, AdminStatus.CHANGE_NAME);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter a new name:",
                            (InlineKeyboardMarkup) null);
                }
                case "description" ->{
                    ComponentContainer.stepMap.put(chatId, AdminStatus.CHANGE_DESCRIPTION);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter a new description:",
                            (InlineKeyboardMarkup) null);
                }
                case "price" ->{
                    ComponentContainer.stepMap.put(chatId, AdminStatus.CHANGE_PRICE);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Enter a new price:",
                            (InlineKeyboardMarkup) null);
                }
            }

        } else {
            ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);

             switch (data) {
                 case "add_new" -> {
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Choose what you want to add:",
                             InlineKeyboardUtil.addNew());
                 }
                 case "add_category" -> {
                     ComponentContainer.stepMap.put(chatId, AdminStatus.CLICKED_ADD_CATEGORY);
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Enter a category name: ",
                             (InlineKeyboardMarkup) null);

                 }
                 case "rename_category" -> {
                     ComponentContainer.stepMap.put(chatId, AdminStatus.RENAME_CATEGORY);
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Enter the category you want to rename:",
                             InlineKeyboardUtil.categoryShow());

                 }
                 case "delete_category" -> {
                     ComponentContainer.stepMap.put(chatId, AdminStatus.DELETE_CATEGORY);
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Enter the category you want to delete:",
                             InlineKeyboardUtil.categoryShow());

                 }
                 case "delete_category_commit" -> {
                     CategoryService.deleteCategory(ComponentContainer.categoryIdMap.get(chatId));
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Category was deleted",
                             InlineKeyboardUtil.categoryShow());
                     ComponentContainer.stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);
                 }
                 case "delete_category_cancel", "to_main_menu" ->{
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());
                 }
                 case "show_category_list" -> {
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "All categories:",
                             InlineKeyboardUtil.categoryShow());
                     ComponentContainer.stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);

                 }
                 case "add_category_commit" -> {
                     Category category = ComponentContainer.categoryMap.get(chatId);
                     CategoryService.addCategory(category);

                     ComponentContainer.categoryMap.remove(chatId);
                     ComponentContainer.stepMap.remove(chatId);
                     ComponentContainer.productMap.remove(chatId);

                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             category.getName() + " saved.\n\n" + "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "add_category_cancel" -> {
                     ComponentContainer.categoryMap.remove(chatId);
                     ComponentContainer.stepMap.remove(chatId);

                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "add_product" -> {
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Choose one of the categories:",
                             InlineKeyboardUtil.categoryShowForProducts());

                     ComponentContainer.productMap.remove(chatId);
                     ComponentContainer.stepMap.put(chatId, AdminStatus.CLICKED_ADD_PRODUCT);
                     ComponentContainer.productMap.put(chatId, new Product());

                 }
                 case "add_product_commit" -> {
                     Product product = ComponentContainer.productMap.get(chatId);
                     ProductService.addProduct(product);

                     ComponentContainer.productMap.remove(chatId);
                     ComponentContainer.stepMap.remove(chatId);
                     ComponentContainer.categoryMap.remove(chatId);

                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             product.getName() + " saved.\n\n" + "Select an action:",
                             InlineKeyboardUtil.backToCategories());

                 }
                 case "add_product_cancel" -> {
                     ComponentContainer.productMap.remove(chatId);
                     ComponentContainer.stepMap.remove(chatId);

                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Select an action:",
                             InlineKeyboardUtil.categoryCRUDMenu());

                 }
                 case "show_product_list" -> {
                     ComponentContainer.my_telegram_bot.sendMessage(chatId,
                             "Choose one of the categories:",
                             InlineKeyboardUtil.categoryShowForProducts());
                     ComponentContainer.stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);

                 }
             }
         }
    }
    private void handleText(String chatId, Message message) {
        String text = message.getText();

        if (text.equals("/start")) {
            ComponentContainer.my_telegram_bot.sendMessage(chatId, "Select an action:", KeyboardUtil.adminMenu());

        } else if (text.equals("Categories and Products\nCRUD")) {
            ComponentContainer.my_telegram_bot.sendMessage(chatId, "Select an action:", InlineKeyboardUtil.categoryCRUDMenu());
            System.out.println(message.getFrom().getId());

        } else if (ComponentContainer.stepMap.containsKey(chatId)) {

            Product product = ComponentContainer.productMap.get(chatId);
            switch (ComponentContainer.stepMap.get(chatId)) {
                case SELECT_CATEGORY_FOR_ADD_PRODUCT -> {

                    product.setName(text);
                    ComponentContainer.stepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_NAME);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
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
                        ComponentContainer.my_telegram_bot.sendMessage(chatId,
                                "The price was entered incorrectly, please re-enter the price:",
                                (InlineKeyboardMarkup) null);
                    } else {
                        product.setPrice(price);
                        ComponentContainer.stepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_PRICE);
                        ComponentContainer.my_telegram_bot.sendMessage(chatId,
                                "Send a picture of the product:",
                                (InlineKeyboardMarkup) null);
                    }
                }
                case IMAGE_SENT -> {

                    product.setDescription(text);
                    ComponentContainer.my_telegram_bot.sendPhoto(chatId, String.format("""
                                Category: %s
                                Product: %s\s
                                Cost: %s $
                                Description: %s""",
                                    Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getDescription()),
                                    product.getImage(), null);

                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Add the following product to the database?",
                            InlineKeyboardUtil.confirmAddProduct());
                }
                case CHANGE_NAME -> {

                    ProductService.renameProduct(ComponentContainer.productIdMap.get(chatId), text, ComponentContainer.categoryIdMap.get(chatId));
                    ComponentContainer.crudStepMap.remove(chatId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "The product name was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CHANGE_PRICE -> {

                    ProductService.updateProductPrice(ComponentContainer.productIdMap.get(chatId), Double.parseDouble(text));
                    ComponentContainer.crudStepMap.remove(chatId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "The product price was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CHANGE_DESCRIPTION -> {

                    ProductService.updateProductDescription(ComponentContainer.productIdMap.get(chatId), text);
                    ComponentContainer.crudStepMap.remove(chatId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "The product description was changed",
                            InlineKeyboardUtil.backToCategories());
                }
                case CLICKED_ADD_CATEGORY -> {

                    ComponentContainer.categoryMap.put(chatId, new Category(null, text, false));
                    ComponentContainer.stepMap.remove(chatId);
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Add category " + text + " ?",
                            InlineKeyboardUtil.confirmAddCategory());
                }
                case RENAME_CATEGORY -> {

                    CategoryService.renameCategory(ComponentContainer.categoryIdMap.get(chatId), text, chatId);
                    ComponentContainer.stepMap.put(chatId, AdminStatus.SHOW_PRODUCTS);
                }
            }

        }
    }
    public static void notificationToAdmin(String text, Message message) {

        long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        ComponentContainer.my_telegram_bot.sendMessage(conf.getProperty("adminId"),
                String.format("A message from @%s: \n%s.\n\nUserId: %s", userName, text, userId),
                (InlineKeyboardMarkup) null);
    }
}
