package com.viajajunto.api.modules.trip.dto;

import com.viajajunto.api.modules.auth.dto.UserDTO;
import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembroResponseDTO {
    private Long id;
    private UserDTO usuario;
    private PermissaoMembro permissao;
    private LocalDateTime dataEntrada;
}
