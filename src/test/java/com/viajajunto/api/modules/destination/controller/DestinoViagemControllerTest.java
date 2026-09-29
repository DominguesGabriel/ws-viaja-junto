package com.viajajunto.api.modules.destination.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.destination.dto.CreateDestinoViagemDTO;
import com.viajajunto.api.modules.destination.dto.DestinoViagemResponseDTO;
import com.viajajunto.api.modules.destination.service.DestinoViagemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinoViagemControllerTest {

    @Mock
    private DestinoViagemService destinoViagemService;

    @InjectMocks
    private DestinoViagemController destinoViagemController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("addDestino - Deve retornar HTTP 201 Created")
    void shouldAddDestino() {
        CreateDestinoViagemDTO dto = CreateDestinoViagemDTO.builder().nome("Roma").build();
        DestinoViagemResponseDTO expected = DestinoViagemResponseDTO.builder().id(10L).nome("Roma").build();

        when(destinoViagemService.addDestinoToViagem(1L, dto, 1L)).thenReturn(expected);

        ResponseEntity<DestinoViagemResponseDTO> response = destinoViagemController.addDestino(1L, dto, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("listDestinos - Deve retornar HTTP 200 OK")
    void shouldListDestinos() {
        DestinoViagemResponseDTO destino = DestinoViagemResponseDTO.builder().id(10L).nome("Roma").build();
        when(destinoViagemService.listDestinosByViagem(1L, 1L)).thenReturn(List.of(destino));

        ResponseEntity<List<DestinoViagemResponseDTO>> response = destinoViagemController.listDestinos(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("removeDestino - Deve retornar HTTP 204 No Content")
    void shouldRemoveDestino() {
        ResponseEntity<Void> response = destinoViagemController.removeDestino(1L, 10L, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(destinoViagemService, times(1)).removeDestino(1L, 10L, 1L);
    }
}
