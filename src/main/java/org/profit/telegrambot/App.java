package org.profit.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.profit.telegrambot.container.ComponentContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


public class App {

    public static String botName;
    public static String botToken;

    public static void main(String[] args) {

        try {
            File inputFile = new File("properties.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("botinformation");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList botNameList = eElement.getElementsByTagName("bot_name");
                    for (int count = 0; count < botNameList.getLength(); count++) {
                        Node node1 = botNameList.item(count);
                        if (node1.getNodeType() == node1.ELEMENT_NODE) {
                            Element car = (Element) node1;
                            botName = car.getTextContent();
                        }
                    }
                    NodeList botTokenList = eElement.getElementsByTagName("bot_token");
                    for (int count = 0; count < botTokenList.getLength(); count++) {
                        Node node1 = botTokenList.item(count);
                        if (node1.getNodeType() == node1.ELEMENT_NODE) {
                            Element car = (Element) node1;
                            botToken = car.getTextContent();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            ShopBot shopBot = new ShopBot();
            ComponentContainer.MY_TELEGRAM_BOT = shopBot;
            telegramBotsApi.registerBot(shopBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
