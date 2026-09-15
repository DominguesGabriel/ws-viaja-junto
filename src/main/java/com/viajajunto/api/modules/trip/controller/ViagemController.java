package com.viajajunto.api.modules.trip.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.trip.dto.CreateViagemDTO;
import com.viajajunto.api.modules.trip.dto.UpdateViagemDTO;
import com.viajajunto.api.modules.trip.dto.ViagemResponseDTO;
import com.viajajunto.api.modules.trip.service.ViagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viagens")
@RequiredArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @PostMapping
    public ResponseEntity<ViagemResponseDTO> createViagem(
            @Valid @RequestBody CreateViagemDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ViagemResponseDTO response = viagemService.createViagem(dto, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ViagemResponseDTO>> listUserTrips(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ViagemResponseDTO> trips = viagemService.listUserTrips(principal.getId());
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> getViagemById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ViagemResponseDTO response = viagemService.getViagemById(id, principal.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> updateViagem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateViagemDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ViagemResponseDTO response = viagemService.updateViagem(id, dto, principal.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViagem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        viagemService.deleteViagem(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
