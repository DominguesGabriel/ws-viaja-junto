package com.viajajunto.api.modules.activity.dto;

import com.viajajunto.api.modules.activity.entity.StatusAtividade;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAtividadeDTO {

    private Long catalogoAtividadeId;

    @NotBlank(message = "O nome da atividade é obrigatório")
    private String nome;

    private String tipo;

    private String local;

    private String fotoUrl;

    private String descricao;

    private LocalDateTime dataHorario;

    private Integer duracaoMinutos;

    private BigDecimal custoPrevisto;

    private StatusAtividade status;
}
