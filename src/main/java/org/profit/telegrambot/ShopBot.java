package org.profit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.profit.telegrambot.container.ComponentContainer;
import org.profit.telegrambot.controller.AdminController;
import org.profit.telegrambot.controller.MainController;

public class ShopBot extends TelegramLongPollingBot {

    public AdminController adminController = new AdminController();
    public MainController mainController = new MainController();

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();

            if(String.valueOf(user.getId()).equals(ComponentContainer.ADMIN_ID)) {
                adminController.handleMessage(message);
            } else {
                mainController.handleMessage(message);
            }

        } else if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();

            if(String.valueOf(user.getId()).equals(ComponentContainer.ADMIN_ID)) {
                adminController.handleCallBack(message, data);
            } else {
                mainController.handleCallBack(message, data);
            }
        }
    }

    public void sendMsg (Object message) {

        try {
            if (message instanceof SendMessage) {
                execute((SendMessage) message);
            }
            if (message instanceof SendDocument) {
                execute((SendDocument) message);
            }
            if (message instanceof DeleteMessage) {
                execute((DeleteMessage) message);
            }
            if (message instanceof EditMessageText) {
                execute((EditMessageText) message);
            }
            if (message instanceof SendSticker) {
                execute((SendSticker) message);
            }
            if (message instanceof SendPhoto) {
                execute((SendPhoto) message);
            }
            if (message instanceof SendDice) {
                execute((SendDice) message);
            }
            if (message instanceof SendVenue) {
                execute((SendVenue) message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }
}
