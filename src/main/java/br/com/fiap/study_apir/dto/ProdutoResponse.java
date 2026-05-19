package br.com.fiap.study_apir.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProdutoResponse {
    private Long id;
    private String nome;
    private BigDecimal valor;
}
