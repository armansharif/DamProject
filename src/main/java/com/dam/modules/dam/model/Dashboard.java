package com.dam.modules.dam.model;

import com.dam.modules.dam.repository.ChartDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Dashboard {

    private Long countOfTicket;
    private Long countOfDamdari;
    private Long countOfDam;
    private Long countOfDamWithTab;
    private Long countOfDamIsFahli;
    private Long countOfDamHasLangesh;
    private Long avgOfMilk;
 //   private Map<String, List<ChartDto>> charts;

    private Map<String, DynamicCharts> charts;
}
