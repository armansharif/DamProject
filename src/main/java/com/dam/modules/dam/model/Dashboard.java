package com.dam.modules.dam.model;

import lombok.Data;

import java.util.List;

@Data
public class Dashboard {



//    private Long countOfTicket;
//    private Long countOfDamdari;
//    private Long countOfDam;
//    private Long countOfNeedCare;
//    private Long countOfDamWithTab;
//    private Long countOfDamIsFahli;
//    private Long countOfDamHasLangesh;
//    private Long avgOfMilk;
 //   private Map<String, List<ChartDto>> charts;
    private List<DynamicInfos> infos;
 //   private Map<String, DynamicCharts> charts;

    private List<DynamicCharts> charts;
}
