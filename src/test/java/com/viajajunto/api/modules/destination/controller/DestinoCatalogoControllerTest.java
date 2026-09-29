package com.viajajunto.api.modules.destination.controller;

import com.viajajunto.api.core.security.UserPrincipal;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.destination.dto.DestinoCatalogoDTO;
import com.viajajunto.api.modules.destination.dto.VisitedCountryDTO;
import com.viajajunto.api.modules.destination.service.DestinoCatalogoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinoCatalogoControllerTest {

    @Mock
    private DestinoCatalogoService destinoCatalogoService;

    @InjectMocks
    private DestinoCatalogoController destinoCatalogoController;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).email("user@test.com").build();
        principal = new UserPrincipal(user);
    }

    @Test
    @DisplayName("searchDestinos - Deve retornar HTTP 200 OK com página de destinos")
    void shouldSearchDestinos() {
        Pageable pageable = PageRequest.of(0, 12);
        DestinoCatalogoDTO dto = DestinoCatalogoDTO.builder().id(1L).nome("Roma").build();
        Page<DestinoCatalogoDTO> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(destinoCatalogoService.searchDestinos("Roma", "Histórico", 4.0, pageable)).thenReturn(page);

        ResponseEntity<Page<DestinoCatalogoDTO>> response = destinoCatalogoController.searchDestinos("Roma", "Histórico", 4.0, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    @DisplayName("getDestinoById - Deve retornar HTTP 200 OK")
    void shouldGetDestinoById() {
        DestinoCatalogoDTO dto = DestinoCatalogoDTO.builder().id(1L).nome("Roma").build();
        when(destinoCatalogoService.getDestinoById(1L)).thenReturn(dto);

        ResponseEntity<DestinoCatalogoDTO> response = destinoCatalogoController.getDestinoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roma", response.getBody().getNome());
    }

    @Test
    @DisplayName("getTopRatedDestinos - Deve retornar HTTP 200 OK")
    void shouldGetTopRatedDestinos() {
        DestinoCatalogoDTO dto = DestinoCatalogoDTO.builder().id(1L).nome("Roma").build();
        when(destinoCatalogoService.getTopRatedDestinos()).thenReturn(List.of(dto));

        ResponseEntity<List<DestinoCatalogoDTO>> response = destinoCatalogoController.getTopRatedDestinos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getVisitedCountries - Deve retornar HTTP 200 OK")
    void shouldGetVisitedCountries() {
        VisitedCountryDTO dto = VisitedCountryDTO.builder().totalVisited(3).visitedIsoCodes(List.of("BR", "IT", "FR")).build();
        when(destinoCatalogoService.getUserVisitedCountries(1L)).thenReturn(dto);

        ResponseEntity<VisitedCountryDTO> response = destinoCatalogoController.getVisitedCountries(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getTotalVisited());
    }
}
