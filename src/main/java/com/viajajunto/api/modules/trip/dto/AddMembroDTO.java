package com.viajajunto.api.modules.trip.dto;

import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMembroDTO {

    @NotBlank(message = "O código de convite é obrigatório")
    private String codigoConvite;

    private PermissaoMembro permissao;
}
