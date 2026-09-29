package com.viajajunto.api.core.security;

import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.exception.UnauthorizedAccessException;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.trip.entity.MembroViagem;
import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import com.viajajunto.api.modules.trip.entity.Viagem;
import com.viajajunto.api.modules.trip.repository.MembroViagemRepository;
import com.viajajunto.api.modules.trip.repository.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripSecurityServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private MembroViagemRepository membroViagemRepository;

    @InjectMocks
    private TripSecurityService tripSecurityService;

    private User criador;
    private Viagem viagem;

    @BeforeEach
    void setUp() {
        criador = User.builder().id(1L).nome("Criador").email("criador@test.com").build();
        viagem = Viagem.builder().id(10L).nome("Viagem Teste").criador(criador).build();
    }

    @Test
    @DisplayName("validateUserCanViewTrip - Deve permitir visualização para o criador")
    void shouldAllowCreatorToViewTrip() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));

        Viagem result = tripSecurityService.validateUserCanViewTrip(10L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(membroViagemRepository, never()).existsByViagemIdAndUsuarioId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("validateUserCanViewTrip - Deve permitir visualização para membro da viagem")
    void shouldAllowMemberToViewTrip() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.existsByViagemIdAndUsuarioId(10L, 2L)).thenReturn(true);

        Viagem result = tripSecurityService.validateUserCanViewTrip(10L, 2L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("validateUserCanViewTrip - Deve lançar UnauthorizedAccessException para não membro")
    void shouldThrowWhenNonMemberViewsTrip() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.existsByViagemIdAndUsuarioId(10L, 2L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> tripSecurityService.validateUserCanViewTrip(10L, 2L));
    }

    @Test
    @DisplayName("validateUserCanViewTrip - Deve lançar ResourceNotFoundException quando viagem não existe")
    void shouldThrowWhenTripNotFoundOnView() {
        when(viagemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripSecurityService.validateUserCanViewTrip(99L, 1L));
    }

    @Test
    @DisplayName("validateUserCanEditTrip - Deve permitir edição para o criador")
    void shouldAllowCreatorToEditTrip() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));

        Viagem result = tripSecurityService.validateUserCanEditTrip(10L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("validateUserCanEditTrip - Deve permitir edição para membro com papel EDITOR")
    void shouldAllowEditorToEditTrip() {
        MembroViagem membroEditor = MembroViagem.builder()
                .permissao(PermissaoMembro.EDITOR)
                .build();

        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.findByViagemIdAndUsuarioId(10L, 2L)).thenReturn(Optional.of(membroEditor));

        Viagem result = tripSecurityService.validateUserCanEditTrip(10L, 2L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("validateUserCanEditTrip - Deve lançar UnauthorizedAccessException para membro com papel VISUALIZADOR")
    void shouldThrowWhenViewerTriesToEditTrip() {
        MembroViagem membroViewer = MembroViagem.builder()
                .permissao(PermissaoMembro.VISUALIZADOR)
                .build();

        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.findByViagemIdAndUsuarioId(10L, 2L)).thenReturn(Optional.of(membroViewer));

        assertThrows(UnauthorizedAccessException.class, () -> tripSecurityService.validateUserCanEditTrip(10L, 2L));
    }

    @Test
    @DisplayName("validateUserCanEditTrip - Deve lançar ResourceNotFoundException quando viagem não existe")
    void shouldThrowWhenTripNotFoundOnEdit() {
        when(viagemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripSecurityService.validateUserCanEditTrip(99L, 1L));
    }

    @Test
    @DisplayName("validateUserIsOwner - Deve permitir acesso ao proprietário/criador")
    void shouldAllowOwner() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));

        Viagem result = tripSecurityService.validateUserIsOwner(10L, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("validateUserIsOwner - Deve lançar UnauthorizedAccessException para quem não é criador")
    void shouldThrowWhenUserIsNotOwner() {
        when(viagemRepository.findById(10L)).thenReturn(Optional.of(viagem));

        assertThrows(UnauthorizedAccessException.class, () -> tripSecurityService.validateUserIsOwner(10L, 2L));
    }

    @Test
    @DisplayName("validateUserIsOwner - Deve lançar ResourceNotFoundException quando viagem não existe")
    void shouldThrowWhenTripNotFoundOnOwnerCheck() {
        when(viagemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripSecurityService.validateUserIsOwner(99L, 1L));
    }
}
