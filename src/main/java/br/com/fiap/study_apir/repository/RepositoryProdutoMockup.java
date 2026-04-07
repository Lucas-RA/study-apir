package br.com.fiap.study_apir.repository;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.fiap.study_apir.model.Produto;
// anotação para não precisarmos criar a instância em memória - Spring vai realizar automaticamente 
@Service
public class RepositoryProdutoMockup {
// simular um banco de dados da entidade Produto
    private List<Produto> produtos = new ArrayList<>();
    // controle artificial para o id -> Controlador de id
    private long ID = 1L;

    // criando construtor 
    public RepositoryProdutoMockup() {
        // Inserindo o valor na lista de produtos
        produtos.add(new Produto(ID++, "maça", BigDecimal.valueOf(10.50)));
        produtos.add(new Produto(ID++, "uva", BigDecimal.valueOf(15.25)));
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

    // fluxo de qualquer entidade
    public Produto create(Produto produto) {
        // atribuir o id novo aoo produto oa ser cadastrado 
            // tiramos a geração - deixamos diretamente aqui 
        produto.setId(ID++);
        System.out.println(produto.getNome());
        // salvar no BD (lista de produtos)
        produtos.add(produto);
        // retornar o produto novo 
        return produto;    
    }

    public boolean update(Long id, Produto produto){
        Optional<Produto> optProduto = this.findById(id);

        if (optProduto.isPresent()) {
            // encontrou
            Produto produtoAtual = optProduto.get();
            produtoAtual.setNome(produto.getNome());
            produtoAtual.setValor(produto.getValor());
            return true;
        }
        return false;
    }
}
