package com.viajajunto.api.modules.destination.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDestinoViagemDTO {

    private Long destinoCatalogoId;

    @NotBlank(message = "O nome do destino é obrigatório")
    private String nome;

    private String pais;

    private String codigoPaisIso;

    private String localizacao;

    private String fotoUrl;

    private String descricao;

    private String categoria;

    private LocalDate dataChegada;

    private LocalDate dataSaida;

    private Integer ordemVisita;
}
