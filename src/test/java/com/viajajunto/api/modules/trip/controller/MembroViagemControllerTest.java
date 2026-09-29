package com.viajajunto.api.modules.trip.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.trip.dto.AddMembroDTO;
import com.viajajunto.api.modules.trip.dto.MembroResponseDTO;
import com.viajajunto.api.modules.trip.dto.UpdatePermissaoDTO;
import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import com.viajajunto.api.modules.trip.service.MembroViagemService;
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
class MembroViagemControllerTest {

    @Mock
    private MembroViagemService membroViagemService;

    @InjectMocks
    private MembroViagemController membroViagemController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("joinViagem - Deve retornar HTTP 201 Created")
    void shouldJoinViagem() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("CODE123").build();
        MembroResponseDTO expected = MembroResponseDTO.builder().id(5L).build();

        when(membroViagemService.joinViagemByCode(dto, 1L)).thenReturn(expected);

        ResponseEntity<MembroResponseDTO> response = membroViagemController.joinViagem(dto, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, response.getBody().getId());
    }

    @Test
    @DisplayName("listMembros - Deve retornar HTTP 200 OK")
    void shouldListMembros() {
        MembroResponseDTO membro = MembroResponseDTO.builder().id(5L).build();
        when(membroViagemService.listMembros(10L, 1L)).thenReturn(List.of(membro));

        ResponseEntity<List<MembroResponseDTO>> response = membroViagemController.listMembros(10L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("updatePermissao - Deve retornar HTTP 200 OK")
    void shouldUpdatePermissao() {
        UpdatePermissaoDTO dto = UpdatePermissaoDTO.builder().permissao(PermissaoMembro.EDITOR).build();
        MembroResponseDTO membro = MembroResponseDTO.builder().id(5L).permissao(PermissaoMembro.EDITOR).build();

        when(membroViagemService.updatePermissao(10L, 5L, dto, 1L)).thenReturn(membro);

        ResponseEntity<MembroResponseDTO> response = membroViagemController.updatePermissao(10L, 5L, dto, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PermissaoMembro.EDITOR, response.getBody().getPermissao());
    }

    @Test
    @DisplayName("removeMembro - Deve retornar HTTP 204 No Content")
    void shouldRemoveMembro() {
        ResponseEntity<Void> response = membroViagemController.removeMembro(10L, 5L, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(membroViagemService, times(1)).removeMembro(10L, 5L, 1L);
    }
}
