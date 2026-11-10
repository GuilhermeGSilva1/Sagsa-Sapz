package tarefas02.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarefas02.api.models.SituacaoAprendizagem;

@Repository
public interface SituacaoAprendizagemRepository extends JpaRepository<SituacaoAprendizagem, Long> {
}
