package com.example.start_bot.utils;

import com.example.start_bot.model.Weather;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {
    public static InlineKeyboardMarkup weatherMarkUp(List<Weather> weatherList) {
        List<String> emojies = new ArrayList<>();
        {
            emojies.add(":confused:");
            emojies.add(":sleeping:");
            emojies.add(":angry:");
            emojies.add(":laughing:");
            emojies.add(":blush:");
            emojies.add(":smiley:");
            emojies.add(":relaxed:");
            emojies.add(":smirk:");
            emojies.add(":heart_eyes:");
            emojies.add(":relieved:");
            emojies.add(":satisfied:");

        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < weatherList.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            StringBuilder emojiText = new StringBuilder(EmojiParser.parseToUnicode(emojies.get(i)));
            inlineKeyboardButton.setText(weatherList.get(i).getCity() + " " + emojiText);
            inlineKeyboardButton.setCallbackData(String.format("%s/%s", "weather", i));
            row.add(inlineKeyboardButton);
            if (i % 2 == 1) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
