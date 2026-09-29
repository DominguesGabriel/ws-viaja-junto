package com.viajajunto.api.modules.budget.service;

import com.viajajunto.api.core.security.TripSecurityService;
import com.viajajunto.api.modules.activity.entity.AtividadeViagem;
import com.viajajunto.api.modules.activity.repository.AtividadeViagemRepository;
import com.viajajunto.api.modules.budget.dto.OrcamentoResponseDTO;
import com.viajajunto.api.modules.budget.dto.UpdateOrcamentoDTO;
import com.viajajunto.api.modules.budget.entity.Orcamento;
import com.viajajunto.api.modules.budget.repository.OrcamentoRepository;
import com.viajajunto.api.modules.trip.entity.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private AtividadeViagemRepository atividadeViagemRepository;

    @Mock
    private TripSecurityService tripSecurityService;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private Orcamento sampleOrcamento;
    private List<AtividadeViagem> sampleAtividades;

    @BeforeEach
    void setUp() {
        sampleOrcamento = Orcamento.builder()
                .id(1L)
                .orcamentoTotal(BigDecimal.valueOf(1000.00))
                .build();

        sampleAtividades = List.of(
                AtividadeViagem.builder()
                        .id(1L)
                        .nome("Jantar Típico")
                        .tipo("Gastronomia")
                        .custoPrevisto(BigDecimal.valueOf(200.00))
                        .build(),
                AtividadeViagem.builder()
                        .id(2L)
                        .nome("Museu do Vaticano")
                        .tipo("Passeio")
                        .custoPrevisto(BigDecimal.valueOf(300.00))
                        .build()
        );
    }

    @Test
    @DisplayName("Deve calcular resumo de orçamento, saldo disponível e percentuais por categoria corretamente")
    void shouldCalculateBudgetSummaryCorrectly() {
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.of(sampleOrcamento));
        when(atividadeViagemRepository.findAllByViagemId(10L)).thenReturn(sampleAtividades);

        OrcamentoResponseDTO resumo = orcamentoService.getResumoOrcamento(10L, 1L);

        assertNotNull(resumo);
        assertEquals(BigDecimal.valueOf(1000.00), resumo.getOrcamentoTotal());
        assertEquals(BigDecimal.valueOf(500.00), resumo.getTotalPlanejado());
        assertEquals(BigDecimal.valueOf(500.00), resumo.getSaldoDisponivel());
        assertEquals(50.0, resumo.getPercentualConsumidoTotal());
        assertEquals(2, resumo.getGastosPorCategoria().size());

        verify(tripSecurityService, times(1)).validateUserCanViewTrip(10L, 1L);
    }

    @Test
    @DisplayName("Deve calcular resumo quando orçamento não for encontrado e não houver atividades")
    void shouldCalculateBudgetWhenEmpty() {
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.empty());
        when(atividadeViagemRepository.findAllByViagemId(10L)).thenReturn(List.of());

        OrcamentoResponseDTO resumo = orcamentoService.getResumoOrcamento(10L, 1L);

        assertNotNull(resumo);
        assertEquals(BigDecimal.ZERO, resumo.getOrcamentoTotal());
        assertEquals(BigDecimal.ZERO, resumo.getTotalPlanejado());
        assertEquals(BigDecimal.ZERO, resumo.getSaldoDisponivel());
        assertEquals(0.0, resumo.getPercentualConsumidoTotal());
        assertTrue(resumo.getGastosPorCategoria().isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar o orçamento total com sucesso")
    void shouldUpdateOrcamentoTotalSuccessfully() {
        Viagem viagem = Viagem.builder().id(10L).build();
        UpdateOrcamentoDTO dto = UpdateOrcamentoDTO.builder().orcamentoTotal(BigDecimal.valueOf(2500.00)).build();

        when(tripSecurityService.validateUserCanEditTrip(10L, 1L)).thenReturn(viagem);
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.of(sampleOrcamento));
        when(atividadeViagemRepository.findAllByViagemId(10L)).thenReturn(sampleAtividades);

        OrcamentoResponseDTO response = orcamentoService.updateOrcamentoTotal(10L, dto, 1L);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(2500.00), sampleOrcamento.getOrcamentoTotal());
        verify(orcamentoRepository, times(1)).save(sampleOrcamento);
    }
}
