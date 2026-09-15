package com.viajajunto.api.modules.trip.entity;

import com.viajajunto.api.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "membros_viagem", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"viagem_id", "usuario_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembroViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PermissaoMembro permissao = PermissaoMembro.VISUALIZADOR;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataEntrada;
}
