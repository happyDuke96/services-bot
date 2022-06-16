package com.example.start_bot.botapi;

import com.example.start_bot.utils.CurrentMessage;
import com.example.start_bot.utils.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;


@Component
public class BotChild extends TelegramLongPollingBot {
    @Autowired
    BotAPI botAPI;
    @Override
    public String getBotUsername() {
        return "@app_start_bot";
    }


    @Override
    public String getBotToken() {
        return "5386408839:AAEH3o8dGun69EtUM9lov-wAZA0tVBZsGto";
    }

    @Override
    public void onUpdateReceived(Update update) {
        CurrentMessage message = botAPI.handle(update);
        if (message != null && message.getType() != null){
            executeMessage(message);
        }
    }


    private void executeMessage(CurrentMessage message) {
        MessageType type = message.getType();
        try {
            if (message.getType().equals(MessageType.SEND_MESSAGE)){
                execute(message.getSendMessage());
            }
            if (message.getType().equals(MessageType.SEND_PHOTO)){
                execute(message.getSendPhoto());
            }
            if (message.getType().equals(MessageType.DELETE_MESSAGE)){
                execute(message.getDeleteMessage());
            }
            if (message.getType().equals(MessageType.EDIT_MESSAGE)){
                execute(message.getEditMessageText());
            }
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }


}
