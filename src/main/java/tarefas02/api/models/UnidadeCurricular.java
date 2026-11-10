package tarefas02.api.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unidades_curriculares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeCurricular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Column(name = "objetivo_geral", columnDefinition = "TEXT")
    private String objetivoGeral;

    @Column(nullable = false)
    private Integer ordem = 1;
}
