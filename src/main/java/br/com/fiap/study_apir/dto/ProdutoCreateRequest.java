package br.com.fiap.study_apir.dto;

import java.math.BigDecimal;

import br.com.fiap.study_apir.model.Produto;
import lombok.Data;

@Data
public class ProdutoCreateRequest {
    private String nome;
    private BigDecimal valor;
}

