package com.senai.template.services;

import com.senai.template.dtos.CategoriaDto;
import com.senai.template.entities.CategoriaEntity;
import com.senai.template.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDto> obterCategorias() {
        List<CategoriaEntity> categoriaEntities = categoriaRepository.findAll();

        List<CategoriaDto> categoriaDtos = new ArrayList<>();

        for (CategoriaEntity categoriaEntity : categoriaEntities) {
            categoriaDtos.add(converterEntityParaDto(categoriaEntity));
        }

        return categoriaDtos;
    }

    public CategoriaDto obterCategoriaPorId(Long id) {
        Optional<CategoriaEntity> categoriaEntityOptional = categoriaRepository.findById(id);

        if (categoriaEntityOptional.isEmpty()) {
            return null;
        }

        return converterEntityParaDto(categoriaEntityOptional.get());
    }

    public CategoriaDto cadastrarCategoria(CategoriaDto categoriaDto) {
        CategoriaEntity categoriaEntity = converterDtoParaEntity(categoriaDto);

        categoriaEntity = categoriaRepository.save(categoriaEntity);

        return converterEntityParaDto(categoriaEntity);
    }

    public CategoriaDto atualizarCategoria(Long id, CategoriaDto categoriaDto) {
        Optional<CategoriaEntity> categoriaEntityOptional = categoriaRepository.findById(id);

        if (categoriaEntityOptional.isEmpty()) {
            return null;
        }

        CategoriaEntity categoriaEntity = categoriaEntityOptional.get();

        categoriaEntity.setNome(categoriaDto.getNome());

        categoriaEntity = categoriaRepository.save(categoriaEntity);

        return converterEntityParaDto(categoriaEntity);
    }

    public boolean excluirCategoria(Long id) {
        Optional<CategoriaEntity> categoriaEntityOptional = categoriaRepository.findById(id);

        if (categoriaEntityOptional.isEmpty()) {
            return false;
        }

        categoriaRepository.deleteById(id);

        return true;
    }

    private CategoriaEntity converterDtoParaEntity(CategoriaDto categoriaDto) {
        CategoriaEntity categoriaEntity = new CategoriaEntity();

        categoriaEntity.setId(categoriaDto.getId());
        categoriaEntity.setNome(categoriaDto.getNome());

        return categoriaEntity;
    }

    private CategoriaDto converterEntityParaDto(CategoriaEntity categoriaEntity) {
        CategoriaDto categoriaDto = new CategoriaDto();

        categoriaDto.setId(categoriaEntity.getId());
        categoriaDto.setNome(categoriaEntity.getNome());

        return categoriaDto;
    }
}
