package br.com.fiap.study_apir.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    private Long id;
    @Column(name = "nome_cliente", length = 100, nullable = false, columnDefinition = "char(100)")
    private String nome;

    public Cliente() {}
}
