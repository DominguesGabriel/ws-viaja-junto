package com.viajajunto.api.modules.activity.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.security.TripSecurityService;
import com.viajajunto.api.modules.activity.dto.AtividadeResponseDTO;
import com.viajajunto.api.modules.activity.dto.CreateAtividadeDTO;
import com.viajajunto.api.modules.activity.entity.AtividadeViagem;
import com.viajajunto.api.modules.activity.entity.CatalogoAtividade;
import com.viajajunto.api.modules.activity.entity.StatusAtividade;
import com.viajajunto.api.modules.activity.repository.AtividadeViagemRepository;
import com.viajajunto.api.modules.activity.repository.CatalogoAtividadeRepository;
import com.viajajunto.api.modules.destination.entity.DestinoViagem;
import com.viajajunto.api.modules.destination.repository.DestinoViagemRepository;
import com.viajajunto.api.modules.trip.entity.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeViagemServiceTest {

    @Mock
    private AtividadeViagemRepository atividadeViagemRepository;

    @Mock
    private DestinoViagemRepository destinoViagemRepository;

    @Mock
    private CatalogoAtividadeRepository catalogoAtividadeRepository;

    @Mock
    private TripSecurityService tripSecurityService;

    @InjectMocks
    private AtividadeViagemService atividadeViagemService;

    private Viagem sampleViagem;
    private DestinoViagem sampleDestino;
    private CatalogoAtividade sampleCatalogo;
    private AtividadeViagem sampleAtividade;

    @BeforeEach
    void setUp() {
        sampleViagem = Viagem.builder().id(10L).nome("Viagem Teste").build();
        sampleDestino = DestinoViagem.builder().id(20L).viagem(sampleViagem).nome("Roma").build();
        sampleCatalogo = CatalogoAtividade.builder().id(30L).nome("Tour Coliseu").tipo("Passeio").localizacao("Roma").build();
        sampleAtividade = AtividadeViagem.builder()
                .id(100L)
                .destinoViagem(sampleDestino)
                .catalogoAtividade(sampleCatalogo)
                .nome("Tour Coliseu")
                .tipo("Passeio")
                .local("Roma")
                .custoPrevisto(BigDecimal.valueOf(150.00))
                .status(StatusAtividade.CONFIRMADA)
                .dataHorario(LocalDateTime.of(2026, 10, 2, 10, 0))
                .duracaoMinutos(120)
                .build();
    }

    @Test
    @DisplayName("Deve adicionar atividade vinculada ao catálogo com sucesso")
    void shouldAddAtividadeWithCatalogo() {
        CreateAtividadeDTO dto = CreateAtividadeDTO.builder()
                .catalogoAtividadeId(30L)
                .nome("Tour Coliseu")
                .custoPrevisto(BigDecimal.valueOf(150.00))
                .dataHorario(LocalDateTime.of(2026, 10, 2, 10, 0))
                .duracaoMinutos(120)
                .status(StatusAtividade.CONFIRMADA)
                .build();

        when(destinoViagemRepository.findById(20L)).thenReturn(Optional.of(sampleDestino));
        when(catalogoAtividadeRepository.findById(30L)).thenReturn(Optional.of(sampleCatalogo));
        when(atividadeViagemRepository.save(any(AtividadeViagem.class))).thenReturn(sampleAtividade);

        AtividadeResponseDTO response = atividadeViagemService.addAtividade(10L, 20L, dto, 1L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(20L, response.getDestinoViagemId());
        assertEquals(30L, response.getCatalogoAtividadeId());
        assertEquals("Tour Coliseu", response.getNome());
        verify(tripSecurityService, times(1)).validateUserCanEditTrip(10L, 1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando destino não existir ao adicionar atividade")
    void shouldThrowWhenDestinoNotFoundOnAdd() {
        CreateAtividadeDTO dto = CreateAtividadeDTO.builder().nome("Atividade").build();
        when(destinoViagemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atividadeViagemService.addAtividade(10L, 99L, dto, 1L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException se destino não pertencer à viagem ao adicionar atividade")
    void shouldThrowWhenDestinoNotBelongToTripOnAdd() {
        Viagem otherTrip = Viagem.builder().id(999L).build();
        DestinoViagem foreignDestino = DestinoViagem.builder().id(20L).viagem(otherTrip).build();
        CreateAtividadeDTO dto = CreateAtividadeDTO.builder().nome("Atividade").build();

        when(destinoViagemRepository.findById(20L)).thenReturn(Optional.of(foreignDestino));

        assertThrows(BusinessRuleException.class, () -> atividadeViagemService.addAtividade(10L, 20L, dto, 1L));
    }

    @Test
    @DisplayName("Deve listar atividades por destino")
    void shouldListAtividadesByDestino() {
        when(destinoViagemRepository.findById(20L)).thenReturn(Optional.of(sampleDestino));
        when(atividadeViagemRepository.findAllByDestinoViagemIdOrderByDataHorarioAsc(20L)).thenReturn(List.of(sampleAtividade));

        List<AtividadeResponseDTO> list = atividadeViagemService.listAtividadesByDestino(10L, 20L, 1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(tripSecurityService, times(1)).validateUserCanViewTrip(10L, 1L);
    }

    @Test
    @DisplayName("Deve remover atividade com sucesso")
    void shouldRemoveAtividadeSuccessfully() {
        when(atividadeViagemRepository.findById(100L)).thenReturn(Optional.of(sampleAtividade));

        atividadeViagemService.removeAtividade(10L, 20L, 100L, 1L);

        verify(tripSecurityService, times(1)).validateUserCanEditTrip(10L, 1L);
        verify(atividadeViagemRepository, times(1)).delete(sampleAtividade);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar remover atividade inexistente")
    void shouldThrowWhenAtividadeNotFoundOnRemove() {
        when(atividadeViagemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atividadeViagemService.removeAtividade(10L, 20L, 999L, 1L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao remover atividade pertencente a outro destino")
    void shouldThrowWhenAtividadeBelongsToOtherDestino() {
        DestinoViagem otherDestino = DestinoViagem.builder().id(888L).viagem(sampleViagem).build();
        AtividadeViagem foreignAtividade = AtividadeViagem.builder().id(100L).destinoViagem(otherDestino).build();

        when(atividadeViagemRepository.findById(100L)).thenReturn(Optional.of(foreignAtividade));

        assertThrows(BusinessRuleException.class, () -> atividadeViagemService.removeAtividade(10L, 20L, 100L, 1L));
    }
}
