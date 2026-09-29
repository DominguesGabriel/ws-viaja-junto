package com.viajajunto.api.modules.destination.service;

import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.modules.destination.dto.DestinoCatalogoDTO;
import com.viajajunto.api.modules.destination.dto.VisitedCountryDTO;
import com.viajajunto.api.modules.destination.entity.DestinoCatalogo;
import com.viajajunto.api.modules.destination.repository.DestinoCatalogoRepository;
import com.viajajunto.api.modules.destination.repository.DestinoViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinoCatalogoServiceTest {

    @Mock
    private DestinoCatalogoRepository destinoCatalogoRepository;

    @Mock
    private DestinoViagemRepository destinoViagemRepository;

    @InjectMocks
    private DestinoCatalogoService destinoCatalogoService;

    private DestinoCatalogo sampleCatalogo;

    @BeforeEach
    void setUp() {
        sampleCatalogo = DestinoCatalogo.builder()
                .id(1L)
                .nome("Paris")
                .pais("França")
                .codigoPaisIso("FR")
                .cidade("Paris")
                .estado("Île-de-France")
                .descricao("Cidade Luz")
                .categoria("Romântico")
                .fotoUrl("paris.jpg")
                .avaliacaoMedia(4.9)
                .totalAvaliacoes(150)
                .build();
    }

    @Test
    @DisplayName("Deve buscar destinos paginados com filtros")
    void shouldSearchDestinosWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DestinoCatalogo> page = new PageImpl<>(List.of(sampleCatalogo), pageable, 1);

        when(destinoCatalogoRepository.searchDestinos("Paris", "Romântico", 4.0, pageable)).thenReturn(page);

        Page<DestinoCatalogoDTO> result = destinoCatalogoService.searchDestinos("Paris", "Romântico", 4.0, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Paris", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar destino por ID com sucesso")
    void shouldGetDestinoById() {
        when(destinoCatalogoRepository.findById(1L)).thenReturn(Optional.of(sampleCatalogo));

        DestinoCatalogoDTO result = destinoCatalogoService.getDestinoById(1L);

        assertNotNull(result);
        assertEquals("Paris", result.getNome());
        assertEquals("FR", result.getCodigoPaisIso());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando destino do catálogo não existir")
    void shouldThrowWhenDestinoNotFound() {
        when(destinoCatalogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> destinoCatalogoService.getDestinoById(99L));
    }

    @Test
    @DisplayName("Deve retornar top destinos mais bem avaliados")
    void shouldGetTopRatedDestinos() {
        when(destinoCatalogoRepository.findTop6ByOrderByAvaliacaoMediaDesc()).thenReturn(List.of(sampleCatalogo));

        List<DestinoCatalogoDTO> result = destinoCatalogoService.getTopRatedDestinos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.9, result.get(0).getAvaliacaoMedia());
    }

    @Test
    @DisplayName("Deve retornar códigos de países visitados pelo usuário")
    void shouldGetUserVisitedCountries() {
        when(destinoViagemRepository.findDistinctVisitedCountryCodes(1L)).thenReturn(List.of("FR", "IT", "BR"));

        VisitedCountryDTO result = destinoCatalogoService.getUserVisitedCountries(1L);

        assertNotNull(result);
        assertEquals(3, result.getTotalVisited());
        assertTrue(result.getVisitedIsoCodes().contains("FR"));
    }
}
