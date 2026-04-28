package br.com.fiap.study_apir.controller;

import java.util.List;
import java.util.Optional;

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

import br.com.fiap.study_apir.model.Produto;
import br.com.fiap.study_apir.repository.ProdutoRepository;
import br.com.fiap.study_apir.repository.RepositoryProdutoMockup;


// anotação para informar que é controller
@RestController
@RequestMapping("api/${api.version}/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoRepository repository;

    // vamos instanciar a classe repository para acessar os métodos
    // queremos que a controller use a classe - e dentro dessa variável nós poderemos chamar os métodos
    // vai fazer a injeção automática de dependências
    
   
    // criar método que responda as aplicações - CRUD

    // método POST
    // ao criar uma entidade - estamos colocando que receberá um produto 
    @PostMapping
    public ResponseEntity<Produto> create(@RequestBody Produto produto) {
        repository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(produto)); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        // map vai pegar o dado de um lado (lado do objeto que estamos tratando) > Se o
        // produto existir, no map ele pega o produto e manda para a variável
        return repository
                .findById(id)
                // map já eespera um produto e o ok também - então podemos reduzir o código
                .map(ResponseEntity::ok) // aqui tratamos os tipos de dados - OK
                .orElse(ResponseEntity.notFound().build()); // not found
    }

    // find all
    @GetMapping
    public ResponseEntity<List<Produto>> findAll() {
        repository.findAll();
        return ResponseEntity.ok(repository.findAll());
    }

    // método PUT - tiramos para fazer uma alteração 
    @PutMapping("/{id}")
    public ResponseEntity<Produto> update(@PathVariable Long id, @RequestBody Produto produto) { 
       Optional<Produto> optProduto = repository.findById(id);

       if(optProduto.isPresent()){
        produto.setId(id);
        Produto produtoAlterado = repository.save(produto); // retorna uma entidade
        return ResponseEntity.ok(produtoAlterado);
       } else {
            return ResponseEntity.notFound().build();
       }
    }

    // método DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) { // deixamos Void - ResponseEntity - sempre espera
                                                // uma classe > Deixamos Void - classe que mostra
                                                // que não tem conteúdo a ser retornado
        repository.deleteById(id);
        return ResponseEntity.noContent().build(); 
        // if (mockup.deleteById(id)) {
        //     return ResponseEntity.noContent().build(); // código 204
        // } else {
        //     return ResponseEntity.notFound().build(); // código 404
        // }

    }
}
