package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.DamStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DamStatusRepository extends JpaRepository<DamStatus,Long> {


    @Query(nativeQuery = true, value = "select * from dam_status where dam_id =:damId ")
    List<DamStatus> findAllDamStatus(@Param("damId") String damId, Pageable pageable);

    @Query(nativeQuery = true, value = "select gps from dam_status where 1=1 and " +
            " (" +
            " ( :damId is null or  damId =:damId)  OR " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )   " +
            " )")
    List<String> findAllDamGps(@Param("damId") String damId,@Param("damdariId") String damdariId, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam_status where  id = (select MAX(id) from dam_status where dam_id =:damId )")
     DamStatus   findLastDamStatus(@Param("damId") String damId );

}
