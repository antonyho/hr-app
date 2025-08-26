package com.hrapp.exception;

import com.hrapp.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleUserNotFound_ReturnsNotFound() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("User not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_NOT_FOUND", response.getBody().getCode());
        assertEquals("User not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleProfileNotFound_ReturnsNotFound() {
        // Arrange
        ProfileNotFoundException exception = new ProfileNotFoundException("Profile not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleProfileNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PROFILE_NOT_FOUND", response.getBody().getCode());
        assertEquals("Profile not found", response.getBody().getMessage());
    }

    @Test
    void handleInvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidCredentials(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void handleInvalidDateRange_ReturnsBadRequest() {
        // Arrange
        InvalidDateRangeException exception = new InvalidDateRangeException("Invalid date range");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidDateRange(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_DATE_RANGE", response.getBody().getCode());
        assertEquals("Invalid date range", response.getBody().getMessage());
    }

    @Test
    void handleAccessDenied_ReturnsForbidden() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDenied(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ACCESS_DENIED", response.getBody().getCode());
        assertEquals("You do not have permission to access this resource", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        // Arrange
        Exception exception = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}