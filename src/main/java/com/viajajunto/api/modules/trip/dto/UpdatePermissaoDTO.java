package com.viajajunto.api.modules.trip.dto;

import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissaoDTO {

    @NotNull(message = "A permissão é obrigatória")
    private PermissaoMembro permissao;
}
