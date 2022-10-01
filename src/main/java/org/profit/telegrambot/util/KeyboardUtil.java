package org.profit.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyboardUtil {

    private static KeyboardButton createButton(String buttonText){
    return new KeyboardButton(buttonText);
}
    private static KeyboardRow createRow(KeyboardButton ... buttons){
        return new KeyboardRow(Arrays.asList(buttons));
    }
    private static ReplyKeyboardMarkup createMarkup(List<KeyboardRow> keyboard){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }
    public static ReplyKeyboardMarkup contactMarkup(){
        KeyboardButton button = createButton("Send my number");
        button.setRequestContact(true);
        KeyboardRow row = createRow(button);
        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(row);

        return createMarkup(rowList);
    }
    public static ReplyKeyboardMarkup adminMenu() {
        KeyboardButton button = createButton("Categories and Products\nCRUD");
        KeyboardRow keyboardRow = createRow(button);

        return createMarkup(Collections.singletonList(keyboardRow));
    }
    public static ReplyKeyboardMarkup backTo(){
        KeyboardButton backToMenu = createButton("Main menu");
        KeyboardButton backToCategories = createButton("Categories");
        KeyboardButton toCart = createButton("🛒 Cart");

        KeyboardRow keyboardRow = createRow(backToMenu, backToCategories, toCart);

        return createMarkup(Collections.singletonList(keyboardRow));
    }
    public static ReplyKeyboardMarkup cartMarkup() {
        KeyboardButton order = createButton("Order");
        KeyboardButton toMenu = createButton("Back to menu");
        KeyboardRow keyboardRow = createRow(toMenu, order);

        return createMarkup(Collections.singletonList(keyboardRow));
    }
}
