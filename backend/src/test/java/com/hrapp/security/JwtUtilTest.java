package com.hrapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UUID testUserId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", "myTestSecretKeyThatIsLongEnoughForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour

        testUserId = UUID.randomUUID();
        testEmail = "test@example.com";
    }

    @Test
    void generateToken_ValidInput_ReturnsToken() {
        // Act
        String token = jwtUtil.generateToken(testUserId, testEmail);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        // Arrange
        String token = jwtUtil.generateToken(testUserId, testEmail);

        // Act
        UUID extractedUserId = jwtUtil.getUserIdFromToken(token);

        // Assert
        assertEquals(testUserId, extractedUserId);
    }

    @Test
    void getEmailFromToken_ValidToken_ReturnsEmail() {
        // Arrange
        String token = jwtUtil.generateToken(testUserId, testEmail);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtUtil.generateToken(testUserId, testEmail);

        // Act
        boolean isValid = jwtUtil.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(testUserId, testEmail);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange - Create JWT with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L); // Already expired
        String expiredToken = jwtUtil.generateToken(testUserId, testEmail);
        
        // Reset expiration to normal value
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void getUserIdFromToken_InvalidToken_ThrowsException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtil.getUserIdFromToken(invalidToken));
    }

    @Test
    void getEmailFromToken_InvalidToken_ThrowsException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtil.getEmailFromToken(invalidToken));
    }
}