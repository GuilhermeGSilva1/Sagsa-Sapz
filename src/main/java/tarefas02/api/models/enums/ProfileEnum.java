package tarefas02.api.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ProfileEnum {
    ADMIN(1, "ROLE_ADMINISTRADOR"),
    COORDENADOR(2, "ROLE_COORDENADOR"),
    INSTRUTOR(3, "ROLE_INSTRUTOR");

    private Integer code;
    private String description;

    public static ProfileEnum toEnum(Integer code) {
        if (Objects.isNull(code))
            return null;

        for (ProfileEnum x : ProfileEnum.values()) {
            if (code.equals(x.getCode()))
                return x;
        }

        throw new IllegalArgumentException("Código inválido: " + code);
    }
}
