package tarefas02.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarefas02.api.models.PlanoCurso;

@Repository
public interface PlanoCursoRepository extends JpaRepository<PlanoCurso, Long> {
}
