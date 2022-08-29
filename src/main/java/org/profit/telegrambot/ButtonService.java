package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ButtonService {

    public ReplyKeyboardMarkup setButtons(List<KeyboardRow> keyboardRowList) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    public List<KeyboardRow> createButtons(List<String> buttonsName) {
        List<KeyboardRow> keyboardRows = new ArrayList();
        KeyboardRow keyboardRow = new KeyboardRow();
        Iterator var4 = buttonsName.iterator();

        while(var4.hasNext()) {
            String buttonName = (String)var4.next();
            keyboardRow.add(new KeyboardButton(buttonName));
        }

        keyboardRows.add(keyboardRow);
        return keyboardRows;
    }

    /*
    Для добавления одной инлайн кнопки (рабочий код):
    public List<List<InlineKeyboardButton>> createInlineButton(String buttonName) {
        List<List<InlineKeyboardButton>> keyBoardList = new ArrayList<>();
        List<InlineKeyboardButton> keyBoardRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonName);
        inlineKeyboardButton.setCallbackData(buttonName);
        keyBoardRow.add(inlineKeyboardButton);
        keyBoardList.add(keyBoardRow);
        return keyBoardList;
    }

    public InlineKeyboardMarkup setInlineKeyBoard(List<List<InlineKeyboardButton>> keyBoardList){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }

     */
}




