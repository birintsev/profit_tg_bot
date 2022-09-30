package org.profit.telegrambot;

import org.profit.telegrambot.controller.AdminController;
import org.profit.telegrambot.controller.MainController;
import org.profit.telegrambot.properties.PropertiesLoader;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Properties;

public class ShopBot extends TelegramLongPollingBot  {

    public AdminController adminController = new AdminController();
    public MainController mainController = new MainController();
    public static Properties conf;

    {
        try {
            conf = PropertiesLoader.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return conf.getProperty("botName");
    }

    @Override
    public String getBotToken() {
        return conf.getProperty("botToken");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()){
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            int messageId = message.getMessageId();

            if(chatId.equals(conf.getProperty("adminId"))){
                adminController.handleMessage(chatId, message);
            }else{
                mainController.handleMessage(chatId, message, messageId);
            }

        }else if(update.hasCallbackQuery()){
            Message message = update.getCallbackQuery().getMessage();
            int messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String data = update.getCallbackQuery().getData();

            if(chatId.equals(conf.getProperty("adminId"))){
                adminController.handleCallBack(message, data);
            }else{
                mainController.handleCallBack(chatId, data, messageId);
            }
        }
    }

    public void sendMessage(String chatId, String msgText, InlineKeyboardMarkup markup){
        SendMessage message = new SendMessage(chatId, msgText);
        message.setReplyMarkup(markup);

        try{
            execute(message);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String chatId, String msgText, ReplyKeyboardMarkup markup){
        SendMessage message = new SendMessage(chatId, msgText);
        message.setReplyMarkup(markup);

        try{
            execute(message);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendPhoto(String chatId, String caption, String photo, InlineKeyboardMarkup markup){
        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(photo));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(markup);

        try{
            execute(sendPhoto);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void deleteMessage(String chatId, int messageId){
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);

        try{
            execute(deleteMessage);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void editMessage(String chatId, String editText, int messageId, InlineKeyboardMarkup markup){
        EditMessageCaption edit = new EditMessageCaption();
        edit.setCaption(editText);
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setReplyMarkup(markup);

        try{
            execute(edit);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
