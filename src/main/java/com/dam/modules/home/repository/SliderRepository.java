package com.dam.modules.home.repository;

import com.dam.modules.home.model.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {
    @Override
    List<Slider> findAll();
}
