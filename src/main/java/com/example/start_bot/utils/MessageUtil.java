package com.example.start_bot.utils;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessageUtil {

    public static CurrentMessage getMessage(Long chatID,String text){
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatID.toString());
        sendMessage.setText(text);

        currentMessage.setSendMessage(sendMessage);
        currentMessage.setType(MessageType.SEND_MESSAGE);
        return currentMessage;
    }
}
