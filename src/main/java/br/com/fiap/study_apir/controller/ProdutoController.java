package br.com.fiap.study_apir.controller;

import java.util.List;

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

import br.com.fiap.study_apir.model.Produto;
import br.com.fiap.study_apir.repository.RepositoryProdutoMockup;

// anotação para informar que é controller
@RestController
@RequestMapping("api/${api.version}/produtos")
public class ProdutoController {

    // vamos instanciar a classe repository para acessar os métodos
    // queremos que a controller use a classe - e dentro dessa variável nós poderemos chamar os métodos
    private RepositoryProdutoMockup mockup = new RepositoryProdutoMockup();
    // criar método que responda as aplicações - CRUD
    
    // método POST
    // colocamos o responseEntity
    @PostMapping
    public ResponseEntity<String> create(){
        return  ResponseEntity.status(HttpStatus.CREATED).body("Produto Criado"); //body é o texto que vamos retornar 
    }

    // método é GET
    // vamos alterar - recebe um id de produto >> Se recebermos 
    @GetMapping("/{id}") 
    public ResponseEntity<Produto> findById(@PathVariable Long id){
        // colocamos o método - passamos o id que o método tá esperando 
        // vai retornar um produto - então criamos a variável produto 
        Produto produto =  mockup.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(produto); // não retorna mais um texto, retorna o objeto produto
    }

    // find all
    @GetMapping
    public ResponseEntity<List<Produto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(mockup.findAll());            
    }

    // método PUT
    @PutMapping
    public ResponseEntity<String> update(){
        return ResponseEntity.status(HttpStatus.OK).body("Produto Atualizado");
    }
    

    // método DELETE
    @DeleteMapping
    public ResponseEntity<String> delete(){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Produo Excluído");
    }
}
