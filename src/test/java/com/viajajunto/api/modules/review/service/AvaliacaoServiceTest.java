package com.viajajunto.api.modules.review.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.modules.activity.entity.CatalogoAtividade;
import com.viajajunto.api.modules.activity.repository.CatalogoAtividadeRepository;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import com.viajajunto.api.modules.destination.entity.DestinoCatalogo;
import com.viajajunto.api.modules.destination.repository.DestinoCatalogoRepository;
import com.viajajunto.api.modules.review.dto.AvaliacaoResponseDTO;
import com.viajajunto.api.modules.review.dto.CreateAvaliacaoDTO;
import com.viajajunto.api.modules.review.entity.Avaliacao;
import com.viajajunto.api.modules.review.repository.AvaliacaoRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DestinoCatalogoRepository destinoCatalogoRepository;

    @Mock
    private CatalogoAtividadeRepository catalogoAtividadeRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private User sampleUser;
    private DestinoCatalogo sampleDestino;
    private CatalogoAtividade sampleAtividade;
    private Avaliacao sampleAvaliacao;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).nome("João").email("joao@test.com").build();
        sampleDestino = DestinoCatalogo.builder().id(10L).nome("Roma").avaliacaoMedia(4.0).totalAvaliacoes(5).build();
        sampleAtividade = CatalogoAtividade.builder().id(20L).nome("Coliseu").avaliacaoMedia(4.0).totalAvaliacoes(5).build();
        sampleAvaliacao = Avaliacao.builder()
                .id(100L)
                .usuario(sampleUser)
                .destinoCatalogo(sampleDestino)
                .nota(5)
                .comentario("Incrível!")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar avaliação para Destino e atualizar média")
    void shouldCreateAvaliacaoForDestino() {
        CreateAvaliacaoDTO dto = CreateAvaliacaoDTO.builder()
                .destinoCatalogoId(10L)
                .nota(5)
                .comentario("Incrível!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(destinoCatalogoRepository.findById(10L)).thenReturn(Optional.of(sampleDestino));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(sampleAvaliacao);
        when(avaliacaoRepository.calculateAverageRatingForDestino(10L)).thenReturn(4.8);

        AvaliacaoResponseDTO response = avaliacaoService.createAvaliacao(dto, 1L);

        assertNotNull(response);
        assertEquals(5, response.getNota());
        assertEquals(10L, response.getDestinoCatalogoId());
        assertEquals(4.8, sampleDestino.getAvaliacaoMedia());
        assertEquals(6, sampleDestino.getTotalAvaliacoes());
        verify(destinoCatalogoRepository, times(1)).save(sampleDestino);
    }

    @Test
    @DisplayName("Deve criar avaliação para Atividade e atualizar média")
    void shouldCreateAvaliacaoForAtividade() {
        CreateAvaliacaoDTO dto = CreateAvaliacaoDTO.builder()
                .catalogoAtividadeId(20L)
                .nota(4)
                .comentario("Muito bom!")
                .build();

        Avaliacao avaliacaoAtiv = Avaliacao.builder()
                .id(101L)
                .usuario(sampleUser)
                .catalogoAtividade(sampleAtividade)
                .nota(4)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(catalogoAtividadeRepository.findById(20L)).thenReturn(Optional.of(sampleAtividade));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacaoAtiv);
        when(avaliacaoRepository.calculateAverageRatingForAtividade(20L)).thenReturn(4.5);

        AvaliacaoResponseDTO response = avaliacaoService.createAvaliacao(dto, 1L);

        assertNotNull(response);
        assertEquals(4, response.getNota());
        assertEquals(20L, response.getCatalogoAtividadeId());
        assertEquals(4.5, sampleAtividade.getAvaliacaoMedia());
        assertEquals(6, sampleAtividade.getTotalAvaliacoes());
        verify(catalogoAtividadeRepository, times(1)).save(sampleAtividade);
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando nem destino nem atividade forem informados")
    void shouldThrowWhenNoTargetProvided() {
        CreateAvaliacaoDTO dto = CreateAvaliacaoDTO.builder().nota(5).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(BusinessRuleException.class, () -> avaliacaoService.createAvaliacao(dto, 1L));
    }

    @Test
    @DisplayName("Deve listar avaliações por destino")
    void shouldListAvaliacoesByDestino() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Avaliacao> page = new PageImpl<>(List.of(sampleAvaliacao), pageable, 1);

        when(avaliacaoRepository.findAllByDestinoCatalogoIdOrderByDataCriacaoDesc(10L, pageable)).thenReturn(page);

        Page<AvaliacaoResponseDTO> result = avaliacaoService.listAvaliacoesByDestino(10L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Incrível!", result.getContent().get(0).getComentario());
    }

    @Test
    @DisplayName("Deve listar avaliações por atividade")
    void shouldListAvaliacoesByAtividade() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Avaliacao> page = new PageImpl<>(List.of(sampleAvaliacao), pageable, 1);

        when(avaliacaoRepository.findAllByCatalogoAtividadeIdOrderByDataCriacaoDesc(20L, pageable)).thenReturn(page);

        Page<AvaliacaoResponseDTO> result = avaliacaoService.listAvaliacoesByAtividade(20L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
