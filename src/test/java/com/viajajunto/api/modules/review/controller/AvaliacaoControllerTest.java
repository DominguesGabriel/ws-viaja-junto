package com.viajajunto.api.modules.review.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.review.dto.AvaliacaoResponseDTO;
import com.viajajunto.api.modules.review.dto.CreateAvaliacaoDTO;
import com.viajajunto.api.modules.review.service.AvaliacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("createAvaliacao - Deve retornar HTTP 201 Created")
    void shouldCreateAvaliacao() {
        CreateAvaliacaoDTO dto = CreateAvaliacaoDTO.builder().nota(5).build();
        AvaliacaoResponseDTO expected = AvaliacaoResponseDTO.builder().id(10L).nota(5).build();

        when(avaliacaoService.createAvaliacao(dto, 1L)).thenReturn(expected);

        ResponseEntity<AvaliacaoResponseDTO> response = avaliacaoController.createAvaliacao(dto, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("listAvaliacoesByDestino - Deve retornar HTTP 200 OK")
    void shouldListAvaliacoesByDestino() {
        Pageable pageable = PageRequest.of(0, 10);
        AvaliacaoResponseDTO av = AvaliacaoResponseDTO.builder().id(10L).nota(5).build();
        Page<AvaliacaoResponseDTO> page = new PageImpl<>(List.of(av), pageable, 1);

        when(avaliacaoService.listAvaliacoesByDestino(1L, pageable)).thenReturn(page);

        ResponseEntity<Page<AvaliacaoResponseDTO>> response = avaliacaoController.listAvaliacoesByDestino(1L, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    @DisplayName("listAvaliacoesByAtividade - Deve retornar HTTP 200 OK")
    void shouldListAvaliacoesByAtividade() {
        Pageable pageable = PageRequest.of(0, 10);
        AvaliacaoResponseDTO av = AvaliacaoResponseDTO.builder().id(10L).nota(5).build();
        Page<AvaliacaoResponseDTO> page = new PageImpl<>(List.of(av), pageable, 1);

        when(avaliacaoService.listAvaliacoesByAtividade(2L, pageable)).thenReturn(page);

        ResponseEntity<Page<AvaliacaoResponseDTO>> response = avaliacaoController.listAvaliacoesByAtividade(2L, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }
}
