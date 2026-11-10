
package tarefas02.api.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tarefas02.api.models.*;
import tarefas02.api.repositories.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private EscolaRepository escolaRepository;

    @Autowired
    private EixoTecnologicoRepository eixoTecnologicoRepository;

    @Autowired
    private AreaTecnologicaRepository areaTecnologicaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PlanoCursoRepository planoCursoRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private UnidadeCurricularRepository unidadeCurricularRepository;

    @Override
    public void run(String... args) throws Exception {
        if (perfilRepository.count() == 0) {
            perfilRepository.save(new Perfil(null, "ADMINISTRADOR", "Acesso total ao sistema"));
            perfilRepository.save(new Perfil(null, "COORDENADOR", "Gestão de cursos e turmas"));
            perfilRepository.save(new Perfil(null, "INSTRUTOR", "Criação e gestão de SAPZ"));
        }

        if (escolaRepository.count() == 0) {
            escolaRepository.save(new Escola(null, "SENAI São Leopoldo", "UN-01", "São Leopoldo", "RS", true));
        }

        if (eixoTecnologicoRepository.count() == 0) {
            EixoTecnologico eixo = eixoTecnologicoRepository.save(
                new EixoTecnologico(null, "Informação e Comunicação")
            );

            AreaTecnologica area = areaTecnologicaRepository.save(
                new AreaTecnologica(null, eixo, "Tecnologia da Informação")
            );

            Curso curso = cursoRepository.save(
                new Curso(null, area, "Técnico em Desenvolvimento de Sistemas", "HABILITACAO_TECNICA", true)
            );

            PlanoCurso plano = planoCursoRepository.save(
                new PlanoCurso(null, curso, "PC-2026", "1.0", 1200, null, true)
            );

            Modulo modulo = moduloRepository.save(
                new Modulo(null, plano, "Módulo Específico", "ESPECIFICO", 1, 400)
            );

            UnidadeCurricular uc = new UnidadeCurricular();
            uc.setModulo(modulo);
            uc.setNome("Desenvolvimento Web e API REST");
            uc.setCargaHoraria(80);
            uc.setObjetivoGeral("Desenvolvimento de Aplicações Web Fullstack com Spring Boot e Javascript");
            uc.setOrdem(1);

            unidadeCurricularRepository.save(uc);
        }
    }
}
