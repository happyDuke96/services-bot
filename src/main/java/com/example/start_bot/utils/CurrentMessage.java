package com.example.start_bot.utils;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Getter
@Setter
public class CurrentMessage {
    private SendMessage sendMessage;
    private SendPhoto sendPhoto;
    private DeleteMessage deleteMessage;
    private EditMessageText editMessageText;

    MessageType type;

}
