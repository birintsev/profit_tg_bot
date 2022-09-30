package org.profit.telegrambot.service;

import org.profit.telegrambot.container.ComponentContainer;
import org.profit.telegrambot.database.Database;
import org.profit.telegrambot.model.CartProduct;
import org.profit.telegrambot.model.Customer;
import org.profit.telegrambot.model.Product;
import org.profit.telegrambot.util.InlineKeyboardUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;
import java.util.Optional;

public class CartProductService {
    public static void addToCart(String userId, Integer productId, Integer quantity){
        Customer customer = CustomerService.getCustomerById(userId);
        Product product = ProductService.getProductById(productId);

        Optional<CartProduct> optional = Database.cartProducts.stream()
                .filter(cartProduct -> {
                    if (!Objects.equals(cartProduct.getCustomer().getId(), customer.getId())) {
                        return false;
                    }
                    assert product != null;
                    return Objects.equals(cartProduct.getProduct().getId(), product.getId()) && Objects.equals(cartProduct.getProduct().getCategoryId(), product.getCategoryId());
                })
                .findFirst();

        if(optional.isPresent()){
            CartProduct cartProduct = optional.get();
            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
        }else{
            CartProduct cartProduct = new CartProduct(++ComponentContainer.generalId, customer, product, quantity);
            Database.cartProducts.add(cartProduct);
        }
    }

    public static void showCart(String chatId, String customerId){
        ProductService.loadProductList();
        ComponentContainer.my_telegram_bot.sendMessage(chatId, "Your cart:", (InlineKeyboardMarkup) null);

        for (CartProduct cartProduct : Database.cartProducts){
            if (Objects.equals(cartProduct.getCustomer().getId(), customerId)){
                Product product = cartProduct.getProduct();
                ComponentContainer.my_telegram_bot.sendPhoto(chatId, String.format("""
                                    💻 Product: %s
                                    💸 Cost: %s
                                    Description: %s
                                    Quantity: %s""",
                                product.getName(),
                                product.getPrice(),
                                product.getDescription(),
                                cartProduct.getQuantity()),
                                product.getImage(),
                                InlineKeyboardUtil.deleteFromCart(cartProduct.getId()));
            }
        }
    }
    public static void deleteFromCart(Integer cartProductId, int messageId, String chatId) {
        Database.cartProducts.removeIf(cartProduct -> cartProduct.getId().equals(cartProductId));
        ComponentContainer.my_telegram_bot.deleteMessage(chatId, messageId);
    }
}
