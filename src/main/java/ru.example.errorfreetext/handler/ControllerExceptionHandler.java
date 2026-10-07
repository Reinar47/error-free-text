package ru.example.errorfreetext.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.example.errorfreetext.dto.response.ErrorResponse;
import ru.example.errorfreetext.exception.TaskNotFoundException;
import ru.example.errorfreetext.exception.YandexApiException;

import java.time.LocalDateTime;

import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.internalErrorMessage;
import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.methodArgumentTypeMismatchMessage;
import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.notFoundErrorMessage;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final int VALIDATION_ERROR_CODE = 40001;
    private static final int CONSTRAINT_VIOLATION_ERROR_CODE = 40002;
    private static final int TYPE_MISMATCH_ERROR_CODE = 40003;
    private static final int MISSING_PARAMETER_ERROR_CODE = 40004;
    private static final int NOT_FOUND_ERROR_CODE = 40401;
    private static final int INTERNAL_ERROR_CODE = 50001;
    private static final int YANDEX_API_ERROR_CODE = 50002;

    @ExceptionHandler(TaskNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleTaskNotFoundException(
            @NonNull TaskNotFoundException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(ex.getMessage(), NOT_FOUND_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(VALIDATION_ERROR_CODE + " Validation failed");
        ErrorResponse error = buildError(errorMessage, VALIDATION_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
            @NonNull ConstraintViolationException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        String[] split = ex.getMessage().split(":");
        String errorMessage = split.length > 2 ? (split[1] + split[2]).trim() : ex.getMessage();
        ErrorResponse error = buildError(errorMessage, CONSTRAINT_VIOLATION_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            @NonNull MethodArgumentTypeMismatchException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(
                methodArgumentTypeMismatchMessage + ex.getName(),
                TYPE_MISMATCH_ERROR_CODE,
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(
                ex.getMessage(),
                MISSING_PARAMETER_ERROR_CODE,
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            @NonNull NoResourceFoundException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(notFoundErrorMessage, NOT_FOUND_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(YandexApiException.class)
    protected ResponseEntity<ErrorResponse> handleYandexApiException(
            @NonNull YandexApiException ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(ex.getMessage(), YANDEX_API_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleInternalException(
            @NonNull Exception ex,
            @NonNull jakarta.servlet.http.HttpServletRequest request) {
        ErrorResponse error = buildError(internalErrorMessage, INTERNAL_ERROR_CODE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponse buildError(String message, int errorCode, String path) {
        return new ErrorResponse(message, errorCode, LocalDateTime.now(), path);
    }
}
