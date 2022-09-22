package org.profit.telegrambot.controller;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.profit.telegrambot.container.ComponentContainer;
import org.profit.telegrambot.enums.CustomerStatus;
import org.profit.telegrambot.model.Customer;
import org.profit.telegrambot.service.CustomerService;
import org.profit.telegrambot.util.InlineKeyboardUtil;
import org.profit.telegrambot.util.KeyboardUtil;

public class MainController {

    public void handleMessage(Message message) {
        if (message.hasText()) {
            handleText(message);
        } else if (message.hasContact()) {
            handleContact(message);
        }
    }

    private void handleContact(Message message) {
        Contact contact = message.getContact();
        String customerId = String.valueOf(contact.getUserId());

        Customer customer = CustomerService.getCustomerById(customerId);
        if (customer != null) {
            customer = new Customer(customerId, contact.getFirstName(),
                    contact.getLastName(), contact.getPhoneNumber(), CustomerStatus.SHARE_CONTACT);
            CustomerService.addCustomer(customer);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        deleteMessage.setMessageId(message.getMessageId());
        ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Select an action:");
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setReplyMarkup(InlineKeyboardUtil.Menu());
        ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
    }

    private void handleText(Message message) {
        String text = message.getText();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        Customer customer = CustomerService.getCustomerById(String.valueOf(message.getChatId()));

        if (text.equals("/start")) {

            if (customer == null) {
                sendMessage.setText("<b>Hey!</b>\n"
                        + "Send me your number.");
                sendMessage.setParseMode(ParseMode.HTML);
                sendMessage.setReplyMarkup(KeyboardUtil.contactMarkup());
                sendMessage.setChatId(String.valueOf(message.getChatId()));
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

            } else {
                sendMessage.setText("<b>💻AHello and welcome to JabkoBot ! \n\nSelect One from the menu</b>\n");
                sendMessage.setParseMode(ParseMode.HTML);

                sendMessage.setReplyMarkup(InlineKeyboardUtil.Menu());
                ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
            }
        }
    }


    public void handleCallBack(Message message, String data) {

        if (data.equals("web_network")) {

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(message.getChatId()));
            deleteMessage.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Our networks");
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setReplyMarkup(InlineKeyboardUtil.NetAddressMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("back")) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(message.getChatId()));
            deleteMessage.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Select an action: ");
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setReplyMarkup(InlineKeyboardUtil.Menu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);

        } else if (data.equals("menu_")) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(message.getChatId()));
            deleteMessage.setMessageId(message.getMessageId());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Choose one of the categories : 🔽");
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setReplyMarkup(InlineKeyboardUtil.categorysMenu());
            ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
        }
    }

}