package org.profit.telegrambot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class AntiHardcode {

   static final String botDescription = """
            This is a test bot for Java courses disguised as a tea shop.

            It has two simple and one inline keyboards, can send images, and receive them.

            More features coming soon :)""";

   static final String greetingMessage = """
            Welcome to our Tea Shop!

            We have a wide selection of traditional Chinese tea for reasonable prices.

            Just click 'Go Shopping' to see what we can offer you!

            For any additional information click 'Shop's Info'""";

    protected static final String token = "5793024716:AAHBn08nBwGe0D864bLUFX4UtFhLt412ppk";
    static final String name = "R231Bot";
    static int addCounter = 1;
    static int pageCounter = 1;
    static String imagePath;
    static String photoCaption;
    static final long totalItems = 6;

    static ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    public static void chosePathAndDescription(int pageCounter) {

        switch (pageCounter){
            case 1 -> {
                imagePath = "AgACAgIAAxkBAAIJCWMPh9WArhJP40wpbFuk8C0Ex_KdAALWvjEbxmqASEb1cdcWjNHNAQADAgADeQADKQQ";
                photoCaption = "Description of the first item";
            }
            case 2 -> {
                imagePath = "AgACAgIAAxkBAAIJCmMPh_lBNBhDfBsd1mCRljKqM4cqAALXvjEbxmqASPt-0PyHLogOAQADAgADeQADKQQ";
                photoCaption = "Description of the second item";
            }
            case 3 -> {
                imagePath = "AgACAgIAAxkBAAIJC2MPiDUcFJWhMdqwenRXg-rbLBl-AALrvjEbxmqASMliwo7z-XhRAQADAgADeAADKQQ";
                photoCaption = "Description of the third item";
            }
            case 4 -> {
                imagePath = "AgACAgIAAxkBAAIJDGMPiJ-HQRxQAAH00hXjCXappD7RxAAC9b4xG8ZqgEicMz0Psyx_oQEAAwIAA3gAAykE";
                photoCaption = "Description of the fourth item";
            }
            case 5 -> {
                imagePath = "AgACAgIAAxkBAAIJDWMPiMFBW8HzFhhR6btOtQEUsRHHAAL4vjEbxmqASPg-ciL6Pe2GAQADAgADeAADKQQ";
                photoCaption = "Description of the fifth item";
            }
            case 6 -> {
                imagePath = "AgACAgIAAxkBAAIJDmMPiON05Bw-OufwDGZ3JklUQCzvAAL5vjEbxmqASIPi_6XO2rjZAQADAgADeAADKQQ";
                photoCaption = "Description of the sixth item";
            }
        }
    }
}

