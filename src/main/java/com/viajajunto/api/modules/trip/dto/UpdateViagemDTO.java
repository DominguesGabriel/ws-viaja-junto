package com.viajajunto.api.modules.trip.dto;

import com.viajajunto.api.modules.trip.entity.StatusViagem;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateViagemDTO {

    @Size(min = 2, max = 150, message = "O nome da viagem deve ter entre 2 e 150 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição pode ter no máximo 1000 caracteres")
    private String descricao;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private StatusViagem status;
}
