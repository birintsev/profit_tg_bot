package org.profit.telegrambot.controller;

import org.profit.telegrambot.enums.CustomerStatus;
import org.profit.telegrambot.model.Customer;
import org.profit.telegrambot.service.CartProductService;
import org.profit.telegrambot.service.CategoryService;
import org.profit.telegrambot.service.CustomerService;
import org.profit.telegrambot.service.ProductService;
import org.profit.telegrambot.util.InlineKeyboardUtil;
import org.profit.telegrambot.util.KeyboardUtil;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;

import static org.profit.telegrambot.container.ComponentContainer.*;

public class MainController {

    public void handleMessage(String chatId, Message message, int messageId) {
        if (message.hasText()) {
            handleText(chatId, message, messageId);
        } else if (message.hasContact()) {
            handleContact(chatId, message, messageId);
        } else{
            System.out.println("Method 'handleMessage' was not executed");
        }
    }

    private void handleText(String chatId, Message message, int messageId) {
        String text = message.getText();
        Customer customer = CustomerService.getCustomerById(String.valueOf(message.getFrom().getId()));

        if (Objects.equals(customerStepMap.get(chatId), CustomerStatus.SPECIFY_PROBLEM)){
            AdminController.notificationToAdmin(text, message);
            customerStepMap.remove(chatId);
        } else if (Objects.equals(customerStepMap.get(chatId), CustomerStatus.ENTERED_QUANTITY)){
            quantityMap.put(chatId, Integer.valueOf(text));
            my_telegram_bot.editMessage(chatId, "Choose the quantity:", quantityMap.get(chatId + 2), InlineKeyboardUtil.productQuantity(chatId));
            customerStepMap.remove(chatId);
        }
        switch (text) {

            case "/start":
                if (customer == null) {
                    my_telegram_bot.sendMessage(chatId,
                            "Welcome!\n" + "To start, please send me your number:",
                            KeyboardUtil.contactMarkup());
                } else {
                    my_telegram_bot.sendMessage(chatId,
                            "Welcome to our shop again!\n\nChoose an option from menu:\n",
                            InlineKeyboardUtil.menu());
                }
                break;
            case "Categories":
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.categoryShow());
                break;
            case "Back to menu", "Main menu":
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Choose an option:", InlineKeyboardUtil.menu());
                break;
            case  "🛒 Cart":
                if (customerStepMap.containsValue(CustomerStatus.ACCEPTED_QUANTITY)){
                    my_telegram_bot.deleteMessage(chatId, messageId);
                    CartProductService.showCart(chatId, customer.getId());
                    my_telegram_bot.sendMessage(chatId, "Continue to order?", KeyboardUtil.cartMarkup());
                }
                break;
            case "Order":
                System.out.println("Ordering...");
                break;
        }
    }

    private void handleContact(String chatId, Message message, int messageId) {
        Contact contact = message.getContact();

        Customer customer = new Customer(String.valueOf(message.getFrom().getId()),
                                contact.getFirstName(),
                                contact.getLastName(),
                                contact.getPhoneNumber(),
                                CustomerStatus.SHARE_CONTACT);
        CustomerService.addCustomer(customer);

        my_telegram_bot.deleteMessage(chatId, messageId);
        my_telegram_bot.sendMessage(chatId, "Choose an option:", InlineKeyboardUtil.menu());
    }


    public void handleCallBack(String chatId, String data, int messageId) {


        switch (data) {

            case "web_networks" -> {
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Our social networks:", InlineKeyboardUtil.createNetAddressMenu());
            }
            case "to_categories" -> {
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.categoryShow());
            }
            case "help" -> {
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Please, specify your problem:", (InlineKeyboardMarkup) null);
                customerStepMap.put(chatId, CustomerStatus.SPECIFY_PROBLEM);
            }
            case "to_main_menu" -> {
                my_telegram_bot.deleteMessage(chatId, messageId);
                my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.menu());
            }
        }

        if (data.startsWith("category")){
            my_telegram_bot.deleteMessage(chatId, messageId);
            ProductService.showProductList(chatId, Integer.parseInt(data.split("/")[1]));
            my_telegram_bot.sendMessage(chatId,
                    Objects.requireNonNull(CategoryService.
                            getCategoryById(Integer.parseInt(data.split("/")[1]))).getName(),
                            KeyboardUtil.backTo());
        } else if (data.startsWith("add_to_cart")) {
            int productId = Integer.parseInt(data.split("/")[1]);
            quantityMap.put(chatId, 1);
            customerStepMap.put(chatId, CustomerStatus.ADDING_TO_CART);
            productIdMap.put(chatId, productId);
            my_telegram_bot.editMessage(chatId,
                    "Choose the quantity:",
                    messageId,
                    InlineKeyboardUtil.productQuantity(chatId));
        } else if (data.startsWith("delete_from_cart")){
            CartProductService.deleteFromCart(Integer.parseInt(data.split("/")[1]), messageId,chatId);
        } else if (Objects.equals(customerStepMap.get(chatId), CustomerStatus.ADDING_TO_CART)) {

            switch (data) {
                case "forward" -> {
                    quantityMap.replace(chatId, quantityMap.get(chatId) + 1);
                    my_telegram_bot.editMessage(chatId,
                            "Choose the quantity:",
                            messageId,
                            InlineKeyboardUtil.productQuantity(chatId));
                }
                case "back" -> {
                    if (quantityMap.get(chatId) > 1){
                        quantityMap.replace(chatId, quantityMap.get(chatId) - 1);
                        my_telegram_bot.editMessage(chatId,
                                "Choose the quantity:",
                                messageId,
                                InlineKeyboardUtil.productQuantity(chatId));
                    }
                }
                case "accept_quantity" -> {
                    my_telegram_bot.editMessage(chatId,
                            quantityMap.get(chatId) + " Product is added to cart",
                            messageId,
                            InlineKeyboardUtil.addToCart(productIdMap.get(chatId)));

                    String userId = CustomerService.getCustomerById(chatId).getId();
                    customerStepMap.put(chatId + 1, CustomerStatus.ACCEPTED_QUANTITY);
                    CartProductService.addToCart(userId, productIdMap.get(chatId), quantityMap.get(chatId));
                }
                case "decline_quantity" -> {
                    my_telegram_bot.editMessage(chatId,
                            "description of product",
                            messageId,
                            InlineKeyboardUtil.addToCart(productIdMap.get(chatId)));
                }
                case "quantity" -> {
                    my_telegram_bot.editMessage(chatId,
                            "Send me quantity of products you want to add:",
                            messageId,
                            InlineKeyboardUtil.productQuantity(chatId));
                    customerStepMap.put(chatId, CustomerStatus.ENTERED_QUANTITY);
                    quantityMap.put(chatId + 2, messageId);
                }
            }
        }
    }
}
