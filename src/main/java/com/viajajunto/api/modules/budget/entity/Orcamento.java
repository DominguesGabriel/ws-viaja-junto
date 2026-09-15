package com.viajajunto.api.modules.budget.entity;

import com.viajajunto.api.modules.trip.entity.Viagem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orcamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id", nullable = false, unique = true)
    private Viagem viagem;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal orcamentoTotal = BigDecimal.ZERO;
}
