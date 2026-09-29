package com.viajajunto.api.modules.activity.service;

import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.modules.activity.dto.CatalogoAtividadeDTO;
import com.viajajunto.api.modules.activity.entity.CatalogoAtividade;
import com.viajajunto.api.modules.activity.repository.CatalogoAtividadeRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoAtividadeServiceTest {

    @Mock
    private CatalogoAtividadeRepository catalogoAtividadeRepository;

    @InjectMocks
    private CatalogoAtividadeService catalogoAtividadeService;

    private CatalogoAtividade sampleAtividade;

    @BeforeEach
    void setUp() {
        sampleAtividade = CatalogoAtividade.builder()
                .id(1L)
                .nome("Museu do Louvre")
                .tipo("Cultura")
                .localizacao("Centro")
                .cidade("Paris")
                .pais("França")
                .descricao("O maior museu de arte do mundo")
                .fotoUrl("louvre.jpg")
                .precoMedio(BigDecimal.valueOf(100.00))
                .avaliacaoMedia(4.8)
                .totalAvaliacoes(320)
                .build();
    }

    @Test
    @DisplayName("Deve buscar atividades com filtros e paginação")
    void shouldSearchAtividades() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CatalogoAtividade> page = new PageImpl<>(List.of(sampleAtividade), pageable, 1);

        when(catalogoAtividadeRepository.searchAtividades("Louvre", "Cultura", 4.5, pageable)).thenReturn(page);

        Page<CatalogoAtividadeDTO> result = catalogoAtividadeService.searchAtividades("Louvre", "Cultura", 4.5, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Museu do Louvre", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar atividade por ID no catálogo")
    void shouldGetAtividadeById() {
        when(catalogoAtividadeRepository.findById(1L)).thenReturn(Optional.of(sampleAtividade));

        CatalogoAtividadeDTO result = catalogoAtividadeService.getAtividadeById(1L);

        assertNotNull(result);
        assertEquals("Museu do Louvre", result.getNome());
        assertEquals(4.8, result.getAvaliacaoMedia());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando atividade não for encontrada")
    void shouldThrowWhenAtividadeNotFound() {
        when(catalogoAtividadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogoAtividadeService.getAtividadeById(99L));
    }

    @Test
    @DisplayName("Deve listar top atividades mais bem avaliadas")
    void shouldGetTopRatedAtividades() {
        when(catalogoAtividadeRepository.findTop6ByOrderByAvaliacaoMediaDesc()).thenReturn(List.of(sampleAtividade));

        List<CatalogoAtividadeDTO> result = catalogoAtividadeService.getTopRatedAtividades();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.8, result.get(0).getAvaliacaoMedia());
    }
}
