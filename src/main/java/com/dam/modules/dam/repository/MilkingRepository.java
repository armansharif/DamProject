package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Milking;
import com.dam.modules.dam.model.Resource;
import liquibase.pro.packaged.L;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MilkingRepository extends JpaRepository<Milking, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM milking WHERE DATE(created_at) = :date ")
    Milking findMilkingByDate(String date );
}
