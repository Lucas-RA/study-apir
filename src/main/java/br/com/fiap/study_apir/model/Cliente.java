package br.com.fiap.study_apir.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Cliente {
    @Id
    private Long id;
    private String nome;


    public Cliente() {}

}
