package br.com.fiap.study_apir.repository;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.fiap.study_apir.model.Produto;

public class RepositoryProdutoMockup {
// simular um banco de dados da entidade Produto
    private List<Produto> produtos = new ArrayList<>();

    // criando construtor 
    public RepositoryProdutoMockup() {
        // Inserindo o valor na lista de produtos
        produtos.add(new Produto(1L, "maça", BigDecimal.valueOf(10.50)));
        produtos.add(new Produto(3L, "uva", BigDecimal.valueOf(15.25)));
    }

    // retornar os produtos que estão na lista
    public List<Produto> findAll(){
        return produtos;
    }

    
// não queremos que retorne produto - queremos que possa retornar um produto
    public Optional<Produto> findById(Long id){
        return produtos.stream()
        .filter(p -> p.getId().equals(id))
        .findFirst();
    }

    public boolean deleteById(Long id){
        return produtos.removeIf(p -> p.getId().equals(id));
    }
}
