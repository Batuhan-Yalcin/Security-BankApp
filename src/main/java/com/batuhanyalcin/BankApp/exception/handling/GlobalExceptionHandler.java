package com.batuhanyalcin.BankApp.exception.handling;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.batuhanyalcin.BankApp.exception.BadRequestException;
import com.batuhanyalcin.BankApp.exception.BankAppException;
import com.batuhanyalcin.BankApp.exception.BusinessRuleException;
import com.batuhanyalcin.BankApp.exception.ConflictException;
import com.batuhanyalcin.BankApp.exception.DuplicateResourceException;
import com.batuhanyalcin.BankApp.exception.ForbiddenException;
import com.batuhanyalcin.BankApp.exception.InsufficientFundsException;
import com.batuhanyalcin.BankApp.exception.ResourceNotFoundException;
import com.batuhanyalcin.BankApp.exception.UnauthorizedException;
import com.batuhanyalcin.BankApp.exception.model.ErrorDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    // Özel exception'lar için handler metodları
    
    @ExceptionHandler(BankAppException.class)
    public ResponseEntity<ErrorDetails> handleBankAppException(BankAppException exception, HttpServletRequest request) {
        return createErrorResponse(
                exception.getStatus().value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorDetails> handleBusinessRuleException(BusinessRuleException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleForbiddenException(ForbiddenException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDetails> handleConflictException(ConflictException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateResourceException(DuplicateResourceException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDetails> handleInsufficientFundsException(InsufficientFundsException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    // Spring Security exception'ları
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_CREDENTIALS",
                "Geçersiz email veya şifre",
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_ERROR",
                "Kimlik doğrulama hatası: " + exception.getMessage(),
                request.getRequestURI()
        );
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "Bu işlemi yapmak için gerekli yetkiniz bulunmamaktadır",
                request.getRequestURI()
        );
    }
    
    // Validation exception'ları
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Doğrulama hatası")
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorDetails.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            validationErrors.put(fieldName, message);
        });
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Doğrulama hatası")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        validationErrors.forEach(errorDetails::addValidationError);
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    // Genel exception'lar
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, HttpServletRequest request) {
        log.error("Beklenmeyen bir hata oluştu:", exception);
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.",
                request.getRequestURI()
        );
    }
    
    // Yardımcı metodlar
    
    private ResponseEntity<ErrorDetails> createErrorResponse(int status, String errorCode, String message, String path) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(status));
    }
} 