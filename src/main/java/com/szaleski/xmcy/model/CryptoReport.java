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

    private String currency;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private BigDecimal normalizedValue;

    private PriceData oldestPrice;
    private PriceData newestPrice;
    private PriceData maxPrice;
    private PriceData minPrice;

}
