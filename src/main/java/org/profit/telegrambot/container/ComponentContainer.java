package org.profit.telegrambot.container;

import org.profit.telegrambot.ShopBot;
import org.profit.telegrambot.enums.AdminStatus;
import org.profit.telegrambot.enums.CustomerStatus;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.model.Product;

import java.util.HashMap;
import java.util.Map;

public abstract class ComponentContainer {
    public static int generalId;

    public static ShopBot my_telegram_bot;

    public static Map<String, Product> productMap = new HashMap<>();

    public static Map<String, Category> categoryMap = new HashMap<>();

    public static Map<String, Integer> categoryIdMap = new HashMap<>();

    public static Map<String, Integer> productIdMap = new HashMap<>();

    public static Map<String, AdminStatus> stepMap = new HashMap<>();

    public static Map<String, Integer> quantityMap = new HashMap<>();

    public static Map<String, Product> crudStepMap = new HashMap<>();

    public static Map<String, CustomerStatus> customerStepMap = new HashMap<>();
}
