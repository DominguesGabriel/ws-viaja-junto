package com.viajajunto.api.modules.activity.controller;

import com.viajajunto.api.modules.activity.dto.CatalogoAtividadeDTO;
import com.viajajunto.api.modules.activity.service.CatalogoAtividadeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoAtividadeControllerTest {

    @Mock
    private CatalogoAtividadeService catalogoAtividadeService;

    @InjectMocks
    private CatalogoAtividadeController catalogoAtividadeController;

    @Test
    @DisplayName("searchAtividades - Deve retornar HTTP 200 OK com página de atividades")
    void shouldSearchAtividades() {
        Pageable pageable = PageRequest.of(0, 12);
        CatalogoAtividadeDTO dto = CatalogoAtividadeDTO.builder().id(1L).nome("Coliseu").build();
        Page<CatalogoAtividadeDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(catalogoAtividadeService.searchAtividades("Coliseu", "Passeio", 4.0, pageable)).thenReturn(page);

        ResponseEntity<Page<CatalogoAtividadeDTO>> response = catalogoAtividadeController.searchAtividades("Coliseu", "Passeio", 4.0, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    @DisplayName("getAtividadeById - Deve retornar HTTP 200 OK")
    void shouldGetAtividadeById() {
        CatalogoAtividadeDTO dto = CatalogoAtividadeDTO.builder().id(1L).nome("Coliseu").build();
        when(catalogoAtividadeService.getAtividadeById(1L)).thenReturn(dto);

        ResponseEntity<CatalogoAtividadeDTO> response = catalogoAtividadeController.getAtividadeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Coliseu", response.getBody().getNome());
    }

    @Test
    @DisplayName("getTopRatedAtividades - Deve retornar HTTP 200 OK com destaques")
    void shouldGetTopRatedAtividades() {
        CatalogoAtividadeDTO dto = CatalogoAtividadeDTO.builder().id(1L).nome("Coliseu").build();
        when(catalogoAtividadeService.getTopRatedAtividades()).thenReturn(List.of(dto));

        ResponseEntity<List<CatalogoAtividadeDTO>> response = catalogoAtividadeController.getTopRatedAtividades();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
