package com.viajajunto.api.modules.trip.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.trip.dto.CreateViagemDTO;
import com.viajajunto.api.modules.trip.dto.UpdateViagemDTO;
import com.viajajunto.api.modules.trip.dto.ViagemResponseDTO;
import com.viajajunto.api.modules.trip.service.ViagemService;
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
class ViagemControllerTest {

    @Mock
    private ViagemService viagemService;

    @InjectMocks
    private ViagemController viagemController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").nome("User").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("createViagem - Deve retornar HTTP 201 Created")
    void shouldCreateViagem() {
        CreateViagemDTO dto = CreateViagemDTO.builder().nome("Roma").build();
        ViagemResponseDTO expected = ViagemResponseDTO.builder().id(10L).nome("Roma").build();

        when(viagemService.createViagem(dto, 1L)).thenReturn(expected);

        ResponseEntity<ViagemResponseDTO> response = viagemController.createViagem(dto, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("listUserTrips - Deve retornar HTTP 200 OK com lista de viagens")
    void shouldListUserTrips() {
        ViagemResponseDTO viagem = ViagemResponseDTO.builder().id(10L).nome("Roma").build();
        when(viagemService.listUserTrips(1L)).thenReturn(List.of(viagem));

        ResponseEntity<List<ViagemResponseDTO>> response = viagemController.listUserTrips(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getViagemById - Deve retornar HTTP 200 OK com detalhes da viagem")
    void shouldGetViagemById() {
        ViagemResponseDTO viagem = ViagemResponseDTO.builder().id(10L).nome("Roma").build();
        when(viagemService.getViagemById(10L, 1L)).thenReturn(viagem);

        ResponseEntity<ViagemResponseDTO> response = viagemController.getViagemById(10L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("updateViagem - Deve retornar HTTP 200 OK com viagem atualizada")
    void shouldUpdateViagem() {
        UpdateViagemDTO dto = UpdateViagemDTO.builder().nome("Roma Atualizada").build();
        ViagemResponseDTO viagem = ViagemResponseDTO.builder().id(10L).nome("Roma Atualizada").build();

        when(viagemService.updateViagem(10L, dto, 1L)).thenReturn(viagem);

        ResponseEntity<ViagemResponseDTO> response = viagemController.updateViagem(10L, dto, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roma Atualizada", response.getBody().getNome());
    }

    @Test
    @DisplayName("deleteViagem - Deve retornar HTTP 204 No Content")
    void shouldDeleteViagem() {
        ResponseEntity<Void> response = viagemController.deleteViagem(10L, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(viagemService, times(1)).deleteViagem(10L, 1L);
    }
}
