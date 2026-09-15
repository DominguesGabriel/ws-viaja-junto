package com.viajajunto.api.modules.trip.dto;

import com.viajajunto.api.modules.auth.dto.UserDTO;
import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import com.viajajunto.api.modules.trip.entity.StatusViagem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViagemResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusViagem status;
    private String codigoConvite;
    private UserDTO criador;
    private PermissaoMembro permissaoUsuarioAutenticado;
    private List<MembroResponseDTO> membros;
    private BigDecimal orcamentoTotal;
    private BigDecimal totalPlanejado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
