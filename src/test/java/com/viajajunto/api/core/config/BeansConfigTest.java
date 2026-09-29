package com.viajajunto.api.core.config;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeansConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private BeansConfig beansConfig;

    @Test
    @DisplayName("userDetailsService - Deve retornar UserDetails quando usuário existe")
    void shouldReturnUserDetailsWhenUserExists() {
        User user = User.builder().id(1L).email("user@test.com").nome("User").senha("123").build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetailsService service = beansConfig.userDetailsService();
        UserDetails details = service.loadUserByUsername("user@test.com");

        assertNotNull(details);
        assertEquals("user@test.com", details.getUsername());
    }

    @Test
    @DisplayName("userDetailsService - Deve lançar UsernameNotFoundException quando usuário não existe")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        UserDetailsService service = beansConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown@test.com"));
    }

    @Test
    @DisplayName("Deve instanciar PasswordEncoder, AuthenticationProvider, AuthenticationManager e RestTemplate")
    void shouldCreateOtherBeans() throws Exception {
        PasswordEncoder encoder = beansConfig.passwordEncoder();
        assertNotNull(encoder);

        AuthenticationProvider provider = beansConfig.authenticationProvider();
        assertNotNull(provider);

        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager manager = beansConfig.authenticationManager(authenticationConfiguration);
        assertEquals(mockManager, manager);

        RestTemplate restTemplate = beansConfig.restTemplate();
        assertNotNull(restTemplate);
    }
}
