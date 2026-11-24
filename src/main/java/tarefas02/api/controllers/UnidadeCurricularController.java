package tarefas02.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tarefas02.api.models.UnidadeCurricular;
import tarefas02.api.repositories.UnidadeCurricularRepository;

import java.util.List;

@RestController
@RequestMapping("/unidades-curriculares")
public class UnidadeCurricularController {

    @Autowired
    private UnidadeCurricularRepository repository;

    @GetMapping
    public ResponseEntity<List<UnidadeCurricular>> findAll() {
        return ResponseEntity.ok().body(repository.findAll());
    }
}