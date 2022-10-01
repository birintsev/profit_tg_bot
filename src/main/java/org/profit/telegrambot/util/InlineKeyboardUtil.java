package org.profit.telegrambot.util;

import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.model.Category;
import org.profit.telegrambot.service.CategoryService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.profit.telegrambot.ShopBot.conf;
import static org.profit.telegrambot.container.ComponentContainer.quantityMap;

public class InlineKeyboardUtil {

    private static InlineKeyboardButton createButton(String buttonText, String data) {
        InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
        button.setCallbackData(data);
        return button;
    }
    private static List<InlineKeyboardButton> createRow(InlineKeyboardButton... buttons) {
        return Arrays.asList(buttons);
    }
    private static List<List<InlineKeyboardButton>> createRowList(List<InlineKeyboardButton>... rows) {
        return Arrays.asList(rows);
    }


    public static InlineKeyboardMarkup categoryShow() {
        CategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> listList = new ArrayList<>();
        for (Category category : Database.categoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton(category.getName());
            button.setCallbackData("category/" + category.getId());
            buttonList.add(button);
            listList.add(buttonList);
        }

        InlineKeyboardButton button = createButton("Back", "to_main_menu");
        List<InlineKeyboardButton> row = createRow(button);
        listList.add(row);

        return new InlineKeyboardMarkup(listList);
    }

    public static InlineKeyboardMarkup categoryCRUDMenu() {
        InlineKeyboardButton addCategoriesAndProd = createButton("Add category or product", "add_new");
        InlineKeyboardButton showCategoriesAndProd = createButton("Show categories or products", "show_category_list");
        InlineKeyboardButton renameCategory = createButton("Rename category", "rename_category");
        InlineKeyboardButton deleteCategory = createButton("Delete category", "delete_category");


        List<InlineKeyboardButton> row1 = createRow(addCategoriesAndProd);
        List<InlineKeyboardButton> row2 = createRow(showCategoriesAndProd);
        List<InlineKeyboardButton> row3 = createRow(renameCategory);
        List<InlineKeyboardButton> row4 = createRow(deleteCategory);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1,row2, row3, row4);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup categoryShowForProducts() {

        CategoryService.loadCategoryList();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Category category : Database.categoryList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton(category.getName());
            button.setCallbackData("category/" + category.getId());
            buttonList.add(button);
            rowList.add(buttonList);
        }

        InlineKeyboardButton button = createButton("Back", "back_to_products_crud");
        List<InlineKeyboardButton> row = createRow(button);
        rowList.add(row);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup menu() {

        InlineKeyboardButton menuButton = createButton("📠Our products", "to_categories");
        InlineKeyboardButton addressButton = createButton("🏪Our address", "shop_url");
        InlineKeyboardButton contactButton = createButton("📞Our social networks", "web_networks");
        InlineKeyboardButton helpButton = createButton("💬Help", "help");

        addressButton.setUrl(conf.getProperty("googleMapsUrl"));

        List<InlineKeyboardButton> row1 = createRow(menuButton);
        List<InlineKeyboardButton> row2 = createRow(addressButton);
        List<InlineKeyboardButton> row3 = createRow(helpButton);
        List<InlineKeyboardButton> row4 = createRow(contactButton);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2, row3, row4);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup createNetAddressMenu() {
        InlineKeyboardButton instagram = createButton("📤 Instagram", "instagram");
        InlineKeyboardButton telegram = createButton("📤 Telegram", "telegram");
        InlineKeyboardButton back = createButton("◀ BACK", "to_main_menu");

        instagram.setUrl(conf.getProperty("instagramUrl"));
        telegram.setUrl(conf.getProperty("tgChannelUrl"));


        List<InlineKeyboardButton> row1 = createRow(instagram);
        List<InlineKeyboardButton> row2 = createRow(telegram);
        List<InlineKeyboardButton> row3 = createRow(back);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2, row3);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup confirmAddProduct() {

        InlineKeyboardButton commit = createButton("Yes", "add_product_commit");
        InlineKeyboardButton cancel = createButton("No", "add_product_cancel");

        return new InlineKeyboardMarkup(createRowList(createRow(commit, cancel)));
    }

    public static InlineKeyboardMarkup confirmAddCategory() {
        InlineKeyboardButton commit = createButton("Yes", "add_category_commit");
        InlineKeyboardButton cancel = createButton("No", "add_category_cancel");

        return new InlineKeyboardMarkup(createRowList(createRow(commit, cancel)));
    }

    public static InlineKeyboardMarkup confirmDeleteCategory() {
        InlineKeyboardButton commit = createButton("Yes", "delete_category_commit");
        InlineKeyboardButton cancel = createButton("No", "delete_category_cancel");

        return new InlineKeyboardMarkup(createRowList(createRow(commit, cancel)));
    }

    public static InlineKeyboardMarkup addToCart(int productId){
        InlineKeyboardButton addToCart = createButton("Add to cart?", "add_to_cart/" + productId);

        return new InlineKeyboardMarkup(createRowList(createRow(addToCart)));
    }

    public static InlineKeyboardMarkup backToCategories(){
        InlineKeyboardButton addToCart = createButton("◀ BACK", "show_category_list");

        return new InlineKeyboardMarkup(createRowList(createRow(addToCart)));
    }

    public static InlineKeyboardMarkup deleteFromCart(int productId){
        InlineKeyboardButton deleteFromCart = createButton("Delete from cart", "delete_from_cart/" + productId);

        return new InlineKeyboardMarkup(createRowList(createRow(deleteFromCart)));
    }

    public static InlineKeyboardMarkup deleteOrUpdateProduct(int productId){
        InlineKeyboardButton addProduct = createButton("Delete product", "delete_product/" + productId);
        InlineKeyboardButton updateProduct = createButton("Update product", "update_product/" + productId);

        List<InlineKeyboardButton> row1 = createRow(addProduct);
        List<InlineKeyboardButton> row2 = createRow(updateProduct);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup updateProduct() {
        InlineKeyboardButton changeName = createButton("🖊 Change name 🖊", "change_name");
        InlineKeyboardButton changePrice = createButton("💸 Change price 💸", "change_price");
        InlineKeyboardButton changeDescription = createButton("🖊 Change description🖊", "change_description");

        List<InlineKeyboardButton> row1 = createRow(changeName, changePrice);
        List<InlineKeyboardButton> row2 = createRow(changeDescription);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup productQuantity(String chatId){
        InlineKeyboardButton back = createButton("<", "back");
        InlineKeyboardButton forward = createButton(">", "forward");
        InlineKeyboardButton quantity = createButton(String.valueOf(quantityMap.get(chatId)), "quantity");
        InlineKeyboardButton accept = createButton("yes", "accept_quantity");
        InlineKeyboardButton decline = createButton("no", "decline_quantity");

        List<InlineKeyboardButton> row1 = createRow(back, quantity ,forward);
        List<InlineKeyboardButton> row2 = createRow(decline, accept);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup addNew(){
        InlineKeyboardButton addCategory = createButton("Category", "add_category");
        InlineKeyboardButton addProduct = createButton("Product", "add_product");
        InlineKeyboardButton back = createButton("Back", "to_main_menu");

        List<InlineKeyboardButton> row1 = createRow(addCategory);
        List<InlineKeyboardButton> row2 = createRow(addProduct);
        List<InlineKeyboardButton> row3 = createRow(back);

        List<List<InlineKeyboardButton>> rowList = createRowList(row1, row2, row3);

        return new InlineKeyboardMarkup(rowList);
    }

}
