package tarefas02.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "situacoes_aprendizagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituacaoAprendizagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "unidade_curricular_id", nullable = false)
    private UnidadeCurricular unidadeCurricular;

    @ManyToOne
    @JoinColumn(name = "instrutor_id", nullable = false)
    private User instrutor;

    @Column(name = "tipo_estrategia", nullable = false, length = 50)
    private String tipoEstrategia;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contextualizacao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String desafio;

    @Column(name = "resultados_esperados", nullable = false, columnDefinition = "TEXT")
    private String resultadosEsperados;
    @NotNull(message = "A carga horária da SAPZ é obrigatória.")
    @Min(value = 1, message = "A carga horária da SAPZ deve ser de no mínimo 1 hora.")
    @Max(value = 408, message = "A carga horária da SAPZ não pode ultrapassar 408 horas.")
    @Column(name = "carga_horaria_sa", nullable = false)
    private Integer cargaHorariaSa;

    @Column(nullable = false)
    private Boolean compartilhada = false;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
