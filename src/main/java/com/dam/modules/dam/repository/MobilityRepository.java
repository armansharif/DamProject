package com.dam.modules.dam.repository;

import com.dam.modules.dam.model.DamStatus;
import com.dam.modules.dam.model.Mobility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MobilityRepository extends JpaRepository <Mobility,Long>{

    @Query(nativeQuery = true, value = "select count(*) from mobility where date =:date and dam_id = :damId    ")
    Long existsMobilityInDate(String date,Long damId);

//    @Query(nativeQuery = true, value = " " +
//            " ( SELECT  created_at `date` , gastric_momentum gastric FROM dam_status WHERE  created_at <= DATE_SUB(NOW(), INTERVAL 24 HOUR) and  gastric_momentum  is not null   and dam_id=:damId order by created_at desc limit 1) " +
//            " UNION ALL " +
//            " (SELECT  created_at  `date`, gastric_momentum gastric FROM dam_status WHERE   gastric_momentum  is not null   and dam_id=:damId  order by created_at desc limit 1) ")
//    List<MobilityDto> getGastricMomentum(Long damId);

    @Query(nativeQuery = true, value =
            " SELECT  created_at  `date`, gastric_momentum gastric FROM dam_status WHERE gastric_momentum  is not null and dam_id=:damId " +
                    " order by created_at DESC  limit 2 ")
    List<MobilityDto> getGastricMomentum(Long damId);

    @Query(nativeQuery = true, value = "select * from mobility where  id = (select MAX(id) from mobility where dam_id =:damId )")
    Mobility findLastMobility(@Param("damId") String damId );


}
