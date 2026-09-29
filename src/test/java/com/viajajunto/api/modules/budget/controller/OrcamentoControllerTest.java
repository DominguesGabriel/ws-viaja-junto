package com.viajajunto.api.modules.budget.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.budget.dto.OrcamentoResponseDTO;
import com.viajajunto.api.modules.budget.dto.UpdateOrcamentoDTO;
import com.viajajunto.api.modules.budget.service.OrcamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoControllerTest {

    @Mock
    private OrcamentoService orcamentoService;

    @InjectMocks
    private OrcamentoController orcamentoController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("getResumoOrcamento - Deve retornar HTTP 200 OK")
    void shouldGetResumoOrcamento() {
        OrcamentoResponseDTO resumo = OrcamentoResponseDTO.builder().viagemId(10L).orcamentoTotal(BigDecimal.valueOf(1000)).build();
        when(orcamentoService.getResumoOrcamento(10L, 1L)).thenReturn(resumo);

        ResponseEntity<OrcamentoResponseDTO> response = orcamentoController.getResumoOrcamento(10L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(1000), response.getBody().getOrcamentoTotal());
    }

    @Test
    @DisplayName("updateOrcamento - Deve retornar HTTP 200 OK")
    void shouldUpdateOrcamento() {
        UpdateOrcamentoDTO dto = UpdateOrcamentoDTO.builder().orcamentoTotal(BigDecimal.valueOf(2000)).build();
        OrcamentoResponseDTO resumo = OrcamentoResponseDTO.builder().viagemId(10L).orcamentoTotal(BigDecimal.valueOf(2000)).build();

        when(orcamentoService.updateOrcamentoTotal(10L, dto, 1L)).thenReturn(resumo);

        ResponseEntity<OrcamentoResponseDTO> response = orcamentoController.updateOrcamento(10L, dto, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(2000), response.getBody().getOrcamentoTotal());
    }
}
