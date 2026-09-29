package com.viajajunto.api.core.security;

import com.viajajunto.api.modules.auth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    @Test
    @DisplayName("Deve inicializar UserPrincipal a partir da entidade User e preencher métodos de UserDetails")
    void shouldInitializeUserPrincipalCorrectly() {
        User user = User.builder()
                .id(10L)
                .nome("Gabriel")
                .email("gabriel@test.com")
                .senha("pass123")
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        assertEquals(10L, principal.getId());
        assertEquals("Gabriel", principal.getNome());
        assertEquals("gabriel@test.com", principal.getEmail());
        assertEquals("gabriel@test.com", principal.getUsername());
        assertEquals("pass123", principal.getPassword());
        assertEquals("pass123", principal.getSenha());

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());

        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
        assertTrue(principal.isEnabled());
    }
}
