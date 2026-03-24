package br.com.fiap.study_apir.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.fiap.study_apir.model.Produto;

public class RepositoryProdutoMockup {
// simular um banco de dados da entidade Produto
    private List<Produto> produtos = new ArrayList<>();

    // criando construtor 
    public RepositoryProdutoMockup() {
        Produto produto = new Produto();
        produto.setId(1L); // número 1 com L para interpretar como Long e não como Integer
        produto.setNome("maça");

        produtos.add(produto);
    }

    

    // retornar os produtos que estão na lista
    public List<Produto> findAll(){
        return produtos;
    }

    

    public Produto findById(Long id){
        // pesquisar produto pelo códigoo dele
        for (Produto produto : produtos){ //percorrer a lista de pordutos - atribui na variável
            if (produto.getId().equals(id)) { //pegamos um Id Long (classe) - pois dados primitivos em java sempre tem valor (obrigatoriamente) - comparar o valor do id usamos equals
                return produto; //se for, retorna o produto   
            }
        }
        return null; //se não for   
    }
}
