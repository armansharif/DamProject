package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource,Long> {
}
