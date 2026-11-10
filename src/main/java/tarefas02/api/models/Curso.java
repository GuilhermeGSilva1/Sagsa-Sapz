package tarefas02.api.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "area_tecnologica_id")
    private AreaTecnologica areaTecnologica;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "tipo_curso", nullable = false, length = 50)
    private String tipoCurso;

    @Column(nullable = false)
    private Boolean ativo = true;
}
