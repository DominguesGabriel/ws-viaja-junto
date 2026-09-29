package com.viajajunto.api.core.security;

import com.viajajunto.api.modules.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hora

        User user = User.builder()
                .id(123L)
                .nome("Test User")
                .email("test@example.com")
                .senha("hash_pass")
                .build();
        userPrincipal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("Deve gerar token, extrair username, userId e validar token com sucesso")
    void shouldGenerateAndValidateTokenSuccessfully() {
        String token = jwtService.generateToken(userPrincipal, 123L, "Test User");

        assertNotNull(token);
        assertFalse(token.isBlank());

        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);

        Long userId = jwtService.extractUserId(token);
        assertEquals(123L, userId);

        boolean isValid = jwtService.isTokenValid(token, userPrincipal);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve invalidar token se pertencer a outro usuário")
    void shouldInvalidateTokenForDifferentUser() {
        String token = jwtService.generateToken(userPrincipal, 123L, "Test User");

        User otherUser = User.builder()
                .id(456L)
                .nome("Other User")
                .email("other@example.com")
                .senha("hash_pass")
                .build();
        UserPrincipal otherPrincipal = new UserPrincipal(otherUser);

        boolean isValid = jwtService.isTokenValid(token, otherPrincipal);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve gerar token com extra claims personalizadas")
    void shouldGenerateTokenWithCustomClaims() {
        String token = jwtService.generateToken(Map.of("role", "ADMIN"), userPrincipal);

        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
        assertNull(jwtService.extractUserId(token));
    }
}
