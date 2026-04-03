package br.com.fiap.study_apir.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// @Getter
// @Setter
// // cria construtor para todos os atributos marcados como final 
// @RequiredArgsConstructor

// essa anotação substitui as anteriores 
@Data 
public class Produto {
    private final Long id;
    private final String nome;
    // para valores, usamos BigDecimal 
    private final BigDecimal valor;
    
}
