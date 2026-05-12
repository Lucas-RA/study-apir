package br.com.fiap.study_apir.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    // para valores, usamos BigDecimal
    private BigDecimal valor;

    // adicionamos para ajustar um erro no repositório
    public Produto(Long id, String nome, BigDecimal valor) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
    }

    // construtor sem propriedades 
    public Produto() {
    }

    
}
