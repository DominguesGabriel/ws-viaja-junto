package com.viajajunto.api.modules.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaGastoDTO {
    private String categoria;
    private BigDecimal valorTotal;
    private Double percentualConsumido;
}
