package tarefas02.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarefas02.api.models.Task;
import tarefas02.api.models.projections.TaskProjection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<TaskProjection> findByUser_Id(Long userId);
}
