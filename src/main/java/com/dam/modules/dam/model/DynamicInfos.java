package com.dam.modules.dam.model;

import com.dam.modules.dam.repository.ChartDto;
import lombok.Data;

import java.util.List;

@Data
public class DynamicInfos {
    private String title;
    private String data;
    private String icon;
    private int position;
    private List<Integer> flags;
}
