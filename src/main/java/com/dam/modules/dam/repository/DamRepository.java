package com.dam.modules.dam.repository;

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
    String yearMonthDateStringInAlies = " CONCAT(pyear(created_at),'-',LPAD(PMONTH(created_at), 2, '0')) ";


    String fullDateStringOrder = " order by pyear(created_at) DESC, PMONTH(created_at) DESC, pday(created_at) DESC ";
    String yearMonthDateStringOrder = " order by pyear(created_at) DESC, PMONTH(created_at) DESC  ";

    @Query(nativeQuery = true, value = "select * from dam ")
    List<Dam> getDams(Pageable pageable);

    List<Dam> findAll(Specification<Dam> spec, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam ")
    List<Dam> findDamByIsFahliOrhasOrHasLangesh(int isFahli, int hasLangesh, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from dam where id =:damId     ")
    Optional<Dam> findDam(@Param("damId") String damId);

    @Query(nativeQuery = true, value = "select * from dam  where damdari_id =:damdariId  and  " +
            "( :isFahli is null or  is_fahli = :isFahli ) and   " +
            "( :hasLangesh is null or  has_langesh = :hasLangesh ) and   " +
            "( :typeId is null or  type_id = :typeId )   " +
            " ")
    List<Dam> findDamsOfDamdari(@Param("damdariId") String damdariId, @Param("isFahli") String isFahli, @Param("hasLangesh") String hasLangesh, @Param("typeId") String typeId, Pageable pageable);


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

    @Query(nativeQuery = true, value = "select SUM(liter)/count(*) ang from milking   WHERE 1=1  and  " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     ")
    Long avgOfMilk(@Param("damdariId") String damdariId);


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
            "  from fodder WHERE 1=1 and " +
            " ( :damdariId is null or  damdari_id =:damdariId)     " +
            "   GROUP BY   " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            yearMonthDateStringOrder)
    List<ChartDto> amountOfFodderChartDto(@Param("damdariId") String damdariId);


    @Query(nativeQuery = true, value = "select " + fullDateStringInAlies + "  AS title, SUM(liter) AS value " +
            "  from milking WHERE 1=1 and " +
            " ( :damdariId is null or (dam_id In  (select id from dam where  damdari_id =:damdariId ) ) )     " +
            "   GROUP BY pday(created_at),  " +
            "        PMONTH(created_at), " +
            "        pyear(created_at)" +
            fullDateStringOrder)
    List<ChartDto> amountOfMilkingChartDto(@Param("damdariId") String damdariId);

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
}
