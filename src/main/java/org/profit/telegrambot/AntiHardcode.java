package org.profit.telegrambot;

public class AntiHardcode {

    public static String botDescription = """
            This is a test bot for Java courses disguised as a tea shop.

            It has two simple and one inline keyboards, can send images, and receive them.

            More features coming soon :)""";

    public static String greetingMessage = """
            Welcome to our Tea Shop!

            We have a wide selection of traditional Chinese tea for reasonable prices.

            Just click 'Go Shopping' to see what we can offer you!

            For any additional information click 'Shop's Info'""";

    protected static String token = "5793024716:AAHBn08nBwGe0D864bLUFX4UtFhLt412ppk";
    public static String name = "R231Bot";
    public static int addCounter = 1;
    public static int pageCounter = 1;
    public static String imagePath;

    public void chosePath(int pageCounter) {

        switch (pageCounter){
            case 1 ->  imagePath = "AgACAgIAAxkBAAIJCWMPh9WArhJP40wpbFuk8C0Ex_KdAALWvjEbxmqASEb1cdcWjNHNAQADAgADeQADKQQ";
            case 2 -> imagePath = "AgACAgIAAxkBAAIJCmMPh_lBNBhDfBsd1mCRljKqM4cqAALXvjEbxmqASPt-0PyHLogOAQADAgADeQADKQQ";
            case 3 -> imagePath = "AgACAgIAAxkBAAIJC2MPiDUcFJWhMdqwenRXg-rbLBl-AALrvjEbxmqASMliwo7z-XhRAQADAgADeAADKQQ";
            case 4 -> imagePath = "AgACAgIAAxkBAAIJDGMPiJ-HQRxQAAH00hXjCXappD7RxAAC9b4xG8ZqgEicMz0Psyx_oQEAAwIAA3gAAykE";
            case 5 -> imagePath = "AgACAgIAAxkBAAIJDWMPiMFBW8HzFhhR6btOtQEUsRHHAAL4vjEbxmqASPg-ciL6Pe2GAQADAgADeAADKQQ";
            case 6 -> imagePath = "AgACAgIAAxkBAAIJDmMPiON05Bw-OufwDGZ3JklUQCzvAAL5vjEbxmqASIPi_6XO2rjZAQADAgADeAADKQQ";
        }

    }
}

