package com.example.stockmarket.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";

    public StockMarketBot(@Value("${bot.token}")String botToken) { // не понятно зачем
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) { // обрабатываем все входящие действия пользователя
        if(!update.hasMessage() || !update.getMessage().hasText()) { // если сообщение не пришло или пришло сообщение без текста то выходым из метода
            return;
        }
        String message = update.getMessage().getText(); // Если сообщение пришло то сохраняем его в отдельную переменную
        Long chatId = update.getMessage().getChatId(); // идентификатор чата, нужен для того чтобы отправить сообщение нужному пользователю

        switch (message) { // проверяем соответсвие ( НУЖНО НАПИСАТЬ ДЛЯ КАЖДОГО ОБРАБОТЧИКА НАПИСАТЬ ОТДЕЛЬНЫЙ КЛАСС )
            case START -> {
                String username = update.getMessage().getChat().getUserName(); // получаем userName пользователя для работы команды start
                startCommand(chatId, username);
            }
            case HELP ->
                helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    public void startCommand(Long chatId, String userName) { // chatId - нужен для того чтобы отправить нужно пользователю сообщение
        String text = "Добро пожаловать в stockMarketBot, %s!" +
                "\nДоступные команды:" +
                "\n/help - получение справки";
        String format= String.format(text, userName);
        sendMessage(chatId, format);
    }

    public void helpCommand(Long chatId) {
        String text = "Доступные команды:" +
                "\n/start - запуск начального меню" +
                "\n/help - просмотр доступных команд";
        sendMessage(chatId, text);
    }

    public void unknownCommand(Long chatId) {
        String text = "Неопознанная команда, для просмотра доступных команд воспользуйтесь: /help";
        sendMessage(chatId, text);
    }

    public void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId); // переделываем long в String
        SendMessage sendMessage = new SendMessage(chatIdStr, text); // отправляем сообщение, по индефикатору пользователя и само сообщение вторым параметром
        try {
            execute(sendMessage); // выполняем, отправку сообщения
        } catch (TelegramApiException e) { // можно добавить свою ошибку, или оставить но изменить под каждую ситуацию ( подумать )
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() { // получение имени бота
        return "stockMarketBot";
    }
}
