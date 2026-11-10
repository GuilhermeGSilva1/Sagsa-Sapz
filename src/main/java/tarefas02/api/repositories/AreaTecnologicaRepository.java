package tarefas02.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarefas02.api.models.AreaTecnologica;

@Repository
public interface AreaTecnologicaRepository extends JpaRepository<AreaTecnologica, Long> {
}
