package com.viajajunto.api.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test-path");
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException com HTTP 404")
    void shouldHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleResourceNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso não encontrado", response.getBody().getMessage());
        assertEquals("/api/test-path", response.getBody().getPath());
    }

    @Test
    @DisplayName("Deve tratar BusinessRuleException com HTTP 400")
    void shouldHandleBusinessRule() {
        BusinessRuleException ex = new BusinessRuleException("Regra de negócio violada");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBusinessRule(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Regra de negócio violada", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve tratar UnauthorizedAccessException / AccessDeniedException com HTTP 403")
    void shouldHandleUnauthorized() {
        UnauthorizedAccessException ex = new UnauthorizedAccessException("Acesso proibido");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUnauthorized(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Acesso proibido", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve tratar BadCredentialsException com HTTP 401")
    void shouldHandleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Senha incorreta");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBadCredentials(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Credenciais inválidas: e-mail ou senha incorretos.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com HTTP 400 e mapa de campos")
    void shouldHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "email", "E-mail inválido");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        doReturn(List.of(fieldError)).when(bindingResult).getAllErrors();

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("E-mail inválido", response.getBody().getValidationErrors().get("email"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica com HTTP 500")
    void shouldHandleGenericException() {
        Exception ex = new NullPointerException("Erro inesperado");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ocorreu um erro interno inesperado no servidor.", response.getBody().getMessage());
    }
}
