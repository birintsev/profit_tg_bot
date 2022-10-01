package org.profit.telegrambot;

import org.profit.telegrambot.container.ComponentContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) {
        try {

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            ShopBot shopBot = new ShopBot();
            ComponentContainer.my_telegram_bot = shopBot;

            telegramBotsApi.registerBot(shopBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
