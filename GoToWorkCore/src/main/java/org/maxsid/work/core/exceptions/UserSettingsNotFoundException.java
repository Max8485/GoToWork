package org.maxsid.work.core.exceptions;

public class UserSettingsNotFoundException extends RuntimeException {
    public UserSettingsNotFoundException(Long userId) {
        super(String.format("Настройки пользователя с ID %d не найдены", userId));
    }
}
