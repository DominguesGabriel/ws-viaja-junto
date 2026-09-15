package com.viajajunto.api.modules.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrcamentoDTO {

    @NotNull(message = "O orçamento total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O orçamento não pode ser negativo")
    private BigDecimal orcamentoTotal;
}
