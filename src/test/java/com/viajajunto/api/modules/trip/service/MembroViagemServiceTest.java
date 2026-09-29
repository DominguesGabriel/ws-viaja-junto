package com.viajajunto.api.modules.trip.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.core.security.TripSecurityService;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import com.viajajunto.api.modules.trip.dto.AddMembroDTO;
import com.viajajunto.api.modules.trip.dto.MembroResponseDTO;
import com.viajajunto.api.modules.trip.dto.UpdatePermissaoDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembroViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private MembroViagemRepository membroViagemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripSecurityService tripSecurityService;

    @InjectMocks
    private MembroViagemService membroViagemService;

    private User criador;
    private User usuario;
    private Viagem viagem;
    private MembroViagem membro;

    @BeforeEach
    void setUp() {
        criador = User.builder().id(1L).nome("Criador").email("criador@test.com").build();
        usuario = User.builder().id(2L).nome("Membro").email("membro@test.com").avatarUrl("avatar.png").build();
        viagem = Viagem.builder().id(10L).nome("Viagem").codigoConvite("CODE123").criador(criador).build();
        membro = MembroViagem.builder().id(100L).viagem(viagem).usuario(usuario).permissao(PermissaoMembro.EDITOR).dataEntrada(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve entrar na viagem via código de convite com sucesso")
    void shouldJoinViagemByCodeSuccessfully() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("code123").permissao(PermissaoMembro.EDITOR).build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(viagemRepository.findByCodigoConvite("CODE123")).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.existsByViagemIdAndUsuarioId(10L, 2L)).thenReturn(false);
        when(membroViagemRepository.save(any(MembroViagem.class))).thenReturn(membro);

        MembroResponseDTO response = membroViagemService.joinViagemByCode(dto, 2L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Membro", response.getUsuario().getNome());
        assertEquals(PermissaoMembro.EDITOR, response.getPermissao());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não for encontrado no join")
    void shouldThrowWhenUserNotFoundOnJoin() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("CODE123").build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> membroViagemService.joinViagemByCode(dto, 99L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando código de convite for inválido")
    void shouldThrowWhenInvalidInviteCode() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("INVALID").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(viagemRepository.findByCodigoConvite("INVALID")).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> membroViagemService.joinViagemByCode(dto, 2L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException se o criador tentar entrar como membro da própria viagem")
    void shouldThrowWhenCreatorTriesToJoin() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("CODE123").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(criador));
        when(viagemRepository.findByCodigoConvite("CODE123")).thenReturn(Optional.of(viagem));

        assertThrows(BusinessRuleException.class, () -> membroViagemService.joinViagemByCode(dto, 1L));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException se o usuário já for membro da viagem")
    void shouldThrowWhenUserAlreadyMember() {
        AddMembroDTO dto = AddMembroDTO.builder().codigoConvite("CODE123").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(viagemRepository.findByCodigoConvite("CODE123")).thenReturn(Optional.of(viagem));
        when(membroViagemRepository.existsByViagemIdAndUsuarioId(10L, 2L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> membroViagemService.joinViagemByCode(dto, 2L));
    }

    @Test
    @DisplayName("Deve listar membros da viagem com sucesso")
    void shouldListMembros() {
        when(membroViagemRepository.findAllByViagemId(10L)).thenReturn(List.of(membro));

        List<MembroResponseDTO> list = membroViagemService.listMembros(10L, 1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(tripSecurityService, times(1)).validateUserCanViewTrip(10L, 1L);
    }

    @Test
    @DisplayName("Deve atualizar permissão de membro com sucesso")
    void shouldUpdatePermissao() {
        UpdatePermissaoDTO dto = UpdatePermissaoDTO.builder().permissao(PermissaoMembro.VISUALIZADOR).build();
        when(membroViagemRepository.findById(100L)).thenReturn(Optional.of(membro));
        when(membroViagemRepository.save(membro)).thenReturn(membro);

        MembroResponseDTO response = membroViagemService.updatePermissao(10L, 100L, dto, 1L);

        assertNotNull(response);
        assertEquals(PermissaoMembro.VISUALIZADOR, membro.getPermissao());
        verify(tripSecurityService, times(1)).validateUserIsOwner(10L, 1L);
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao atualizar permissão de membro que não pertence à viagem")
    void shouldThrowWhenMemberDoesNotBelongToTripOnUpdate() {
        Viagem otherTrip = Viagem.builder().id(999L).build();
        MembroViagem otherMember = MembroViagem.builder().id(100L).viagem(otherTrip).build();
        UpdatePermissaoDTO dto = UpdatePermissaoDTO.builder().permissao(PermissaoMembro.VISUALIZADOR).build();

        when(membroViagemRepository.findById(100L)).thenReturn(Optional.of(otherMember));

        assertThrows(BusinessRuleException.class, () -> membroViagemService.updatePermissao(10L, 100L, dto, 1L));
    }

    @Test
    @DisplayName("Deve remover membro com sucesso")
    void shouldRemoveMembro() {
        when(membroViagemRepository.findById(100L)).thenReturn(Optional.of(membro));

        membroViagemService.removeMembro(10L, 100L, 1L);

        verify(tripSecurityService, times(1)).validateUserIsOwner(10L, 1L);
        verify(membroViagemRepository, times(1)).delete(membro);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar remover membro inexistente")
    void shouldThrowWhenMemberNotFoundOnRemove() {
        when(membroViagemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> membroViagemService.removeMembro(10L, 999L, 1L));
    }
}
