package br.com.fiap.study_apir.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.fiap.study_apir.model.Produto;

// deixamos component pois ela só é uma classe que usamos para mapear dados (não chama interface, não fala com banco de dados, etc)
@Component
public class ProdutoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public Produto toModel(ProdutoCreateRequest dto){
        return modelMapper.map(dto, Produto.class);
    }


public ProdutoResponse toDto(Produto entity){
    // mpetodo que quando batermos na base de dados - Service retorna model/entity e converter para a classe DTO
    return modelMapper.map(entity, ProdutoResponse.class);
}

public Produto toModel(Long id, ProdutoUpdateRequest dto){
    // pega o dto e faz a conversão 
    Produto produto = modelMapper.map(dto, Produto.class);
    produto.setId(id);
    return produto;
}
}