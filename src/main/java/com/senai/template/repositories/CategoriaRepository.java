package com.senai.template.repositories;

import com.senai.template.entities.CategoriaEntity;
import com.senai.template.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {

}
