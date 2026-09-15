package com.viajajunto.api.modules.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoResponseDTO {
    private Long viagemId;
    private BigDecimal orcamentoTotal;
    private BigDecimal totalPlanejado;
    private BigDecimal saldoDisponivel;
    private Double percentualConsumidoTotal;
    private List<CategoriaGastoDTO> gastosPorCategoria;
}
