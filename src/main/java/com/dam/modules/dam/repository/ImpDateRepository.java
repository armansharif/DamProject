package com.dam.modules.dam.repository;


import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.ImpDate;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImpDateRepository extends JpaRepository<ImpDate, Long> {
    List<ImpDate> findAllByDam(Dam dam, Pageable pageable);

    @Query(nativeQuery = true, value = " SELECT * FROM imp_date imp0 " +
            " WHERE created_at = ( select MAX(created_at) from imp_date imp1  where imp1.type_id =imp0.type_id AND dam_id =:damId ) " +
            " GROUP BY type_id " +
            " ORDER BY type_id ")
    List<ImpDate>  findLastImpDate(@Param("damId") Long damId );
}
