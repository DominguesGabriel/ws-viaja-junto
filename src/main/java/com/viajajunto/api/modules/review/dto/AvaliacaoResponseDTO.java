package com.viajajunto.api.modules.review.dto;

import com.viajajunto.api.modules.auth.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponseDTO {
    private Long id;
    private UserDTO usuario;
    private Long destinoCatalogoId;
    private Long catalogoAtividadeId;
    private Integer nota;
    private String comentario;
    private LocalDateTime dataCriacao;
}
