package org.maxsid.work.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.bot.kafka.service.KafkaProducerService;
import org.maxsid.work.bot.service.CoreServiceClient;
import org.maxsid.work.dto.RouteRequest;
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

@Slf4j
@Component
public class TransportBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final Map<Long, UserState> userStates;
    private final Map<Long, RouteRequest> tempSettings;
    private final KafkaProducerService kafkaProducerService;
    private final CoreServiceClient coreServiceClient;

    private enum UserState {
        IDLE,
        AWAITING_HOME_ADDRESS,
        AWAITING_WORK_ADDRESS,
        AWAITING_WORK_TIME
    }

    public TransportBot(@Value("${bot.token}") String botToken,
                        @Value("${bot.username}") String botUsername, KafkaProducerService kafkaProducerService,
                        CoreServiceClient coreServiceClient) {
        super(botToken);
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.kafkaProducerService = kafkaProducerService;
        this.userStates = new HashMap<>();
        this.tempSettings = new HashMap<>();
        this.coreServiceClient = coreServiceClient;
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

            case "/notifications_on":
                enableNotifications(chatId, true);
                userStates.put(chatId, UserState.IDLE);
                break;

            case "/notifications_off":
                enableNotifications(chatId, false);
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
                /start - начать работу
                /settings - Настройка домашнего и рабочего адреса
                /calculate - Расчет времени выезда
                /help - Помощь и инструкции
                /notifications_on - включить уведомления
                /notifications_off - выключить уведомления
                """;
        sendMessage(chatId, welcomeText);
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = """
                📋 Инструкция по использованию бота:
                
                   Начать работу /start
 
                1. Сначала настройте маршрут командой /settings
                2. Введите домашний адрес
                3. Введите рабочий адрес
                4. Введите время прибытия на работу (например: 9:00)

                5. Получите расчет времени выезда командой /calculate
                
                🔔 *Управление уведомлениями:
                   
                   Включить уведомления /notifications_on
                   Выключить уведомления /notifications_off

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

            //Отправляем через KAFKA
            kafkaProducerService.sendUserSettingsToCore(chatId, settings);

            userStates.put(chatId, UserState.IDLE);

            sendMessage(chatId, "⏳ Настройки отправлены на обработку. Вы получите уведомление, когда они будут сохранены.");

        } catch (DateTimeParseException e) {
            sendMessage(chatId, "❌ Неверный формат времени. Введите время в формате ЧЧ:MM (например 9:00 или 09:00):");
        }
    }

    private void calculateRoute(Long chatId) {
        try {
            RouteRequest routeRequest = tempSettings.get(chatId);

            //отправляем запрос через KAFKA
            kafkaProducerService.sendRouteCalculationRequestToCore(chatId, routeRequest);

            tempSettings.remove(chatId);

            // Вместо этого:
            sendMessage(chatId, "⏳ Запрос на расчет маршрута отправлен. Вы получите уведомление, когда расчет будет завершен.");

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

    public void enableNotifications(Long chatId, boolean enabled) {
        try {
            // Проверяем, есть ли настройки у пользователя
            if (coreServiceClient.getUserSettings(chatId) == null) {
                sendMessage(chatId, "❌ Сначала настройте маршрут через /settings");
                return;
            }

            coreServiceClient.enableNotifications(chatId, enabled);

            String status = enabled ? "включены ✅" : "выключены 🔕";
            String emoji = enabled ? "✅" : "🔕";

            sendMessage(chatId, emoji + " Уведомления " + status + "!\n\n" +
                    (enabled ?
                            "Вы будете получать уведомления за 30 минут до выезда." :
                            "Вы не будете получать уведомления."));

        } catch (Exception e) {
            log.error("Failed to enable notifications for user {}: {}", chatId, e.getMessage());
            sendMessage(chatId, "❌ Ошибка при изменении статуса уведомлений. Попробуйте позже.");
        }
    }
}


