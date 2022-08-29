package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;


public class MaksymenBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return "5533026532:AAHGftCTJqPz0KKdegvqwUWUTUg69eDpfjU";
    }

    @Override
    public String getBotUsername() {
        return "@MaksymZymynTestBot";
    }

    private final ButtonService buttonService = new ButtonService();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText() != null) {
                Message newMessage = update.getMessage();
                String textMsg = newMessage.getText();
                String chatId = String.valueOf(newMessage.getChatId());

                if (textMsg.equals("/start")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Добридень, ми з України!\n" +
                            "Введіть, будь ласка, своє ім'я та прізвище:");
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(chatId);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                } else if (textMsg.equals("Хліби")) {
                    /*
                    Для добавления одной кнопки:
                    String textNewMsg = "Оберіть, який саме хліб Ви хочете замовити:";
                    keyboard(chatId, textNewMsg, "Бородінський");
                     */
                    String textBread = "Оберіть, який саме хліб Ви хочете замовити:";
                    sendInlineKeyboard(textBread, chatId, "Бородінський", "Василівський");
                    // "Литовський", "Ризький", "Заріченський"
                } else if (textMsg.equals("Торти")) {
                    String textCake = "Оберіть, який саме торт Ви хочете замовити:";
                    sendInlineKeyboard(textCake, chatId,  "Київський", "Мадонна");
                    // "Сакура", "Йогуртовий", "Деметра"
                } else if (textMsg.equals("Тістечка")) {
                    String textCupcake = "Оберіть, яке саме тістечко Ви хочете замовити:";
                    sendInlineKeyboard(textCupcake, chatId,  "Еклер", "Лимонне");
                    // "Спартак",  "Корзинка", "Медове"
                } else if (textMsg.equals("Круасани")) {
                    String textPie = "Оберіть, який саме круасан Ви хочете замовити:";
                    sendInlineKeyboard(textPie, chatId,  "З вишнею", "Зі згущеним молоком");
                    // "З малиною", "З шоколадом", "Зі смородиною"
                } else {
                        String textNewMsg = textMsg + ", ми пропонуємо:";
                        sendReplayKeyboard(textNewMsg, chatId);
                    }
                }
            }
        }



        private void sendReplayKeyboard (String textNewMsg, String chatId){
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setText(textNewMsg);
            sendMessage.setChatId(chatId);
            ReplyKeyboardMarkup keyboardMarkup =
                    buttonService.setButtons(buttonService.createButtons(asList(
                            "Хліби", "Торти", "Тістечка", "Круасани"
                    )));
            sendMessage.setReplyMarkup(keyboardMarkup);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void sendInlineKeyboard (String product, String chatId, String firstName, String secondName) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(product);
            sendMessage.setChatId(chatId);
            List<InlineKeyboardButton> firstRow = new LinkedList<>();
            InlineKeyboardButton inlineKeyboardButtonFirst = new InlineKeyboardButton();
            inlineKeyboardButtonFirst.setText(firstName);
            inlineKeyboardButtonFirst.setCallbackData("first");
            InlineKeyboardButton inlineKeyboardButtonSecond = new InlineKeyboardButton();
            inlineKeyboardButtonSecond.setText(secondName);
            inlineKeyboardButtonSecond.setCallbackData("second");
                    /*
                    List<InlineKeyboardButton> secondRow = new LinkedList<>();
                    InlineKeyboardButton inlineKeyboardButtonThird = new InlineKeyboardButton();
                    inlineKeyboardButtonSecond.setText(thirdName);
                    inlineKeyboardButtonSecond.setCallbackData("third");
                    InlineKeyboardButton inlineKeyboardButtonFourth = new InlineKeyboardButton();
                    inlineKeyboardButtonSecond.setText(fourthName);
                    inlineKeyboardButtonSecond.setCallbackData("fourth");
                    List<InlineKeyboardButton> thirdRow = new LinkedList<>();
                    InlineKeyboardButton inlineKeyboardButtonFifth = new InlineKeyboardButton();
                    inlineKeyboardButtonSecond.setText(fifthName);
                    inlineKeyboardButtonSecond.setCallbackData("fifth");
                     */
            firstRow.add(inlineKeyboardButtonFirst);
            firstRow.add(inlineKeyboardButtonSecond);
                    /*
                    secondRow.add(inlineKeyboardButtonThird);
                    secondRow.add(inlineKeyboardButtonFourth);
                    thirdRow.add(inlineKeyboardButtonFifth);
                     */
            List<List<InlineKeyboardButton>> rowCollection = new LinkedList<>();
            rowCollection.add(firstRow);
                    /*
                    rowCollection.add(secondRow);
                    rowCollection.add(thirdRow);
                     */
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowCollection);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    /*
    Для добавления одной инлайн кнопки (рабочий код):
    public synchronized void keyboard(String chatId, String product, String firstName) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(product);
        response.enableMarkdown(true);
        InlineKeyboardMarkup inlineKeyboardMarkup1 =
                buttonService.setInlineKeyBoard(buttonService.createInlineButton(firstName));
        response.setReplyMarkup(inlineKeyboardMarkup1);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
     */
}






