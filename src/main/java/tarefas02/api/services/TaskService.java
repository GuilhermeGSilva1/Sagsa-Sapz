
package tarefas02.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tarefas02.api.models.Task;
import tarefas02.api.models.User;
import tarefas02.api.models.enums.ProfileEnum;
import tarefas02.api.models.projections.TaskProjection;
import tarefas02.api.repositories.TaskRepository;
import tarefas02.api.security.UserSpringSecurity;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Tarefa não encontrada! Id: " + id));

        UserSpringSecurity authenticated = UserService.authenticated();

        if (authenticated == null ||
                (!authenticated.hasRole(ProfileEnum.ADMIN)
                        && !Objects.equals(task.getUser().getId(), authenticated.getId()))) {
            throw new tarefas02.api.exceptions.AuthorizationException("Acesso negado.");
        }

        return task;
    }

    public List<TaskProjection> findAllByUserId(Long userId) {
        userService.findById(userId);
        return taskRepository.findByUser_Id(userId);
    }

    @Transactional
    public Task create(Task obj) {
        if (obj.getUser() == null || obj.getUser().getId() == null) {
            throw new ObjectNotFoundException("Usuário da tarefa não informado.");
        }

        User user = userService.findById(obj.getUser().getId());

        obj.setId(null);
        obj.setUser(user);

        return taskRepository.save(obj);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        taskRepository.deleteById(id);
    }
}
