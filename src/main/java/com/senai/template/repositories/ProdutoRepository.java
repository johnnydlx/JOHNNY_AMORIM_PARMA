package com.senai.template.repositories;

import com.senai.template.entities.ProdutoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    @Query("SELECT p FROM ProdutoEntity p " +
           "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:inicio IS NULL OR p.dataCadastro >= :inicio) " +
           "AND (:fim IS NULL OR p.dataCadastro <= :fim) " +
           "ORDER BY p.nome ASC")
    Page<ProdutoEntity> pesquisar(@Param("nome") String nome,
                                  @Param("inicio") LocalDateTime inicio,
                                  @Param("fim") LocalDateTime fim,
                                  Pageable pageable);
}
