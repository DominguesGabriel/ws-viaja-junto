package com.viajajunto.api.modules.review.service;

import com.viajajunto.api.core.exception.BusinessRuleException;
import com.viajajunto.api.core.exception.ResourceNotFoundException;
import com.viajajunto.api.modules.activity.entity.CatalogoAtividade;
import com.viajajunto.api.modules.activity.repository.CatalogoAtividadeRepository;
import com.viajajunto.api.modules.auth.dto.UserDTO;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.auth.repository.UserRepository;
import com.viajajunto.api.modules.destination.entity.DestinoCatalogo;
import com.viajajunto.api.modules.destination.repository.DestinoCatalogoRepository;
import com.viajajunto.api.modules.review.dto.AvaliacaoResponseDTO;
import com.viajajunto.api.modules.review.dto.CreateAvaliacaoDTO;
import com.viajajunto.api.modules.review.entity.Avaliacao;
import com.viajajunto.api.modules.review.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UserRepository userRepository;
    private final DestinoCatalogoRepository destinoCatalogoRepository;
    private final CatalogoAtividadeRepository catalogoAtividadeRepository;

    @Transactional
    public AvaliacaoResponseDTO createAvaliacao(CreateAvaliacaoDTO dto, Long userId) {
        // RN05: Apenas usuários registrados podem avaliar destinos e atividades
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (dto.getDestinoCatalogoId() == null && dto.getCatalogoAtividadeId() == null) {
            throw new BusinessRuleException("A avaliação deve estar associada a um Destino ou a uma Atividade.");
        }

        DestinoCatalogo destino = null;
        if (dto.getDestinoCatalogoId() != null) {
            destino = destinoCatalogoRepository.findById(dto.getDestinoCatalogoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destino não encontrado com ID: " + dto.getDestinoCatalogoId()));
        }

        CatalogoAtividade atividade = null;
        if (dto.getCatalogoAtividadeId() != null) {
            atividade = catalogoAtividadeRepository.findById(dto.getCatalogoAtividadeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada com ID: " + dto.getCatalogoAtividadeId()));
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .usuario(user)
                .destinoCatalogo(destino)
                .catalogoAtividade(atividade)
                .nota(dto.getNota())
                .comentario(dto.getComentario())
                .build();

        Avaliacao saved = avaliacaoRepository.save(avaliacao);

        // Recalcular médias
        if (destino != null) {
            Double media = avaliacaoRepository.calculateAverageRatingForDestino(destino.getId());
            destino.setAvaliacaoMedia(media != null ? Math.round(media * 10.0) / 10.0 : 0.0);
            destino.setTotalAvaliacoes(destino.getTotalAvaliacoes() + 1);
            destinoCatalogoRepository.save(destino);
        }

        if (atividade != null) {
            Double media = avaliacaoRepository.calculateAverageRatingForAtividade(atividade.getId());
            atividade.setAvaliacaoMedia(media != null ? Math.round(media * 10.0) / 10.0 : 0.0);
            atividade.setTotalAvaliacoes(atividade.getTotalAvaliacoes() + 1);
            catalogoAtividadeRepository.save(atividade);
        }

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponseDTO> listAvaliacoesByDestino(Long destinoId, Pageable pageable) {
        return avaliacaoRepository.findAllByDestinoCatalogoIdOrderByDataCriacaoDesc(destinoId, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponseDTO> listAvaliacoesByAtividade(Long atividadeId, Pageable pageable) {
        return avaliacaoRepository.findAllByCatalogoAtividadeIdOrderByDataCriacaoDesc(atividadeId, pageable)
                .map(this::mapToDTO);
    }

    private AvaliacaoResponseDTO mapToDTO(Avaliacao entity) {
        return AvaliacaoResponseDTO.builder()
                .id(entity.getId())
                .usuario(UserDTO.builder()
                        .id(entity.getUsuario().getId())
                        .nome(entity.getUsuario().getNome())
                        .email(entity.getUsuario().getEmail())
                        .avatarUrl(entity.getUsuario().getAvatarUrl())
                        .build())
                .destinoCatalogoId(entity.getDestinoCatalogo() != null ? entity.getDestinoCatalogo().getId() : null)
                .catalogoAtividadeId(entity.getCatalogoAtividade() != null ? entity.getCatalogoAtividade().getId() : null)
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .dataCriacao(entity.getDataCriacao())
                .build();
    }
}
