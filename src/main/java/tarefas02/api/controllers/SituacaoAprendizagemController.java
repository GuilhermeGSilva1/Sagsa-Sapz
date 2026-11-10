package tarefas02.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tarefas02.api.models.SituacaoAprendizagem;
import tarefas02.api.services.SituacaoAprendizagemService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/situacoes-aprendizagem")
public class SituacaoAprendizagemController {

    @Autowired
    private SituacaoAprendizagemService service;

    @GetMapping
    public ResponseEntity<List<SituacaoAprendizagem>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SituacaoAprendizagem> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody SituacaoAprendizagem sa) {
        SituacaoAprendizagem newObj = service.create(sa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newObj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
