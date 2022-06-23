package com.triple.mileage.module.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HistoryRes {

    private int mileage;
    private List<PointHistoryDto> history;



}
