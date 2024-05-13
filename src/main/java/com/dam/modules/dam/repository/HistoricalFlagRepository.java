package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.HistoricalFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricalFlagRepository extends JpaRepository<HistoricalFlag,Long> {

    @Query(nativeQuery = true, value = "select * from historical_flag where dam_id =:damId     ")
    List<HistoricalFlag> findAllByDam(@Param("damId") Long damId);
}
