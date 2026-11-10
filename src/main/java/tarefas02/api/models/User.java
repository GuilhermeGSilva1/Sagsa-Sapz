package tarefas02.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import tarefas02.api.models.enums.ProfileEnum;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(min = 2, max = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    @NotBlank
    @Email
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "senha_hash", nullable = false)
    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile")
    @Column(name = "profile_id")
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles() {
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (profiles.isEmpty() && perfil != null) {
            if (perfil.getId() == 1L) addProfile(ProfileEnum.ADMIN);
            else if (perfil.getId() == 2L) addProfile(ProfileEnum.COORDENADOR);
            else addProfile(ProfileEnum.INSTRUTOR);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
