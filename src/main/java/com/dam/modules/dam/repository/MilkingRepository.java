package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Milking;
import liquibase.pro.packaged.L;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilkingRepository extends JpaRepository<Milking, Long> {

}
