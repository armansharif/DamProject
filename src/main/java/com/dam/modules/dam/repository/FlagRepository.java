package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface FlagRepository extends JpaRepository<Flag,Long> {
}
