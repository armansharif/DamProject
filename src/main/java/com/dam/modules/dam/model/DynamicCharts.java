package com.dam.modules.dam.model;

import com.dam.modules.dam.repository.ChartDto;
import lombok.Data;

import java.util.List;

@Data
public class DynamicCharts {
    private String chartTitle;
    private List<ChartDto> data;
    private int position;
    private String type;
    private String chartName;
    private Float limitMin;
    private Float limitMax;
}
