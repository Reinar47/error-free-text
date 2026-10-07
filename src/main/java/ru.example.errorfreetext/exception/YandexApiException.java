package ru.example.errorfreetext.exception;

public class YandexApiException extends RuntimeException {

    public YandexApiException(String message) {
        super(message);
    }

    public YandexApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
