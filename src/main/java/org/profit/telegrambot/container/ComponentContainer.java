package org.profit.telegrambot.container;

import org.profit.telegrambot.ShopBot;
import org.profit.telegrambot.enums.AdminStatus;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.model.Product;

import java.util.HashMap;
import java.util.Map;

public abstract class ComponentContainer {

    public static final String ADMIN_ID = "";

    public static ShopBot MY_TELEGRAM_BOT;

    public static Map<String, Product> productMap = new HashMap<>();

    public static Map<String, Product> crudStepMap = new HashMap<>();

    public static Map<String, Category> categoryMap = new HashMap<>();

    public static Map<String, AdminStatus> productStepMap = new HashMap<>();

    public static Map<String, AdminStatus> categoryStepMap = new HashMap<>();

}
