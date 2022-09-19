package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NormalizedRanges {

    private final LocalDateTime dateFrom;
    private final LocalDateTime dateTo;
    private final LinkedHashMap<String, BigDecimal> normalizedRanges;

}
