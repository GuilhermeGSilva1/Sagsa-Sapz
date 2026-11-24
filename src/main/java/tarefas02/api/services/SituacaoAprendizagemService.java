package tarefas02.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tarefas02.api.models.SituacaoAprendizagem;
import tarefas02.api.models.UnidadeCurricular;
import tarefas02.api.models.User;
import tarefas02.api.repositories.SituacaoAprendizagemRepository;
import tarefas02.api.repositories.UnidadeCurricularRepository;
import tarefas02.api.repositories.UserRepository;
import tarefas02.api.security.UserSpringSecurity;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

import java.util.List;

@Service
public class SituacaoAprendizagemService {

    @Autowired
    private SituacaoAprendizagemRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnidadeCurricularRepository unidadeCurricularRepository;

    public List<SituacaoAprendizagem> findAll() {
        return repository.findAll();
    }

    public SituacaoAprendizagem findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Situação de Aprendizagem não encontrada! Id: " + id));
    }

    public SituacaoAprendizagem create(SituacaoAprendizagem sa) {
        sa.setId(null);

        UserSpringSecurity userAuth = UserService.authenticated();
        if (userAuth != null) {
            User instrutor = userRepository.findById(userAuth.getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Instrutor não encontrado com ID: " + userAuth.getId()));
            sa.setInstrutor(instrutor);
        } else if (sa.getInstrutor() == null || sa.getInstrutor().getId() == null) {
            User instrutorPadrao = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException("Nenhum usuário cadastrado como instrutor no banco."));
            sa.setInstrutor(instrutorPadrao);
        }

        Long ucId = (sa.getUnidadeCurricular() != null && sa.getUnidadeCurricular().getId() != null)
                ? sa.getUnidadeCurricular().getId()
                : 1L;
        UnidadeCurricular uc = unidadeCurricularRepository.findById(ucId)
                .orElseThrow(() -> new ObjectNotFoundException("Unidade Curricular não encontrada com ID: " + ucId));
        sa.setUnidadeCurricular(uc);

        if (sa.getCompartilhada() == null) {
            sa.setCompartilhada(false);
        }

        return repository.save(sa);
    }

    public SituacaoAprendizagem update(Long id, SituacaoAprendizagem saAtualizada) {
        SituacaoAprendizagem existente = findById(id);

        existente.setTitulo(saAtualizada.getTitulo());
        existente.setTipoEstrategia(saAtualizada.getTipoEstrategia());
        existente.setCargaHorariaSa(saAtualizada.getCargaHorariaSa());
        existente.setContextualizacao(saAtualizada.getContextualizacao());
        existente.setDesafio(saAtualizada.getDesafio());
        existente.setResultadosEsperados(saAtualizada.getResultadosEsperados());

        if (saAtualizada.getCompartilhada() != null) {
            existente.setCompartilhada(saAtualizada.getCompartilhada());
        }

        if (saAtualizada.getUnidadeCurricular() != null && saAtualizada.getUnidadeCurricular().getId() != null) {
            UnidadeCurricular uc = unidadeCurricularRepository.findById(saAtualizada.getUnidadeCurricular().getId())
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "Unidade Curricular não encontrada com ID: " + saAtualizada.getUnidadeCurricular().getId()));
            existente.setUnidadeCurricular(uc);
        }

        return repository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}