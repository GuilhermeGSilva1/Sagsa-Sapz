package tarefas02.api.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "planos_curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "codigo_plano", nullable = false, length = 50)
    private String codigoPlano;

    @Column(nullable = false, length = 20)
    private String versao = "1.0";

    @Column(name = "carga_horaria_total", nullable = false)
    private Integer cargaHorariaTotal;

    @Column(name = "data_aprovacao")
    private LocalDate dataAprovacao;

    @Column(nullable = false)
    private Boolean ativo = true;
}
