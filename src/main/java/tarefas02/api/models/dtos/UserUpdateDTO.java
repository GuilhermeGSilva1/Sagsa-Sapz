package tarefas02.api.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private Long id;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 255)
    private String password;
}
