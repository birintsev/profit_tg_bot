package org.profit.telegrambot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.service.CategoryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InlineKeyboardUtil {
    public static InlineKeyboardMarkup productMenu() {
        InlineKeyboardButton addButton = getButton("Add product", "add_product");
        InlineKeyboardButton updateButton = getButton("Update product", "update_product");
        InlineKeyboardButton deleteButton = getButton("Delete product", "delete_product");
        InlineKeyboardButton listButton = getButton("Show product list", "show_product_list");

        List<InlineKeyboardButton> row1 = getRow(addButton);
        List<InlineKeyboardButton> row2 = getRow(updateButton);
        List<InlineKeyboardButton> row3 = getRow(deleteButton);
        List<InlineKeyboardButton> row4 = getRow(listButton);

        List<List<InlineKeyboardButton>> rowList = getRowList(row1, row2, row3, row4);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup categorysMenu() {
        CategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();

        for (Category category : Database.categoryList) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getName());
            button.setCallbackData("category/"+category.getId());
            List<InlineKeyboardButton> row = getRow(button);

            rowList.add(row);
        }

        return inlineMarkup(rowList);
    }

    public static InlineKeyboardMarkup categoryMenu() {
        InlineKeyboardButton addButtonC = getButton("Add category", "add_category");
        InlineKeyboardButton updateButtonC = getButton("Update category", "update_category");
        InlineKeyboardButton deleteButtonC = getButton("Delete category", "delete_category");
        InlineKeyboardButton listButtonC = getButton("Show category list", "show_category_list");

        List<InlineKeyboardButton> row5 = getRow(addButtonC);
        List<InlineKeyboardButton> row6 = getRow(updateButtonC);
        List<InlineKeyboardButton> row7 = getRow(deleteButtonC);
        List<InlineKeyboardButton> row8 = getRow(listButtonC);
        List<List<InlineKeyboardButton>> rowList = getRowList(row5, row6, row7, row8);
        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButton(String demo, String data) {
        InlineKeyboardButton button = new InlineKeyboardButton(demo);
        button.setCallbackData(data);
        return button;
    }

    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton... buttons) {
        return Arrays.asList(buttons);
    }

    private static List<List<InlineKeyboardButton>> getRowList(List<InlineKeyboardButton>... rows) {
        return Arrays.asList(rows);
    }

    public static InlineKeyboardMarkup categoryInlineMarkup() {

        CategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Category category : Database.categoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton(category.getName());
            button.setCallbackData("add_product_category_id/" + category.getId());
            buttonList.add(button);
            rowList.add(buttonList);
        }
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup categoryShow() {
        CategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> listList = new ArrayList<>();
        for (Category category : Database.categoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton(category.getName());
            button.setCallbackData("show_category/" + category.getId());
            buttonList.add(button);
            listList.add(buttonList);
        }

        InlineKeyboardButton button = getButton("back", "back_from_category_list");
        List<InlineKeyboardButton> row = getRow(button);
        listList.add(row);

        return new InlineKeyboardMarkup(listList);
    }


    public static InlineKeyboardMarkup Menu() {

        InlineKeyboardButton menuButton = getButton("📠MENU", "menu_");
        InlineKeyboardButton magazineButton = getButton("🏪Our address", "shop_url");
        InlineKeyboardButton contactButton = getButton("📞Our social networks", "web_network");
        InlineKeyboardButton helpButton = getButton("💬HELP", "help");

        magazineButton.setUrl("https://www.google.com/maps/place/%D0%A6%D0%B5%D0%BD%D1%82%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9+%D1%83%D0%BD%D0%B8%D0%B2%D0%B5%D1%80%D0%BC%D0%B0%D0%B3/@50.9067489,34.7966222,15z/data=!4m5!3m4!1s0x0:0xdc737b63790a2531!8m2!3d50.9067489!4d34.7966222");
        helpButton.setUrl("https://t.me/appleboom_sumy");

        List<InlineKeyboardButton> row1 = getRow(menuButton);
        List<InlineKeyboardButton> row2 = getRow(magazineButton);
        List<InlineKeyboardButton> row3 = getRow(helpButton);
        List<InlineKeyboardButton> row4 = getRow(contactButton);


        List<List<InlineKeyboardButton>> rowList = getRowList(row1, row2, row3, row4);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup NetAddressMenu() {
        InlineKeyboardButton instagram = getButton("📤 Instgram", "instagram");
        InlineKeyboardButton telegram = getButton("📤 Telegram", "telegram");
        InlineKeyboardButton facebook = getButton("📤 Facebook", "facebook");
        InlineKeyboardButton back = getButton("◀ BACK", "back");

        instagram.setUrl("https://www.instagram.com/@iphone_sumy");
        telegram.setUrl("https://t.me/appleboom_sumy");
        facebook.setUrl("https://www.facebook.com/appleb0om/");


        List<InlineKeyboardButton> row1 = getRow(instagram);
        List<InlineKeyboardButton> row2 = getRow(telegram, facebook);
        List<InlineKeyboardButton> row3 = getRow(back);

        List<List<InlineKeyboardButton>> rowList = getRowList(row1, row2, row3);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup confirmAddProductMarkup() {

        InlineKeyboardButton commit = getButton("Yes", "add_product_commit");
        InlineKeyboardButton cancel = getButton("No", "add_product_cancel");

        return new InlineKeyboardMarkup(getRowList(getRow(commit, cancel)));
    }

    public static InlineKeyboardMarkup inlineMarkup(List<List<InlineKeyboardButton>> keyboard) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup confirmAddCategoryMarkup() {
        InlineKeyboardButton commit = getButton("Yes", "add_category_commit");
        InlineKeyboardButton cancel = getButton("No", "add_category_cancel");

        return new InlineKeyboardMarkup(getRowList(getRow(commit, cancel)));
    }

    public static InlineKeyboardMarkup updateProduct() {
        InlineKeyboardButton change_name = getButton("🖊 Rename 🖊", "change_name");
        InlineKeyboardButton change_price = getButton("💸 Change Price 💸", "change_price");
        InlineKeyboardButton change_deleted = getButton("📑 Enable disabled  📑", "change_deleted_on");
        InlineKeyboardButton change_deleted_switch = getButton("📑 Turn off enabled ones  📑", "change_deleted_off");
        InlineKeyboardButton change_deleted_show = getButton("📜 View Deleted Items  📜", "change_deleted_show");
        InlineKeyboardButton back = getButton("📜 Back 📜", "back_crud");


        List<InlineKeyboardButton> row1 = getRow(change_name,change_price);
        List<InlineKeyboardButton> row2 = getRow(change_deleted_show);
        List<InlineKeyboardButton> row3 = getRow(change_deleted,change_deleted_switch);
        List<InlineKeyboardButton> row4 = getRow(back);

        List<List<InlineKeyboardButton>> rowList = getRowList(row1, row2,row3,row4);

        return new InlineKeyboardMarkup(rowList);
    }

}
