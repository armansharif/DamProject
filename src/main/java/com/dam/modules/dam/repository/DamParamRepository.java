package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.DamParam;
import com.dam.modules.dam.model.Damdari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DamParamRepository extends JpaRepository<DamParam,Long> {

    List<DamParam> findDamParamByDamdari(Damdari damdari);
}
