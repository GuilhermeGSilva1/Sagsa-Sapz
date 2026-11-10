package tarefas02.api.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "escolas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "codigo_unidade", unique = true, length = 20)
    private String codigoUnidade;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado = "RS";

    @Column(nullable = false)
    private Boolean ativo = true;
}
