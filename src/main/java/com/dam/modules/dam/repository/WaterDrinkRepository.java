package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.Milking;
import com.dam.modules.dam.model.WaterDrink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WaterDrinkRepository extends JpaRepository<WaterDrink, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM water_drink WHERE DATE(created_at) = :date  and dam_id = :damId ")
    WaterDrink findWaterDrinkByDate(String date, Long damId);
}
