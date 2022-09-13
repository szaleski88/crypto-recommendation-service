package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CryptoDto {

    private LocalDateTime timestamp;
    private String symbol;

    private BigDecimal price;

    public CryptoDto() {
    }

    public CryptoDto(LocalDateTime timestamp, String symbol, BigDecimal price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public static CryptoDto fromCrypto(Crypto crypto) {
        return new CryptoDto(crypto.getTimestamp(), crypto.getSymbol(), crypto.getPrice());
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
