package br.com.fiap.study_apir.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProdutoUpdateRequest {
    private String nome;
    private BigDecimal valor;
}
