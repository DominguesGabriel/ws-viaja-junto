package com.viajajunto.api.modules.trip.repository;

import com.viajajunto.api.modules.trip.entity.Viagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    Optional<Viagem> findByCodigoConvite(String codigoConvite);

    @Query("SELECT DISTINCT v FROM Viagem v LEFT JOIN v.membros m " +
           "WHERE v.criador.id = :userId OR m.usuario.id = :userId " +
           "ORDER BY v.dataCriacao DESC")
    List<Viagem> findAllByUserAccess(@Param("userId") Long userId);
}
