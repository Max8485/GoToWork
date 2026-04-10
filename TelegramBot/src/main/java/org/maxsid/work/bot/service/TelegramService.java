package org.maxsid.work.bot.service;

public interface TelegramService {

    void sendMessage(Long chatId, String text);
}
