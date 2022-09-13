package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HighestNormalizedRange {

    private String currency;
    private BigDecimal normalizedRange;
    private LocalDateTime date;

}
