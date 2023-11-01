package com.dam.modules.dam.model;

import com.dam.modules.dam.repository.ChartDto;
import lombok.Data;

import java.util.List;

@Data
public class DynamicCharts {
    private String chartTitle;
    private List<ChartDto> data;
    private int position;
}
