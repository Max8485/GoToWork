package org.maxsid.work.core.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.exceptions.FailedToSaveUserSettingsException;
import org.maxsid.work.core.exceptions.UserSettingsNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalUserSettingsHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserSettingsNotFoundException.class)
    public String handleUserSettingsNotFoundException(UserSettingsNotFoundException e) {
        log.error(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedToSaveUserSettingsException.class)
    public String handleFailedToSaveUserSettingsException(FailedToSaveUserSettingsException e) {
        log.error(e.getMessage(), e);
        return e.getMessage();
    }
}
