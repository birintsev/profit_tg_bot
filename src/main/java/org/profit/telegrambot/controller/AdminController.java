package org.profit.telegrambot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.profit.telegrambot.container.ComponentContainer;
import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.enums.AdminStatus;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.model.Product;
import org.profit.telegrambot.service.CategoryService;
import org.profit.telegrambot.service.ProductService;
import org.profit.telegrambot.util.InlineKeyboardUtil;
import org.profit.telegrambot.util.KeyboardUtil;

import java.util.List;
import java.util.Objects;

import static org.profit.telegrambot.database.Database.productList;

public class AdminController {
    public void handleMessage(Message message) {
        if (message.hasText()) {
            handleText(message);
        } else if (message.hasPhoto()) {
            handlePhoto(message);
        }
    }

    private void handlePhoto(Message message) {
        List<PhotoSize> photoSizeList = message.getPhoto();

        String chatId = String.valueOf(message.getChatId());

        if (ComponentContainer.productStepMap.containsKey(chatId)) {
            Product product = ComponentContainer.productMap.get(chatId);

            if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_PRICE)) {
                product.setImage(photoSizeList.get(photoSizeList.size() - 1).getFileId());

                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("""
                                Category: %s
                                Product: %s\s
                                 Cost: %s $

                                 Add the following product to the list?""",
                        Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(),
                        product.getName(), product.getPrice()));
                sendPhoto.setReplyMarkup(InlineKeyboardUtil.confirmAddProductMarkup());

                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
        }
    }

    private static Integer id = null;

    private static String newName = null;

    private void handleText(Message message) {
        String text = message.getText();
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        if (text.equals("/start")) {
            sendMessage.setText("Select an action:");
            sendMessage.setReplyMarkup(KeyboardUtil.adminMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (text.equals("Category CRUD")) {
            sendMessage.setText("Select an action:");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (text.equals("Product CRUD")) {
            sendMessage.setText("Select an action:");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (ComponentContainer.productStepMap.containsKey(chatId)) {

            Product product = ComponentContainer.productMap.get(chatId);

            if (ComponentContainer.productStepMap.get(chatId)
                    .equals(AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT)) {
                product.setName(text);
                ComponentContainer.productStepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_NAME);

                sendMessage.setText("Enter the price of the product (a real positive number): ");
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            } else if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.ENTERED_PRODUCT_NAME)) {
                double price = 0;
                try {
                    price = Double.parseDouble(text.trim());
                } catch (NumberFormatException ignored) {
                }

                if (price <= 0) {
                    sendMessage.setText("The price was entered incorrectly, please re-enter the price: ");
                } else {
                    product.setPrice(price);
                    ComponentContainer.productStepMap.put(chatId, AdminStatus.ENTERED_PRODUCT_PRICE);

                    sendMessage.setText("Send a picture of the product: ");
                    ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
                }

            } else if (ComponentContainer.productStepMap.get(chatId)
                    .equals(AdminStatus.DELETE_PRODUCT)) {
                ProductService.deleteProduct(Integer.valueOf(text));
                sendMessage.setText("The product has been removed");
                ComponentContainer.productMap.remove(chatId);
                ComponentContainer.productStepMap.remove(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }

        } else if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.CHANGE_NAME)) {
            id = Integer.valueOf(text);

            ComponentContainer.crudStepMap.put(chatId, new Product(id));
            ComponentContainer.productStepMap.put(chatId, AdminStatus.CHANGE_NAME_BY_ID);
            sendMessage.setText("Enter a New Product Name : ");

        } else if (ComponentContainer.productStepMap.get(chatId).equals(AdminStatus.CHANGE_NAME_BY_ID)) {
            newName = text;
            Product product1 = ComponentContainer.crudStepMap.get(chatId);
            ProductService.updateProductName(product1.getId(), newName);

            ComponentContainer.crudStepMap.remove(chatId);
            sendMessage.setText("The product name has changed ");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.updateProduct());

        } else if (ComponentContainer.categoryStepMap.get(chatId).equals(AdminStatus.DELETE_CATEGORY)) {
            CategoryService.deleteCategory(Integer.parseInt(text));
            sendMessage.setText("Category has been deleted");
            ComponentContainer.categoryMap.remove(chatId);
            ComponentContainer.categoryStepMap.remove(chatId);
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (ComponentContainer.categoryStepMap.get(chatId)
                .equals(AdminStatus.CLICKED_ADD_CATEGORY)) {
            Category category = ComponentContainer.categoryMap.get(chatId);
            category.setName(text);
            ComponentContainer.categoryStepMap.put(chatId, AdminStatus.ENTERED_CATEGORY_NAME);

            sendMessage.setText("Category: " + text);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.confirmAddCategoryMarkup());

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
    }

    public void handleCallBack(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        if (data.equals("add_category")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage(chatId, "Enter a category name: ");

            ComponentContainer.categoryStepMap.remove(chatId);
            ComponentContainer.categoryMap.remove(chatId);

            ComponentContainer.categoryStepMap.put(chatId, AdminStatus.CLICKED_ADD_CATEGORY);
            ComponentContainer.categoryMap.put(chatId,
                    new Category(null, null, false));

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("update_category")) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

        } else if (data.equals("delete_category")) {

            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            CategoryService.loadCategoryList();

            for (Category category : Database.categoryList) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(String.format(" Category: %s\n ID: %s\n ",
                        category.getName(), category.getId()));
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
            SendMessage sendMessage = new SendMessage(
                    chatId, "Enter the id of the category you want to delete: ");
            ComponentContainer.categoryStepMap.put(chatId, AdminStatus.DELETE_CATEGORY);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("show_category_list")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("All categories");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryShow());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("add_product")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Choose one of the categories:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryInlineMarkup());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            ComponentContainer.productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            ComponentContainer.productStepMap.put(chatId, AdminStatus.CLICKED_ADD_PRODUCT);
            ComponentContainer.productMap.put(chatId,
                    new Product(null, null, null, null));

        } else if (data.startsWith("add_product_category_id")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            int categoryId = Integer.parseInt(data.split("/")[1]);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Enter the product name: "
            );

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            ComponentContainer.productStepMap.put(chatId, AdminStatus.SELECT_CATEGORY_FOR_ADD_PRODUCT);
            Product product = ComponentContainer.productMap.get(chatId);
            product.setCategoryId(categoryId);
        } else if (data.equals("delete_product")) {

            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            ProductService.loadProductList();
            for (Product product : Database.productList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("""
                                🏆 Category: %s
                                🏆 ID: %s
                                💻 Product: %s
                                💸 Cost: %s""".indent(1),
                        Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(), product.getId(),
                        product.getName(), product.getPrice()));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(
                    chatId, "Enter the id of the product you want to delete: "
            );
            ComponentContainer.productStepMap.put(chatId, AdminStatus.DELETE_PRODUCT);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        } else if (data.equals("add_product_commit")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            Product product = ComponentContainer.productMap.get(chatId);

            ProductService.addProduct(product);

            ComponentContainer.productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, product.getName() + "\t saved.\n\n" + "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("add_product_cancel")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            ComponentContainer.productMap.remove(chatId);
            ComponentContainer.productStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("update_product")) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Select an option from the menu: ");
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setReplyMarkup(InlineKeyboardUtil.updateProduct());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("show_product_list")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            ProductService.loadProductList();

            for (Product product : productList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format("""
                                Category: %s
                                Product: %s\s
                                 Cost: %s $
                                """,
                        Objects.requireNonNull(CategoryService.getCategoryById(product.getCategoryId())).getName(),
                        product.getName(), product.getPrice()));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }

            SendMessage sendMessage = new SendMessage(
                    chatId, "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.productMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("change_name")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            ProductService.loadProductList();

            for (Product product : Database.productList) {
                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getImage()));
                sendPhoto.setCaption(String.format(" 🏆 Category: %s\n 🏆 ID: %s\n "
                                + "💻 Product: %s\n 💸 Cost: %s",
                        CategoryService.getCategoryById(product.getCategoryId()).getName(),
                            product.getId(), product.getName(), product.getPrice()));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendPhoto);
            }
            SendMessage sendMessage = new SendMessage(chatId, "Enter the ID of the product you want to change: ");

            ComponentContainer.productStepMap.put(chatId, AdminStatus.CHANGE_NAME);

            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("add_category_commit")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            Category category = ComponentContainer.categoryMap.get(chatId);

            CategoryService.addCategory(category);

            ComponentContainer.categoryMap.remove(chatId);
            ComponentContainer.categoryStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, category.getName() + "\t saved.\n\n" + "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("add_category_cancel")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            ComponentContainer.categoryMap.remove(chatId);
            ComponentContainer.categoryStepMap.remove(chatId);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        } else if (data.equals("back_from_category_list")) {
            DeleteMessage deleteMessage = new DeleteMessage(
                    chatId, message.getMessageId()
            );
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage(
                    chatId, "Select an action:"
            );
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categoryMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
    }

}
