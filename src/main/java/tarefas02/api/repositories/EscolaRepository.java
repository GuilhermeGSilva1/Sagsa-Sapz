package tarefas02.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tarefas02.api.models.Escola;

@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long> {
}
