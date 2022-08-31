package org.profit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.profit.telegrambot.AntiHardcode.*;

public class TgBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && !update.getMessage().hasPhoto()){
            String chatId = update.getMessage().getChatId().toString();
            String message = update.getMessage().getText();

            switch (message) {

                case "Main Page", "/start", "Shop's Info" -> firstKeyboard(chatId, message);
                case "Go Shopping", "Forward", "Back" -> keyboard(chatId, message);
            }

        } else if (update.hasCallbackQuery()) {

            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String callData = update.getCallbackQuery().getData();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callData.equals("Added")){

                String answer = "Added " + addCounter;
                EditMessageText newMessage = new EditMessageText();
                newMessage.setChatId(chatId);
                newMessage.setMessageId(messageId);
                newMessage.setText(answer);
                addCounter ++;

                try {
                    execute(newMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.getMessage().hasPhoto()){

            List<PhotoSize> photos = update.getMessage().getPhoto();
            String fileId = Objects.requireNonNull(photos.stream()
                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                    .findFirst()
                    .orElse(null)).getFileId();

            System.out.println(fileId);
        }


    }

    public synchronized void sendPhoto(String chatId, int pageCounter) {

        AntiHardcode antiHardcode = new AntiHardcode();
        antiHardcode.chosePath(pageCounter);

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(imagePath));

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
            case "/start", "Main Page" -> message.setText(greetingMessage);
            case "Shop's Info" -> message.setText(botDescription);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }



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
        List<InlineKeyboardButton> inlineRow1 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Add");
        button.setCallbackData("Added");
        inlineRow1.add(button);
        keyboard.add(inlineRow1);

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

        return name;
    }

    @Override
    public String getBotToken() {

        return token;
    }
}