package com.viajajunto.api.modules.review.repository;

import com.viajajunto.api.modules.review.entity.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Page<Avaliacao> findAllByDestinoCatalogoIdOrderByDataCriacaoDesc(Long destinoCatalogoId, Pageable pageable);

    Page<Avaliacao> findAllByCatalogoAtividadeIdOrderByDataCriacaoDesc(Long catalogoAtividadeId, Pageable pageable);

    List<Avaliacao> findAllByDestinoCatalogoId(Long destinoCatalogoId);

    List<Avaliacao> findAllByCatalogoAtividadeId(Long catalogoAtividadeId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.destinoCatalogo.id = :destinoId")
    Double calculateAverageRatingForDestino(@Param("destinoId") Long destinoId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.catalogoAtividade.id = :atividadeId")
    Double calculateAverageRatingForAtividade(@Param("atividadeId") Long atividadeId);
}
