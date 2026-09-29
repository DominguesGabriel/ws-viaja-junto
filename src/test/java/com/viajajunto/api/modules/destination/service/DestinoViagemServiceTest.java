package com.viajajunto.api.modules.destination.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.security.TripSecurityService;
import com.viajajunto.api.modules.destination.dto.CreateDestinoViagemDTO;
import com.viajajunto.api.modules.destination.dto.DestinoViagemResponseDTO;
import com.viajajunto.api.modules.destination.entity.DestinoCatalogo;
import com.viajajunto.api.modules.destination.entity.DestinoViagem;
import com.viajajunto.api.modules.destination.repository.DestinoCatalogoRepository;
import com.viajajunto.api.modules.destination.repository.DestinoViagemRepository;
import com.viajajunto.api.modules.trip.entity.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinoViagemServiceTest {

    @Mock
    private DestinoViagemRepository destinoViagemRepository;

    @Mock
    private DestinoCatalogoRepository destinoCatalogoRepository;

    @Mock
    private TripSecurityService tripSecurityService;

    @InjectMocks
    private DestinoViagemService destinoViagemService;

    private Viagem sampleViagem;
    private DestinoCatalogo sampleCatalogo;
    private DestinoViagem sampleDestino;

    @BeforeEach
    void setUp() {
        sampleViagem = Viagem.builder().id(10L).nome("Viagem Europa").build();
        sampleCatalogo = DestinoCatalogo.builder()
                .id(5L)
                .nome("Coliseu")
                .pais("Itália")
                .codigoPaisIso("IT")
                .cidade("Roma")
                .fotoUrl("coliseu.jpg")
                .categoria("Histórico")
                .build();

        sampleDestino = DestinoViagem.builder()
                .id(100L)
                .viagem(sampleViagem)
                .destinoCatalogo(sampleCatalogo)
                .nome("Coliseu")
                .pais("Itália")
                .codigoPaisIso("IT")
                .localizacao("Roma")
                .fotoUrl("coliseu.jpg")
                .ordemVisita(1)
                .dataChegada(LocalDate.of(2026, 10, 1))
                .dataSaida(LocalDate.of(2026, 10, 5))
                .build();
    }

    @Test
    @DisplayName("Deve adicionar destino à viagem vinculado a um catálogo com sucesso")
    void shouldAddDestinoWithCatalogoSuccessfully() {
        CreateDestinoViagemDTO dto = CreateDestinoViagemDTO.builder()
                .destinoCatalogoId(5L)
                .nome("Coliseu de Roma")
                .dataChegada(LocalDate.of(2026, 10, 1))
                .dataSaida(LocalDate.of(2026, 10, 5))
                .build();

        when(tripSecurityService.validateUserCanEditTrip(10L, 1L)).thenReturn(sampleViagem);
        when(destinoCatalogoRepository.findById(5L)).thenReturn(Optional.of(sampleCatalogo));
        when(destinoViagemRepository.findAllByViagemIdOrderByOrdemVisitaAsc(10L)).thenReturn(List.of());
        when(destinoViagemRepository.save(any(DestinoViagem.class))).thenReturn(sampleDestino);

        DestinoViagemResponseDTO response = destinoViagemService.addDestinoToViagem(10L, dto, 1L);

        assertNotNull(response);
        assertEquals("Coliseu", response.getNome());
        assertEquals(10L, response.getViagemId());
        assertEquals(5L, response.getDestinoCatalogoId());
        verify(destinoViagemRepository, times(1)).save(any(DestinoViagem.class));
    }

    @Test
    @DisplayName("Deve adicionar destino personalizado sem catálogo")
    void shouldAddCustomDestinoWithoutCatalogo() {
        CreateDestinoViagemDTO dto = CreateDestinoViagemDTO.builder()
                .nome("Praia Secreta")
                .pais("Brasil")
                .codigoPaisIso("BR")
                .localizacao("Ubatuba")
                .ordemVisita(2)
                .build();

        DestinoViagem customDestino = DestinoViagem.builder()
                .id(101L)
                .viagem(sampleViagem)
                .nome("Praia Secreta")
                .pais("Brasil")
                .codigoPaisIso("BR")
                .ordemVisita(2)
                .build();

        when(tripSecurityService.validateUserCanEditTrip(10L, 1L)).thenReturn(sampleViagem);
        when(destinoViagemRepository.findAllByViagemIdOrderByOrdemVisitaAsc(10L)).thenReturn(List.of(sampleDestino));
        when(destinoViagemRepository.save(any(DestinoViagem.class))).thenReturn(customDestino);

        DestinoViagemResponseDTO response = destinoViagemService.addDestinoToViagem(10L, dto, 1L);

        assertNotNull(response);
        assertEquals("Praia Secreta", response.getNome());
        assertNull(response.getDestinoCatalogoId());
    }

    @Test
    @DisplayName("Deve listar destinos da viagem ordenados")
    void shouldListDestinosByViagem() {
        when(destinoViagemRepository.findAllByViagemIdOrderByOrdemVisitaAsc(10L)).thenReturn(List.of(sampleDestino));

        List<DestinoViagemResponseDTO> list = destinoViagemService.listDestinosByViagem(10L, 1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(tripSecurityService, times(1)).validateUserCanViewTrip(10L, 1L);
    }

    @Test
    @DisplayName("Deve remover destino da viagem com sucesso")
    void shouldRemoveDestinoSuccessfully() {
        when(destinoViagemRepository.findById(100L)).thenReturn(Optional.of(sampleDestino));

        destinoViagemService.removeDestino(10L, 100L, 1L);

        verify(tripSecurityService, times(1)).validateUserCanEditTrip(10L, 1L);
        verify(destinoViagemRepository, times(1)).delete(sampleDestino);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar remover destino inexistente")
    void shouldThrowWhenDestinoNotFoundOnRemove() {
        when(destinoViagemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> destinoViagemService.removeDestino(10L, 999L, 1L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao remover destino pertencente a outra viagem")
    void shouldThrowWhenDestinoBelongsToOtherTrip() {
        Viagem otherTrip = Viagem.builder().id(999L).build();
        DestinoViagem foreignDestino = DestinoViagem.builder().id(100L).viagem(otherTrip).build();

        when(destinoViagemRepository.findById(100L)).thenReturn(Optional.of(foreignDestino));

        assertThrows(BusinessRuleException.class, () -> destinoViagemService.removeDestino(10L, 100L, 1L));
    }
}
