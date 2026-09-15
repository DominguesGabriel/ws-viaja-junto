package com.viajajunto.api.modules.activity.repository;

import com.viajajunto.api.modules.activity.entity.AtividadeViagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeViagemRepository extends JpaRepository<AtividadeViagem, Long> {

    List<AtividadeViagem> findAllByDestinoViagemIdOrderByDataHorarioAsc(Long destinoViagemId);

    @Query("SELECT a FROM AtividadeViagem a WHERE a.destinoViagem.viagem.id = :viagemId")
    List<AtividadeViagem> findAllByViagemId(@Param("viagemId") Long viagemId);
}
