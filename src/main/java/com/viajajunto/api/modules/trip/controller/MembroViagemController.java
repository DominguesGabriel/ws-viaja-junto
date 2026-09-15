package com.viajajunto.api.modules.trip.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.trip.dto.AddMembroDTO;
import com.viajajunto.api.modules.trip.dto.MembroResponseDTO;
import com.viajajunto.api.modules.trip.dto.UpdatePermissaoDTO;
import com.viajajunto.api.modules.trip.service.MembroViagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viagens/{viagemId}/membros")
@RequiredArgsConstructor
public class MembroViagemController {

    private final MembroViagemService membroViagemService;

    @PostMapping("/join")
    public ResponseEntity<MembroResponseDTO> joinViagem(
            @Valid @RequestBody AddMembroDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MembroResponseDTO response = membroViagemService.joinViagemByCode(dto, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MembroResponseDTO>> listMembros(
            @PathVariable Long viagemId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<MembroResponseDTO> membros = membroViagemService.listMembros(viagemId, principal.getId());
        return ResponseEntity.ok(membros);
    }

    @PatchMapping("/{membroId}")
    public ResponseEntity<MembroResponseDTO> updatePermissao(
            @PathVariable Long viagemId,
            @PathVariable Long membroId,
            @Valid @RequestBody UpdatePermissaoDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MembroResponseDTO response = membroViagemService.updatePermissao(viagemId, membroId, dto, principal.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{membroId}")
    public ResponseEntity<Void> removeMembro(
            @PathVariable Long viagemId,
            @PathVariable Long membroId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        membroViagemService.removeMembro(viagemId, membroId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
