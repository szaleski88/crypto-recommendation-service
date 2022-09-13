package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CryptoDto {

    private LocalDateTime timestamp;
    private String symbol;

    private BigDecimal price;

    public static CryptoDto fromCrypto(Crypto crypto) {
        return new CryptoDto(crypto.getTimestamp(), crypto.getSymbol(), crypto.getPrice());

    }

}
