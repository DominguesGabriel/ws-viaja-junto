package com.viajajunto.api.modules.auth.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.dto.AuthResponseDTO;
import com.viajajunto.api.modules.auth.dto.ForgotPasswordDTO;
import com.viajajunto.api.modules.auth.dto.LoginRequestDTO;
import com.viajajunto.api.modules.auth.dto.RegisterDTO;
import com.viajajunto.api.modules.auth.dto.UserDTO;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register - Deve retornar HTTP 201 Created com dados de autenticação")
    void shouldRegisterUser() {
        RegisterDTO dto = RegisterDTO.builder().email("gabriel@test.com").build();
        AuthResponseDTO expected = AuthResponseDTO.builder().token("jwt_token").email("gabriel@test.com").build();

        when(authService.register(dto)).thenReturn(expected);

        ResponseEntity<AuthResponseDTO> response = authController.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt_token", response.getBody().getToken());
    }

    @Test
    @DisplayName("login - Deve retornar HTTP 200 OK com token")
    void shouldLoginUser() {
        LoginRequestDTO dto = LoginRequestDTO.builder().email("gabriel@test.com").senha("123").build();
        AuthResponseDTO expected = AuthResponseDTO.builder().token("jwt_token").build();

        when(authService.login(dto)).thenReturn(expected);

        ResponseEntity<AuthResponseDTO> response = authController.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt_token", response.getBody().getToken());
    }

    @Test
    @DisplayName("getProfile - Deve retornar HTTP 200 OK com dados do perfil do usuário logado")
    void shouldGetProfile() {
        User user = User.builder().id(1L).email("gabriel@test.com").nome("Gabriel").build();
        UserPrincipal principal = new UserPrincipal(user);
        UserDTO userDTO = UserDTO.builder().id(1L).email("gabriel@test.com").nome("Gabriel").build();

        when(authService.getProfile("gabriel@test.com")).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authController.getProfile(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gabriel", response.getBody().getNome());
    }

    @Test
    @DisplayName("forgotPassword - Deve retornar HTTP 204 No Content")
    void shouldForgotPassword() {
        ForgotPasswordDTO dto = ForgotPasswordDTO.builder().email("gabriel@test.com").build();

        ResponseEntity<Void> response = authController.forgotPassword(dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authService, times(1)).forgotPassword(dto);
    }
}
