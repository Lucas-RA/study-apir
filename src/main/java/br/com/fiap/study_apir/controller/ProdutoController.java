package br.com.fiap.study_apir.controller;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import br.com.fiap.study_apir.dto.ProdutoCreateRequest;
import br.com.fiap.study_apir.dto.ProdutoMapper;
import br.com.fiap.study_apir.dto.ProdutoResponse;
import br.com.fiap.study_apir.dto.ProdutoUpdateRequest;
import br.com.fiap.study_apir.model.Produto;
import br.com.fiap.study_apir.repository.ProdutoRepository;
import br.com.fiap.study_apir.repository.RepositoryProdutoMockup;

import br.com.fiap.study_apir.service.ProdutoService;
import jakarta.validation.Valid;

// anotação para informar que é controller
@RestController
@RequestMapping("api/${api.version}/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    // nova injeção
    @Autowired
    private ProdutoMapper produtoMapper;

    // criar método que responda as aplicações - CRUD

    // método POST
    @PostMapping
    public ResponseEntity<ProdutoResponse> create(@Valid @RequestBody ProdutoCreateRequest dtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produtoMapper.toDto(service.createOrUpdate(produtoMapper.toModel(dtoRequest))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> findById(@PathVariable Long id) {
        return service
                .findById(id)
                // precisamos de um map adicional - vamos usar o mapper que vai converter o
                // produto para um DTO
                .map(produto -> produtoMapper.toDto(produto))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // find all
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll()
                .stream()
                // aqui fazemos a conversão - cria uma nova lista em memória (de DTOs)
                .map(produto -> produtoMapper.toDto(produto))
                .toList() // colocamos pois o map não é final - é intermediário
        );
    }

    // método PUT
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> update(@PathVariable Long id,
            @RequestBody @Valid ProdutoUpdateRequest dtoRequest) {

        if (service.findById(id).isPresent()) {
            Produto produto = produtoMapper.toModel(id, dtoRequest);
            produto.setId(id);
            return ResponseEntity.ok(produtoMapper.toDto(service.createOrUpdate(produto)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // método DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
