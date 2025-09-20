package uk.gitsoft.ems.exception;

import jakarta.servlet.http.HttpServletRequest;
// SLF4J logging API. LoggerFactory is used to create a logger instance for this class.
// The logger lets you log messages at different levels (info, warn, error, etc.).
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;

/**
 * GlobalExceptionHandler is a centralized error handler for all controllers.
 *
 * - The @RestControllerAdvice annotation tells Spring this class should apply
 *   to all @RestController methods in the application.
 * - Any exception thrown in a controller will be caught here (if a matching
 *   @ExceptionHandler method exists).
 * - This avoids duplicating try/catch logic in every controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger instance for this class (best practice: one per class).
    // It’s used to record warnings/errors in the application logs.
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle cases where a requested resource is not found.
     *
     * - Catches ResourceNotFoundException thrown anywhere in the app.
     * - Logs a warning.
     * - Returns a 404 response with a JSON body containing details.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex,
                                                              HttpServletRequest req) {
        log.warn("404 Not Found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),   // when the error occurred
                "status", status.value(),                // numeric status code (404)
                "error", status.getReasonPhrase(),       // human-readable status ("Not Found")
                "message", ex.getMessage(),              // the exception’s message
                "path", req.getRequestURI()              // which endpoint caused the error
        );
        return ResponseEntity.status(status).body(body);
    }


    /**
     * Handle invalid client input (e.g., wrong arguments).
     *
     * - Catches IllegalArgumentException.
     * - Logs a warning.
     * - Returns a 400 Bad Request with details in JSON.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex,
                                                                HttpServletRequest req) {
        log.warn("400 Bad Request: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage(),
                "path", req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }



    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {

        log.warn("405 Method Not Allowed: {}", ex.getMessage());
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;

        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage(),
                "supported", ex.getSupportedMethods(), // helpful hint for the client
                "path", req.getRequestURI()
        );

        return ResponseEntity.status(status).body(body);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage(),
                "path", req.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", "Invalid parameter: " + ex.getName(),
                "path", req.getRequestURI()
        ));
    }




    /**
     * Catch-all handler for unexpected errors.
     *
     * - Catches any Exception not handled by other methods.
     * - Logs the error stack trace at ERROR level.
     * - Returns a 500 Internal Server Error with a generic message
     *   (so clients don’t see internal details).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex,
                                                                         HttpServletRequest req) {
        log.error("500 Internal Server Error", ex); // full stack trace logged
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", "An unexpected error occurred. Please try again later.", // safe, generic
                "path", req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
