package com.senai.template.repositories;

import com.senai.template.entities.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEntity, Long> {

    List<MovimentacaoEntity> findAllByOrderByDataDesc();

    List<MovimentacaoEntity> findByProdutoIdOrderByDataDesc(Long produtoId);

    boolean existsByProdutoId(Long produtoId);
}
