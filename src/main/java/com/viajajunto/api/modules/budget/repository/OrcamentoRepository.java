package com.viajajunto.api.modules.budget.repository;

import com.viajajunto.api.modules.budget.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    Optional<Orcamento> findByViagemId(Long viagemId);
    void deleteByViagemId(Long viagemId);
}
