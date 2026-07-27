package org.maxsid.work.core.exceptions;

public class FailedToSaveUserSettingsException extends RuntimeException{
    public FailedToSaveUserSettingsException(Long userId, Throwable cause) {
        super(String.format("Не удалось сохранить настройки пользователя с ID %d ", userId), cause);
    }
}
