package com.viajajunto.api.modules.destination.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinoCatalogoDTO {
    private Long id;
    private String nome;
    private String pais;
    private String codigoPaisIso;
    private String cidade;
    private String estado;
    private String descricao;
    private String categoria;
    private String fotoUrl;
    private Double avaliacaoMedia;
    private Integer totalAvaliacoes;
}
