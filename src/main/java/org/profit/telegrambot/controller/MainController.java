package org.profit.telegrambot.controller;

import org.profit.telegrambot.container.ComponentContainer;
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

        if (Objects.equals(ComponentContainer.customerStepMap.get(chatId), CustomerStatus.SPECIFY_PROBLEM)){
            AdminController.notificationToAdmin(text, message);
            ComponentContainer.customerStepMap.remove(chatId);
        } else if (Objects.equals(ComponentContainer.customerStepMap.get(chatId), CustomerStatus.ENTERED_QUANTITY)){
            ComponentContainer.quantityMap.put(chatId, Integer.valueOf(text));
            ComponentContainer.my_telegram_bot.editMessage(chatId, "Choose the quantity:", ComponentContainer.quantityMap.get(chatId + 2), InlineKeyboardUtil.productQuantity(chatId));
            ComponentContainer.customerStepMap.remove(chatId);
        }
        switch (text) {

            case "/start":
                if (customer == null) {
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Welcome!\n" + "To start, please send me your number:",
                            KeyboardUtil.contactMarkup());
                } else {
                    ComponentContainer.my_telegram_bot.sendMessage(chatId,
                            "Welcome to our shop again!\n\nChoose an option from menu:\n",
                            InlineKeyboardUtil.menu());
                }
                break;
            case "Categories":
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.categoryShow());
                break;
            case "Back to menu", "Main menu":
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Choose an option:", InlineKeyboardUtil.menu());
                break;
            case  "🛒 Cart":
                if (ComponentContainer.customerStepMap.containsValue(CustomerStatus.ACCEPTED_QUANTITY)){
                    ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                    CartProductService.showCart(chatId, customer.getId());
                    ComponentContainer.my_telegram_bot.sendMessage(chatId, "Continue to order?", KeyboardUtil.cartMarkup());
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

        ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
        ComponentContainer.my_telegram_bot.sendMessage(chatId, "Choose an option:", InlineKeyboardUtil.menu());
    }


    public void handleCallBack(String chatId, String data, int messageId) {


        switch (data) {

            case "web_networks" -> {
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Our social networks:", InlineKeyboardUtil.createNetAddressMenu());
            }
            case "to_categories" -> {
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.categoryShow());
            }
            case "help" -> {
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Please, specify your problem:", (InlineKeyboardMarkup) null);
                ComponentContainer.customerStepMap.put(chatId, CustomerStatus.SPECIFY_PROBLEM);
            }
            case "to_main_menu" -> {
                ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
                ComponentContainer.my_telegram_bot.sendMessage(chatId, "Choose a category:", InlineKeyboardUtil.menu());
            }
        }

        if (data.startsWith("category")){
            ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
            ProductService.showProductList(chatId, Integer.parseInt(data.split("/")[1]));
            ComponentContainer.my_telegram_bot.sendMessage(chatId,
                    Objects.requireNonNull(CategoryService.
                            getCategoryById(Integer.parseInt(data.split("/")[1]))).getName(),
                            KeyboardUtil.backTo());
        } else if (data.startsWith("add_to_cart")) {
            int productId = Integer.parseInt(data.split("/")[1]);
            ComponentContainer.quantityMap.put(chatId, 1);
            ComponentContainer.customerStepMap.put(chatId, CustomerStatus.ADDING_TO_CART);
            ComponentContainer.productIdMap.put(chatId, productId);
            ComponentContainer.my_telegram_bot.editMessage(chatId,
                    "Choose the quantity:",
                    messageId,
                    InlineKeyboardUtil.productQuantity(chatId));
        } else if (data.startsWith("delete_from_cart")){
            CartProductService.deleteFromCart(Integer.parseInt(data.split("/")[1]), messageId,chatId);
        } else if (Objects.equals(ComponentContainer.customerStepMap.get(chatId), CustomerStatus.ADDING_TO_CART)) {

            switch (data) {
                case "forward" -> {
                    ComponentContainer.quantityMap.replace(chatId, ComponentContainer.quantityMap.get(chatId) + 1);
                    ComponentContainer.my_telegram_bot.editMessage(chatId,
                            "Choose the quantity:",
                            messageId,
                            InlineKeyboardUtil.productQuantity(chatId));
                }
                case "back" -> {
                    if (ComponentContainer.quantityMap.get(chatId) > 1){
                        ComponentContainer.quantityMap.replace(chatId, ComponentContainer.quantityMap.get(chatId) - 1);
                        ComponentContainer.my_telegram_bot.editMessage(chatId,
                                "Choose the quantity:",
                                messageId,
                                InlineKeyboardUtil.productQuantity(chatId));
                    }
                }
                case "accept_quantity" -> {
                    ComponentContainer.my_telegram_bot.editMessage(chatId,
                            ComponentContainer.quantityMap.get(chatId) + " Product is added to cart",
                            messageId,
                            InlineKeyboardUtil.addToCart(ComponentContainer.productIdMap.get(chatId)));

                    String userId = CustomerService.getCustomerById(chatId).getId();
                    ComponentContainer.customerStepMap.put(chatId + 1, CustomerStatus.ACCEPTED_QUANTITY);
                    CartProductService.addToCart(userId, ComponentContainer.productIdMap.get(chatId), ComponentContainer.quantityMap.get(chatId));
                }
                case "decline_quantity" -> {
                    ComponentContainer.my_telegram_bot.editMessage(chatId,
                            "description of product",
                            messageId,
                            InlineKeyboardUtil.addToCart(ComponentContainer.productIdMap.get(chatId)));
                }
                case "quantity" -> {
                    ComponentContainer.my_telegram_bot.editMessage(chatId,
                            "Send me quantity of products you want to add:",
                            messageId,
                            InlineKeyboardUtil.productQuantity(chatId));
                    ComponentContainer.customerStepMap.put(chatId, CustomerStatus.ENTERED_QUANTITY);
                    ComponentContainer.quantityMap.put(chatId + 2, messageId);
                }
            }
        }
    }
}
