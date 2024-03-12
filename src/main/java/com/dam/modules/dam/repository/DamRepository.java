package com.dam.modules.dam.repository;

import com.dam.commons.Consts;
import com.dam.modules.dam.model.Dam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DamRepository extends JpaRepository<Dam, Long> {

    String fullDateStringInAlies = " CONCAT(pyear(created_at),'-',LPAD(PMONTH(created_at), 2, '0'),'-', LPAD(pday(created_at), 2, '0')) ";
    String fullDateTimeStringInAlies = "  CONCAT(pyear(created_at),'-',LPAD(PMONTH(created_at), 2, '0'),'-', LPAD(pday(created_at), 2, '0'), ' ',TIME(created_at) ) ";
    String yearMonthDateStringInAlies = " CONCAT(pyear(created_at),'-',LPAD(PMONTH(created_at), 2, '0')) ";


    String fullDateStringOrder = " order by created_at   ";
    String yearMonthDateStringOrder = " order by created_at   ";

    String fromDateToDateWhereCondition = " AND ( :fromDate is null or  pdate(created_at) >= :fromDate)     " +
            " AND ( :toDate is null or  pdate(created_at) <= :toDate)  ";

    @Query(nativeQuery = true, value = "select * from dam ")
    List<Dam> getDams(Pageable pageable);

    Optional<Dam> findDamByDeviceId(String deviceId);

    List<Dam> findAll(Specification<Dam> spec, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam ")
    List<Dam> findDamByIsFahliOrhasOrHasLangesh(int isFahli, int hasLangesh, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam where id =:damId     ")
    Optional<Dam> findDam(@Param("damId") String damId);

    @Query(nativeQuery = true, value = "select * from dam  where damdari_id =:damdariId  and  " +
            "( id in ( select dam_id from dam_flag where flag_id in ( :flags ) )  ) and   " +
            "( :typeId is null or  type_id = :typeId )   " +
            " ")
    List<Dam> findDamsOfDamdariByFlag(@Param("damdariId") String damdariId, @Param("flags") List<String> flags,  @Param("typeId") String typeId, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam  where damdari_id =:damdariId  and  " +
            "( :typeId is null or  type_id = :typeId )   " +
            " ")
    List<Dam> findDamsOfDamdari(@Param("damdariId") String damdariId ,  @Param("typeId") String typeId, Pageable pageable);

    @Query(nativeQuery = true, value = "select count(1) from  dam d , dam_flag df ,flag f  WHERE " +
            "  d.id = df.dam_id AND df.flag_id = f.id AND " +
            "( :damdariId is null or  damdari_id =:damdariId) and f.id in ( :flags )     " +
            " ")
    Long countOf(@Param("damdariId") String damdariId, @Param("flags") List<String> flags);

    @Query(nativeQuery = true, value = "select count(1) from dam   WHERE is_fahli = 1  and " +
            "( :damdariId is null or  damdari_id =:damdariId)     " +
            " ")
    Long countOfFahliDams(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select count(1) from dam   WHERE has_langesh = 1 and " +
            "( :damdariId is null or  damdari_id =:damdariId)     " +
            " ")
    Long countOfLangeshDams(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select count(1) from dam   WHERE 1=1 and " +
            "( :damdariId is null or  damdari_id =:damdariId) and   " +
            "  has_tab=1 " +
            " ")
    Long countOfDamWithTab(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select count(1) from dam   WHERE 1=1 and " +
            "( :damdariId is null or  damdari_id =:damdariId)   " +
            " ")
    Long countOfDam(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select count(1) from damdari " +
            //   "  WHERE 1=1 and " +
            // "( :userId is null or  user_id =:userId )   " +
            " ")
    Long countOfDamdari(@Param("userId") String userId);

    @Query(nativeQuery = true, value = "select AVG(liter)  ang from milking   WHERE 1=1  and  " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     ")
    Long avgOfMilk(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select AVG(ph)  avg from dam_status   WHERE 1=1  and  " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     ")
    Long avgOfPH(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = "select  (select name from type where id=type_id), count(*) from dam WHERE 1=1 and " +
            "( :damdariId is null or  damdari_id =:damdariId)     " +
            "  group by type_id ")
    List<Object[]> typeOfDam(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select  (select name from type where id=type_id) AS title, count(*) AS value from dam WHERE 1=1 and " +
            "( :damdariId is null or  damdari_id =:damdariId)     " +
            "  group by type_id ")
    List<ChartDto> typeOfDamChartDto(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = "SELECT (SELECT NAME FROM city WHERE id=city_id) AS title ,COUNT(*) AS value " +
            " FROM dam d , damdari dm  " +
            " WHERE d.damdari_id = dm.id AND " +
            " ( :damdariId is null or  damdari_id =:damdariId)     " +
            " GROUP BY city_id " +
            " ")
    List<ChartDto> locationOfDamChartDto(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select   " + yearMonthDateStringInAlies + "  AS title, SUM(amount) AS value " +
            "  from resource WHERE 1=1 and type = " + Consts.TYPE_OF_RESOURCE_FODDER + " and " +
            " ( :damdariId is null or  damdari_id =:damdariId)     "
            + fromDateToDateWhereCondition +
            "   GROUP BY   " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> amountOfFodderChartDto(@Param("damdariId") String damdariId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select   " + yearMonthDateStringInAlies + "  AS title, SUM(amount) AS value " +
            "  from resource WHERE 1=1 and type = " + Consts.TYPE_OF_RESOURCE_WATER + " and " +
            " ( :damdariId is null or  damdari_id =:damdariId)     " +
            fromDateToDateWhereCondition +
            "   GROUP BY   " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> amountOfWaterChartDto(@Param("damdariId") String damdariId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select " + fullDateStringInAlies + "  AS title, SUM(liter) AS value " +
            "  from milking WHERE 1=1 and " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     " +
            fromDateToDateWhereCondition +
            "   GROUP BY pday(created_at),  " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            fullDateStringOrder)
    List<ChartDto> amountOfMilkingChartDto(@Param("damdariId") String damdariId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select " + fullDateStringInAlies + "  AS title, ROUND(AVG(ph),2)  AS value " +
            "  from dam_status WHERE 1=1 and ph IS NOT NULL and " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     " +
            fromDateToDateWhereCondition +
            "   GROUP BY pday(created_at),  " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            fullDateStringOrder)
    List<ChartDto> avgOfPhChartDto(@Param("damdariId") String damdariId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select " + fullDateTimeStringInAlies + "  AS title, ph  AS value " +
            "  from dam_status WHERE 1=1 and ph IS NOT NULL and " +
            " ( :damId is null or  dam_id =:damId)     " +
            fromDateToDateWhereCondition +
            fullDateStringOrder)
    List<ChartDto> phOfDamChartDto(@Param("damId") String damId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);


    @Query(nativeQuery = true, value = "select " + fullDateTimeStringInAlies + "  AS title, temperature  AS value " +
            "  from dam_status WHERE 1=1 and temperature IS NOT NULL and" +
            " ( :damId is null or  dam_id =:damId)     " +
            fromDateToDateWhereCondition +
            fullDateStringOrder)
    List<ChartDto> temperatureOfDamChartDto(@Param("damId") String damId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select " + fullDateStringInAlies + "  AS title,ROUND(  AVG(temperature) ,2) AS value " +
            "  from dam_status WHERE 1=1 and temperature IS NOT NULL and" +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     " +
            fromDateToDateWhereCondition +
            "   GROUP BY pday(created_at),  " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            fullDateStringOrder)
    List<ChartDto> avgOfTemperatureChartDto(@Param("damdariId") String damdariId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select   " + yearMonthDateStringInAlies + "    AS title, count(*) AS value " +
            "  from dam WHERE 1=1 and " +
            " ( :damdariId is null or  damdari_id =:damdariId)     " +
            "   GROUP BY   " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> countDateOfDamChartDto(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = "SELECT    " + yearMonthDateStringInAlies + "   AS title, COUNT(*) AS value  " +
            "    FROM dam d, " +
            "         (  SELECT dam_id, datetime,id " +
            "              FROM dam_status " +
            "             WHERE temperature > 37 " +
            "          GROUP BY dam_id, " +
            "                   pday(datetime), " +
            "                   PMONTH(datetime), " +
            "                   pyear(datetime)) ds " +
            "   WHERE d.id = ds.dam_id  and ( :damdariId is null or  damdari_id =:damdariId)   " +
            "GROUP BY PMONTH(ds.datetime),pyear(ds.datetime) " +
            yearMonthDateStringOrder)
    List<ChartDto> compareTabOverMonthChartDto(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = " select * " +
            " from  dam where id in (  " +
            "       select  id from dam d  where 1=1 and " +
            "       ( :damdariId is null or  d.damdari_id = :damdariId ) and " +
            "       ( :typeId is null or  d.type_id = :typeId )  and " +
            "       ( " +
            "       ( :isFahli is null or  d.is_fahli = :isFahli )   OR  " +
            "       ( :hasLangesh is null or  d.has_langesh = :hasLangesh ) OR " +
            "       ( :hasTab is null or  d.has_tab = :hasTab)  " +
            "       ) " +
            " )   "
    )
    List<Dam> findDamsOfDamdariHasProblem(@Param("damdariId") String damdariId, @Param("isFahli") String isFahli, @Param("hasLangesh") String hasLangesh, @Param("hasTab") String hasTab, @Param("typeId") String typeId, Pageable pageable);


    @Query(nativeQuery = true, value = "select  " + yearMonthDateStringInAlies + "   AS title, count(distinct dam_id) AS value " +
            "  from historical_tab WHERE 1=1 and " +
            " ( :damdariId is null or ( dam_id IN  (select id from dam where  damdari_id = :damdariId ) ) )     " +
            "   GROUP BY    " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> historicalTabOfDamChartDto(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = "select    " + yearMonthDateStringInAlies + "    AS title, count(distinct dam_id) AS value " +
            "  from historical_langesh WHERE 1=1 and " +
            " ( :damdariId is null or ( dam_id IN  (select id from dam where  damdari_id = :damdariId ) ) )     " +
            "   GROUP BY    " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> historicalLangechOfDamChartDto(@Param("damdariId") String damdariId);

    @Query(nativeQuery = true, value = "select    " + yearMonthDateStringInAlies + "  AS title, count(distinct dam_id) AS value " +
            "  from historical_fahli WHERE 1=1 and " +
            " ( :damdariId is null or ( dam_id IN  (select id from dam where  damdari_id = :damdariId ) ) )     " +
            "   GROUP BY    " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> historicalFahliOfDamChartDto(@Param("damdariId") String damdariId);

    List<Dam> findDamsByFlagIn(List list);

    Long countDamsByFlagIn(List list);

}
