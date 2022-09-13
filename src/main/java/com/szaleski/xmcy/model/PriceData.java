package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PriceData {

    private BigDecimal price;
    private LocalDateTime date;

    public static PriceData from(CryptoData cryptoData) {
        return new PriceData(cryptoData.getPrice(), cryptoData.getTimestamp());
    }
}
