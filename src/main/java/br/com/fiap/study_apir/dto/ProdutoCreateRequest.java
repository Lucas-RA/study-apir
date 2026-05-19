package br.com.fiap.study_apir.dto;

import java.math.BigDecimal;

import br.com.fiap.study_apir.model.Produto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProdutoCreateRequest {
    // não precisamos do id pois a camada de persistência (Banco de Dados) vai criar
    @NotNull
    @Size(min = 3 ,message = "Nome de produto deve ter no mínimo 3 caracteres")
    private String nome;
    @Positive(message = "O valor do produto deve ser positivo")
    private BigDecimal valor;
}

