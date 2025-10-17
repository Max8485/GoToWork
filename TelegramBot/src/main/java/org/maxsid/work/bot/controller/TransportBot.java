package org.maxsid.work.bot.controller;

import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.bot.service.CoreServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransportBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final CoreServiceClient coreServiceClient;
    private final Map<Long, UserState> userStates;
    private final Map<Long, RouteRequest> tempSettings;

    private enum UserState {
        IDLE,
        AWAITING_HOME_ADDRESS,
        AWAITING_WORK_ADDRESS,
        AWAITING_WORK_TIME
    }

    public TransportBot(@Value("${bot.token}") String botToken,
                        @Value("${bot.username}") String botUsername,
                        CoreServiceClient coreServiceClient) {
        super(botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.coreServiceClient = coreServiceClient;
        this.userStates = new HashMap<>();
        this.tempSettings = new HashMap<>();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            try {
                handleMessage(chatId, messageText);
            } catch (Exception e) {
                sendMessage(chatId, "Произошла ошибка. Пожалуйста, попробуйте позже.");
            }
        }
    }

    private void handleMessage(Long chatId, String text) {
        UserState currentState = userStates.getOrDefault(chatId, UserState.IDLE);

        switch (text) {
            case "/start":
                sendWelcomeMessage(chatId);
                userStates.put(chatId, UserState.IDLE);
                break;
            case "/settings":
                startSettingsProcess(chatId);
                break;
            case "/calculate":
                calculateRoute(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                userStates.put(chatId, UserState.IDLE);
                break;
            default:
                handleStateMessage(chatId, text, currentState);
        }
    }

    private void handleStateMessage(Long chatId, String text, UserState currentState) {
        switch (currentState) {
            case AWAITING_HOME_ADDRESS:
                handleHomeAddress(chatId, text);
                break;
            case AWAITING_WORK_ADDRESS:
                handleWorkAddress(chatId, text);
                break;
            case AWAITING_WORK_TIME:
                handleWorkTime(chatId, text);
                break;
            default:
                sendMessage(chatId, "Используйте команды:\n/settings - настройки маршрута\n/calculate - расчет времени выезда\n/help - помощь");
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = """
                🚗 Добро пожаловать в Transport Scheduler!

                Я помогу вам рассчитать оптимальное время выезда с учетом пробок.

                Доступные команды:
                /settings - Настройка домашнего и рабочего адреса
                /calculate - Расчет времени выезда
                /help - Помощь и инструкции
                """;
        sendMessage(chatId, welcomeText);
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = """
                📋 Инструкция по использованию бота:

                1. Сначала настройте маршрут командой /settings
                2. Введите домашний адрес
                3. Введите рабочий адрес
                4. Введите время прибытия на работу (например: 9:00)

                5. Получите расчет времени выезда командой /calculate

                ⚠️ Бот работает только в будние дни
                ⏰ Учитывается 30-минутный буфер на сборы
                🚦 Расчет включает пробки на дорогах
                """;
        sendMessage(chatId, helpText);
    }

    private void startSettingsProcess(Long chatId) {
        tempSettings.put(chatId, new RouteRequest());
        userStates.put(chatId, UserState.AWAITING_HOME_ADDRESS);
        sendMessage(chatId, "🏠 Введите ваш домашний адрес (например: Москва, Кутузовский проспект 32):");
    }

    private void handleHomeAddress(Long chatId, String address) {
        if (address.length() < 5) {
            sendMessage(chatId, "❌ Адрес слишком короткий. Пожалуйста, введите полный адрес:");
            return;
        }

        RouteRequest settings = tempSettings.get(chatId);
        settings.setHomeAddress(address.trim());
        settings.setTimeZone("Europe/Moscow"); // По умолчанию

        userStates.put(chatId, UserState.AWAITING_WORK_ADDRESS);
        sendMessage(chatId, "🏢 Теперь введите ваш рабочий адрес:");
    }

    private void handleWorkAddress(Long chatId, String address) {
        if (address.length() < 5) {
            sendMessage(chatId, "❌ Адрес слишком короткий. Пожалуйста, введите полный адрес:");
            return;
        }

        RouteRequest settings = tempSettings.get(chatId);
        settings.setWorkAddress(address.trim());
        userStates.put(chatId, UserState.AWAITING_WORK_TIME);
        sendMessage(chatId, "⏰ Введите время, к которому нужно быть на работе (в формате ЧЧ:MM, например 9:00):");
    }

    private void handleWorkTime(Long chatId, String timeText) {
        try {
            // Валидация времени
            LocalTime workTime = parseTime(timeText);

            if (workTime.isBefore(LocalTime.of(6, 0)) || workTime.isAfter(LocalTime.of(22, 0))) {
                sendMessage(chatId, "❌ Время должно быть между 6:00 и 22:00. Введите корректное время:");
                return;
            }

            RouteRequest settings = tempSettings.get(chatId);
            settings.setArrivalTime(workTime.format(DateTimeFormatter.ofPattern("HH:mm")));

            // Сохраняем настройки в core service
            coreServiceClient.saveUserSettings(chatId, settings);

            userStates.put(chatId, UserState.IDLE);
            tempSettings.remove(chatId);

            String confirmationText = String.format("""
                            ✅ Настройки успешно сохранены!

                            🏠 Домашний адрес: %s
                            🏢 Рабочий адрес: %s
                            ⏰ Время прибытия: %s
                            🌍 Часовой пояс: %s

                            Теперь используйте команду /calculate для расчета времени выезда.
                            """,
                    settings.getHomeAddress(),
                    settings.getWorkAddress(),
                    settings.getArrivalTime(),
                    settings.getTimeZone());

            sendMessage(chatId, confirmationText);

        } catch (DateTimeParseException e) {
            sendMessage(chatId, "❌ Неверный формат времени. Введите время в формате ЧЧ:MM (например 9:00 или 09:00):");
        }
    }

    private void calculateRoute(Long chatId) {
        try {
            var response = coreServiceClient.calculateRoute(chatId);

            if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                sendMessage(chatId, response.getMessage());
            } else {
                sendMessage(chatId, "❌ Не удалось рассчитать маршрут. Проверьте настройки командой /settings");
            }

        } catch (Exception e) {
            sendMessage(chatId, "❌ Сначала настройте маршрут с помощью команды /settings");
        }
    }

    private LocalTime parseTime(String timeText) throws DateTimeParseException {
        String normalizedTime = timeText.trim().replace(";", ":");

        if (normalizedTime.contains(":")) {
            String[] parts = normalizedTime.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                    throw new DateTimeParseException("Invalid time", normalizedTime, 0);
                }

                return LocalTime.of(hours, minutes);
            }
        } else {
            if (normalizedTime.length() == 3) {
                normalizedTime = "0" + normalizedTime;
            }

            if (normalizedTime.length() == 4) {
                int hours = Integer.parseInt(normalizedTime.substring(0, 2));
                int minutes = Integer.parseInt(normalizedTime.substring(2, 4));

                if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                    throw new DateTimeParseException("Invalid time", normalizedTime, 0);
                }

                return LocalTime.of(hours, minutes);
            }
        }

        throw new DateTimeParseException("Invalid time format", normalizedTime, 0);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message to chat " + chatId + ": " + e.getMessage());
        }
    }
}


