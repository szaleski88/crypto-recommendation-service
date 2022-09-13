package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CryptoReport {

    private String cryptoSymbol;
    private String reportedMonth;
    private BigDecimal normalizedValue;

    private BigDecimal maxValue;
    private LocalDateTime maxValueDate;

    private BigDecimal minValue;
    private LocalDateTime minValueDate;

    private BigDecimal newestValue;
    private LocalDateTime newestValueDate;

    private BigDecimal oldestValue;
    private LocalDateTime oldestValueDate;

}
