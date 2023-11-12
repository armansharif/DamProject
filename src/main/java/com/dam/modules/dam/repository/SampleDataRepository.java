package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.SampleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleDataRepository extends JpaRepository<SampleData,Long> {
}
