package com.viajajunto.api.modules.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoAtividadeDTO {
    private Long id;
    private String nome;
    private String tipo;
    private String localizacao;
    private String cidade;
    private String pais;
    private String descricao;
    private String fotoUrl;
    private BigDecimal precoMedio;
    private Double avaliacaoMedia;
    private Integer totalAvaliacoes;
}
