package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CryptoData {

    private LocalDateTime timestamp;
    private String symbol;

    private BigDecimal price;

    public static CryptoData fromCrypto(Crypto crypto) {
        return new CryptoData(crypto.getTimestamp(), crypto.getSymbol(), crypto.getPrice());
    }

}
