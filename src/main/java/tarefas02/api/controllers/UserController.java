
package tarefas02.api.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tarefas02.api.models.User;
import tarefas02.api.models.dtos.UserCreateDTO;
import tarefas02.api.models.dtos.UserUpdateDTO;
import tarefas02.api.services.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO dto) {
        User user = userService.fromDTO(dto);
        User newUser = userService.create(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @Valid @RequestBody UserUpdateDTO dto,
            @PathVariable Long id) {

        User user = new User();
        user.setId(id);
        user.setPassword(dto.getPassword());

        userService.update(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
