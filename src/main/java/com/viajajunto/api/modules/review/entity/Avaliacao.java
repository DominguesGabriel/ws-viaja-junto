package com.viajajunto.api.modules.review.entity;

import com.viajajunto.api.modules.activity.entity.CatalogoAtividade;
import com.viajajunto.api.modules.auth.entity.User;
import com.viajajunto.api.modules.destination.entity.DestinoCatalogo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_catalogo_id")
    private DestinoCatalogo destinoCatalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_atividade_id")
    private CatalogoAtividade catalogoAtividade;

    @Column(nullable = false)
    private Integer nota; // 1 a 5 estrelas (RF22)

    @Column(length = 1500)
    private String comentario;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;
}
