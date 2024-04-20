package com.dam.modules.dam.repository;


import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.ImpDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImpDateRepository extends JpaRepository<ImpDate, Long> {
    List<ImpDate> findAllByDam(Dam dam, Pageable pageable);
}
