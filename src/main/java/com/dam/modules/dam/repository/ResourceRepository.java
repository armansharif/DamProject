package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.model.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM resource WHERE DATE(created_at) = :date and type_id=:typeId ")
    Resource findResourceByDate(String date ,Long typeId);
}
