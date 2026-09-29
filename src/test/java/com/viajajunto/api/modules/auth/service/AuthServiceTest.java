package com.viajajunto.api.modules.auth.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.security.JwtService;
import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.dto.AuthResponseDTO;
import com.viajajunto.api.modules.auth.dto.ForgotPasswordDTO;
import com.viajajunto.api.modules.auth.dto.LoginRequestDTO;
import com.viajajunto.api.modules.auth.dto.RegisterDTO;
import com.viajajunto.api.modules.auth.dto.UserDTO;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .nome("Gabriel Domingues")
                .email("gabriel@example.com")
                .senha("encoded_pwd")
                .avatarUrl("https://avatar.url")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso e retornar token JWT")
    void shouldRegisterUserSuccessfully() {
        RegisterDTO dto = RegisterDTO.builder()
                .nome("Gabriel Domingues")
                .email("gabriel@example.com")
                .senha("password123")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("encoded_pwd");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any(UserPrincipal.class), eq(1L), eq("Gabriel Domingues"))).thenReturn("mocked_jwt_token");

        AuthResponseDTO response = authService.register(dto);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        assertEquals("gabriel@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao tentar cadastrar e-mail duplicado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterDTO dto = RegisterDTO.builder()
                .nome("Gabriel Domingues")
                .email("gabriel@example.com")
                .senha("password123")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> authService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve autenticar usuário e retornar token JWT no login")
    void shouldLoginSuccessfully() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("gabriel@example.com")
                .senha("password123")
                .build();

        Authentication authMock = mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(sampleUser);
        when(authMock.getPrincipal()).thenReturn(principal);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(userRepository.findByEmail("gabriel@example.com")).thenReturn(Optional.of(sampleUser));
        when(jwtService.generateToken(principal, 1L, "Gabriel Domingues")).thenReturn("mocked_jwt_token");

        AuthResponseDTO response = authService.login(dto);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        assertEquals("Gabriel Domingues", response.getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não for encontrado pós autenticação")
    void shouldThrowNotFoundOnLoginWhenUserNotInDb() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("gabriel@example.com")
                .senha("password123")
                .build();

        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(new UserPrincipal(sampleUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(userRepository.findByEmail("gabriel@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    @DisplayName("Deve retornar perfil do usuário por e-mail com sucesso")
    void shouldGetProfileSuccessfully() {
        when(userRepository.findByEmail("gabriel@example.com")).thenReturn(Optional.of(sampleUser));

        UserDTO profile = authService.getProfile("gabriel@example.com");

        assertNotNull(profile);
        assertEquals(1L, profile.getId());
        assertEquals("Gabriel Domingues", profile.getNome());
        assertEquals("gabriel@example.com", profile.getEmail());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar perfil de e-mail inexistente")
    void shouldThrowNotFoundWhenProfileEmailDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.getProfile("unknown@example.com"));
    }

    @Test
    @DisplayName("Deve executar forgotPassword sem lançar erros")
    void shouldExecuteForgotPassword() {
        ForgotPasswordDTO dto = ForgotPasswordDTO.builder().email("gabriel@example.com").build();
        when(userRepository.findByEmail("gabriel@example.com")).thenReturn(Optional.of(sampleUser));

        assertDoesNotThrow(() -> authService.forgotPassword(dto));
    }
}
