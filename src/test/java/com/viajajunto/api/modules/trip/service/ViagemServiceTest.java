package com.viajajunto.api.modules.trip.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.security.TripSecurityService;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import com.viajajunto.api.modules.budget.entity.Orcamento;
import com.viajajunto.api.modules.budget.repository.OrcamentoRepository;
import com.viajajunto.api.modules.trip.dto.CreateViagemDTO;
import com.viajajunto.api.modules.trip.dto.UpdateViagemDTO;
import com.viajajunto.api.modules.trip.dto.ViagemResponseDTO;
import com.viajajunto.api.modules.trip.entity.MembroViagem;
import com.viajajunto.api.modules.trip.entity.PermissaoMembro;
import com.viajajunto.api.modules.trip.entity.StatusViagem;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private MembroViagemRepository membroViagemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private TripSecurityService tripSecurityService;

    @InjectMocks
    private ViagemService viagemService;

    private User sampleUser;
    private User otherUser;
    private Viagem sampleViagem;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .nome("Gabriel Domingues")
                .email("gabriel@example.com")
                .build();

        otherUser = User.builder()
                .id(2L)
                .nome("Maria Silva")
                .email("maria@example.com")
                .build();

        sampleViagem = Viagem.builder()
                .id(10L)
                .nome("Férias em Roma")
                .descricao("Viagem cultural")
                .dataInicio(LocalDate.of(2026, 10, 1))
                .dataFim(LocalDate.of(2026, 10, 15))
                .status(StatusViagem.EM_PLANEJAMENTO)
                .codigoConvite("ROMA2026")
                .criador(sampleUser)
                .build();
    }

    @Test
    @DisplayName("Deve criar viagem com sucesso e gerar orçamento associado")
    void shouldCreateViagemSuccessfully() {
        CreateViagemDTO dto = CreateViagemDTO.builder()
                .nome("Férias em Roma")
                .descricao("Viagem cultural")
                .dataInicio(LocalDate.of(2026, 10, 1))
                .dataFim(LocalDate.of(2026, 10, 15))
                .orcamentoTotal(BigDecimal.valueOf(5000))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(viagemRepository.findByCodigoConvite(anyString())).thenReturn(Optional.empty());
        when(viagemRepository.save(any(Viagem.class))).thenReturn(sampleViagem);
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(new Orcamento());
        when(membroViagemRepository.findAllByViagemId(10L)).thenReturn(Collections.emptyList());

        ViagemResponseDTO response = viagemService.createViagem(dto, 1L);

        assertNotNull(response);
        assertEquals("Férias em Roma", response.getNome());
        assertEquals(PermissaoMembro.CRIADOR, response.getPermissaoUsuarioAutenticado());
        assertEquals(BigDecimal.valueOf(5000), response.getOrcamentoTotal());
        verify(viagemRepository, times(1)).save(any(Viagem.class));
        verify(orcamentoRepository, times(1)).save(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao criar viagem para usuário inexistente")
    void shouldThrowWhenUserNotFoundOnCreate() {
        CreateViagemDTO dto = CreateViagemDTO.builder().nome("Roma").build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> viagemService.createViagem(dto, 99L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando data final for anterior à data inicial")
    void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        CreateViagemDTO dto = CreateViagemDTO.builder()
                .nome("Férias Inválidas")
                .dataInicio(LocalDate.of(2026, 10, 15))
                .dataFim(LocalDate.of(2026, 10, 1))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        assertThrows(BusinessRuleException.class, () -> viagemService.createViagem(dto, 1L));
        verify(viagemRepository, never()).save(any(Viagem.class));
    }

    @Test
    @DisplayName("Deve listar viagens do usuário com papéis corretos")
    void shouldListUserTrips() {
        when(viagemRepository.findAllByUserAccess(1L)).thenReturn(List.of(sampleViagem));
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.of(Orcamento.builder().orcamentoTotal(BigDecimal.valueOf(2000)).build()));
        when(membroViagemRepository.findAllByViagemId(10L)).thenReturn(Collections.emptyList());

        List<ViagemResponseDTO> list = viagemService.listUserTrips(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(PermissaoMembro.CRIADOR, list.get(0).getPermissaoUsuarioAutenticado());
    }

    @Test
    @DisplayName("Deve buscar viagem por ID retornando DTO completo com membros")
    void shouldGetViagemById() {
        MembroViagem membro = MembroViagem.builder()
                .id(1L)
                .usuario(otherUser)
                .permissao(PermissaoMembro.EDITOR)
                .build();

        when(tripSecurityService.validateUserCanViewTrip(10L, 2L)).thenReturn(sampleViagem);
        when(membroViagemRepository.findByViagemIdAndUsuarioId(10L, 2L)).thenReturn(Optional.of(membro));
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.empty());
        when(membroViagemRepository.findAllByViagemId(10L)).thenReturn(List.of(membro));

        ViagemResponseDTO response = viagemService.getViagemById(10L, 2L);

        assertNotNull(response);
        assertEquals(PermissaoMembro.EDITOR, response.getPermissaoUsuarioAutenticado());
        assertEquals(1, response.getMembros().size());
        assertEquals("Maria Silva", response.getMembros().get(0).getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve atualizar viagem com sucesso")
    void shouldUpdateViagemSuccessfully() {
        UpdateViagemDTO dto = UpdateViagemDTO.builder()
                .nome("Férias em Roma Atualizada")
                .descricao("Nova descrição")
                .dataInicio(LocalDate.of(2026, 11, 1))
                .dataFim(LocalDate.of(2026, 11, 10))
                .status(StatusViagem.CONFIRMADA)
                .build();

        when(tripSecurityService.validateUserCanEditTrip(10L, 1L)).thenReturn(sampleViagem);
        when(viagemRepository.save(sampleViagem)).thenReturn(sampleViagem);
        when(orcamentoRepository.findByViagemId(10L)).thenReturn(Optional.empty());
        when(membroViagemRepository.findAllByViagemId(10L)).thenReturn(Collections.emptyList());

        ViagemResponseDTO response = viagemService.updateViagem(10L, dto, 1L);

        assertNotNull(response);
        assertEquals("Férias em Roma Atualizada", sampleViagem.getNome());
        assertEquals("Nova descrição", sampleViagem.getDescricao());
        assertEquals(StatusViagem.CONFIRMADA, sampleViagem.getStatus());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException na atualização quando data final for anterior à data inicial")
    void shouldThrowExceptionWhenUpdatingWithInvalidDates() {
        UpdateViagemDTO dto = UpdateViagemDTO.builder()
                .dataInicio(LocalDate.of(2026, 11, 10))
                .dataFim(LocalDate.of(2026, 11, 1))
                .build();

        when(tripSecurityService.validateUserCanEditTrip(10L, 1L)).thenReturn(sampleViagem);

        assertThrows(BusinessRuleException.class, () -> viagemService.updateViagem(10L, dto, 1L));
        verify(viagemRepository, never()).save(any(Viagem.class));
    }

    @Test
    @DisplayName("Deve deletar viagem e orçamento associado")
    void shouldDeleteViagemSuccessfully() {
        when(tripSecurityService.validateUserIsOwner(10L, 1L)).thenReturn(sampleViagem);

        viagemService.deleteViagem(10L, 1L);

        verify(orcamentoRepository, times(1)).deleteByViagemId(10L);
        verify(viagemRepository, times(1)).delete(sampleViagem);
    }
}
