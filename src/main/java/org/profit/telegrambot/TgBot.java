package org.profit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;


public class TgBot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String message = update.getMessage().getText();


        switch (message) {
            case "Main Page", "/start", "Shop's Info" -> firstKeyboard(chatId, message);
            case "Go Shopping", "Forward", "Back" -> keyboard(chatId, message);
        }
    }

    public synchronized void sendPhoto(String chatId, long pageCounter) {

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        if (pageCounter == 1) {
            photo.setPhoto(new InputFile("https://imgur.com/a/dIc6rlc"));
            photo.setCaption("Photo of product 1");
        } else if (pageCounter == 2) {
            photo.setPhoto(new InputFile("https://imgur.com/gallery/WaiBLIE"));
            photo.setCaption("Photo of product 2");
        }

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void firstKeyboard(String chatId, String msg) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> firstKeyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Go Shopping");
        row2.add("Shop's Info");
        firstKeyboard.add(row1);
        firstKeyboard.add(row2);

        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(firstKeyboard);
        keyboardMarkup.setOneTimeKeyboard(false);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(keyboardMarkup);

        switch (msg) {
            case "/start", "Main Page" -> message.setText("Welcome to our shop!");
            case "Shop's Info" -> message.setText("Info");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    long pageCounter = 1;

    public synchronized void keyboard(String chatId, String msg) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Back");
        row1.add(String.valueOf(pageCounter));
        row1.add("Forward");
        row2.add("Main Page");
        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);

        if (msg.equals("Forward")) {
            pageCounter++;
            row1.set(1, String.valueOf(pageCounter));
        } else if (msg.equals("Back") && pageCounter > 1) {
            pageCounter--;
            row1.set(1, String.valueOf(pageCounter));
        }

        sendPhoto(chatId, pageCounter);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Product description...");
        message.setAllowSendingWithoutReply(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        inlineKeyboard(chatId);
    }

    public synchronized void inlineKeyboard(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Add to cart?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> button1 = new ArrayList<>();
        InlineKeyboardButton but = new InlineKeyboardButton();
        but.setText("Add");
        but.setCallbackData("Add to cart");
        button1.add(but);
        keyboard.add(button1);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return "R231Bot";
    }

    @Override
    public String getBotToken() {
        return "5793024716:AAHBn08nBwGe0D864bLUFX4UtFhLt412ppk";
    }
}