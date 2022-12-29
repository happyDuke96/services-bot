package com.example.start_bot.botapi;

import com.example.start_bot.model.Currency;
import com.example.start_bot.model.Weather;
import com.example.start_bot.model.WeatherCache;
import com.example.start_bot.utils.CurrentMessage;
import com.example.start_bot.utils.Keyboards;
import com.example.start_bot.utils.MessageType;
import com.example.start_bot.utils.MessageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BotAPI {
    private boolean someField;
    private List<Currency> currencyList;
    ;
    private List<Weather> weatherList;
    private List<BotCommand> myCommands;


    public CurrentMessage handle(Update update) {

        CurrentMessage currentMessage = new CurrentMessage();
        if (update.hasMessage()) {
            currentMessage = handleMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            currentMessage = handleCallBack(update.getCallbackQuery());
        }
        return currentMessage;

    }

    private CurrentMessage handleCallBack(CallbackQuery callbackQuery) {

        String request = callbackQuery.getData();
        String command = request.split("/")[0];
        Integer id = Integer.valueOf(request.split("/")[1]);
        if (command.equals("weather")) {
            Weather weather = weatherList.get(id);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            editMessageText.setText(String.format("The temperature in %s %s degrees", weather.getCity(),
                    weather.getData().getTemp()));
            CurrentMessage currentMessage = new CurrentMessage();
            currentMessage.setEditMessageText(editMessageText);
            currentMessage.setType(MessageType.EDIT_MESSAGE);
            return currentMessage;

        }
        return null;
    }

    private CurrentMessage handleMessage(Message message) {
        String inputText = "";
        CurrentMessage currentMessage = new CurrentMessage();
        if (message.hasText()) {
            inputText = message.getText();
        }
        if (someField) {
            currentMessage = handleCalculate(message);
        }
        if (inputText.equals("/help")) {
            currentMessage = handleCommands(message);
        }
        if (inputText.equals("/trains")) {
            currentMessage = handleTrains(message);
        }
        if (inputText.equals("/start")) {
            currentMessage = handleStart(message);
        }
        if (inputText.equals("/course")) {
            currentMessage = getCurrentCurrency(message);
        }
        if (inputText.equals("/manual")) {
            someField = true;
            return MessageUtil.getMessage(message.getChatId(), "Enter number");
        }
        if (inputText.equals("/weather")) {
            try {
//                currentMessage = deleted(message);
                currentMessage = handleWeather(message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return currentMessage;

    }

    private CurrentMessage handleCommands(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        {
            myCommands.add(new BotCommand("/start", "starter bot"));
            myCommands.add(new BotCommand("/course", "currnecy info"));
            myCommands.add(new BotCommand("/manual", "manual currency"));
            myCommands.add(new BotCommand("/weaher", "weather info"));
            myCommands.add(new BotCommand("/trains", "trains info"));

        }
        return null;

    }


    public CurrentMessage getCurrentCurrency(Message message) {
        currencyList = new ArrayList<>();
        String URL = "https://api.uznews.uz/api/v1/main/currencies";
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.getForEntity(URL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode result = mapper.readTree(response.getBody()).get("result");
            JsonNode currencies = result.get("currencies");
            for (int i = 0; i < currencies.size(); i++) {
                ObjectNode jsonNode = (ObjectNode) currencies.get(i);
                Currency currency = new Currency();
                currency.setName(jsonNode.get("Ccy").toString());
                String rate = jsonNode.get("Rate").toString();
                rate = rate.substring(1, rate.length() - 1);
                String diff = jsonNode.get("Diff").toString();
                diff = diff.substring(1, diff.length() - 1);
                currency.setRate(new BigDecimal(rate));
                currency.setDiff(new BigDecimal(diff));
                currency.setNameUZ(jsonNode.get("CcyNm_UZ").toString());
                currency.setNameUZC(jsonNode.get("CcyNm_RU").toString());
                currency.setNameRU(jsonNode.get("CcyNm_UZC").toString());

                currency.setNameRU(currency.getNameRU().replaceAll("\"", ""));
                currency.setNameUZ(currency.getNameUZ().replaceAll("\"", ""));
                currency.setNameUZC(currency.getNameUZC().replaceAll("\"", ""));
                currencyList.add(currency);
            }
        } catch (JsonProcessingException e) {
            return MessageUtil.getMessage(message.getChatId(), "Please try again");
        }
        StringBuilder responseText = new StringBuilder();
        responseText.append("Current exchange rate:\n");

        for (Currency currency : currencyList) {
            responseText.append(currency.getNameUZ()).append(" (").append(currency.getName())
                    .append(")\n");
            responseText.append("1").append(currency.getName()).append(currency.getRate())
                    .append(" ").append("UZS\n").append("Difference ").append(currency.getDiff()).append(" UZS").append("\n");
            responseText.append("\n\n");
        }
        return MessageUtil.getMessage(message.getChatId(), responseText.toString());
    }

    private CurrentMessage handleStart(Message message) {
        return MessageUtil.getMessage(message.getChatId(), "Welcome,choose service");
    }

    private CurrentMessage handleCalculate(Message message) {
        StringBuilder input = new StringBuilder();
        BigDecimal result = null;
        Pattern regex = Pattern.compile("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$");
        Matcher matcher = regex.matcher(message.getText());
        if (matcher.find()) {
            for (Currency currency : currencyList) {
                result = new BigDecimal(message.getText()).divide(currency.getRate(), 2, RoundingMode.CEILING);
                input.append(message.getText()).append("\tUZS\t").append(result).append(currency.getName()).append("\n");
            }
            return MessageUtil.getMessage(message.getChatId(), String.valueOf(input));
        }
        return MessageUtil.getMessage(message.getChatId(), "Invalid number please try again!");
    }


    private CurrentMessage handleWeather(Message message) throws JsonProcessingException {
        weatherList = new ArrayList<>();
        String URL = "https://api.uznews.uz/api/v1/main/weather";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode body = mapper.readTree(response.getBody());
        JsonNode arrayList = body.get("result").get("weather");
        for (int i = 0; i < arrayList.size(); i++) {
            ObjectNode objectNode = (ObjectNode) arrayList.get(i);
            Weather weather = new Weather();
            weather.setCity(String.valueOf(objectNode.get("city")));
            WeatherCache weatherCache = new WeatherCache();
            weatherCache.setIcon(String.valueOf(objectNode.get("data").get("icon")));
            weatherCache.setTemp(Integer.valueOf(String.valueOf(objectNode.get("data").get("temp"))));
            weather.setData(weatherCache);
            weatherList.add(weather);
        }
        CurrentMessage currentMessage = new CurrentMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose your region");
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        sendMessage.setReplyMarkup(Keyboards.weatherMarkUp(weatherList));
        currentMessage.setType(MessageType.SEND_MESSAGE);
        currentMessage.setSendMessage(sendMessage);
        return currentMessage;
    }


    public CurrentMessage deleted(Message message) {
        CurrentMessage currentMessage = new CurrentMessage();
        DeleteMessage deleteMessage = new DeleteMessage();
        if (message != null) {
            deleteMessage.setChatId(message.getChatId().toString());
            deleteMessage.setMessageId(message.getMessageId());
            currentMessage.setDeleteMessage(deleteMessage);
            return currentMessage;
        }
        return null;
    }

    private CurrentMessage handleTrains(Message message) {
        return null;
    }


}
