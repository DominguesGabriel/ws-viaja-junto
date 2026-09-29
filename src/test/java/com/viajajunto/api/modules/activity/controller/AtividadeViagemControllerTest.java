package com.viajajunto.api.modules.activity.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.activity.dto.AtividadeResponseDTO;
import com.viajajunto.api.modules.activity.dto.CreateAtividadeDTO;
import com.viajajunto.api.modules.activity.service.AtividadeViagemService;
import com.viajajunto.api.modules.auth.entity.User;
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
class AtividadeViagemControllerTest {

    @Mock
    private AtividadeViagemService atividadeViagemService;

    @InjectMocks
    private AtividadeViagemController atividadeViagemController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("addAtividade - Deve retornar HTTP 201 Created")
    void shouldAddAtividade() {
        CreateAtividadeDTO dto = CreateAtividadeDTO.builder().nome("Passeio").build();
        AtividadeResponseDTO expected = AtividadeResponseDTO.builder().id(10L).nome("Passeio").build();

        when(atividadeViagemService.addAtividade(1L, 2L, dto, 1L)).thenReturn(expected);

        ResponseEntity<AtividadeResponseDTO> response = atividadeViagemController.addAtividade(1L, 2L, dto, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("listAtividades - Deve retornar HTTP 200 OK")
    void shouldListAtividades() {
        AtividadeResponseDTO ativ = AtividadeResponseDTO.builder().id(10L).nome("Passeio").build();
        when(atividadeViagemService.listAtividadesByDestino(1L, 2L, 1L)).thenReturn(List.of(ativ));

        ResponseEntity<List<AtividadeResponseDTO>> response = atividadeViagemController.listAtividades(1L, 2L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("removeAtividade - Deve retornar HTTP 204 No Content")
    void shouldRemoveAtividade() {
        ResponseEntity<Void> response = atividadeViagemController.removeAtividade(1L, 2L, 10L, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(atividadeViagemService, times(1)).removeAtividade(1L, 2L, 10L, 1L);
    }
}
