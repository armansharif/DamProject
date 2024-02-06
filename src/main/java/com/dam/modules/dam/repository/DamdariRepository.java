package com.dam.modules.dam.repository;


import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.Damdari;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamdariRepository extends JpaRepository<Damdari,Long> {

    List<Damdari> findAll(Specification<Damdari> spec, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from damdari ")
    List<Damdari> findAllDamdari(Pageable pageable);

    Damdari findDamdariById(Long id);


}
