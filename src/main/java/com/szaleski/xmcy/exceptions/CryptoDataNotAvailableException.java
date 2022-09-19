package com.szaleski.xmcy.exceptions;

import java.time.LocalDate;

import com.szaleski.xmcy.utils.DateUtils;

import lombok.Getter;

@Getter
public class CryptoDataNotAvailableException extends RuntimeException {

    private static final String MSG_TEMPLATE = "No data for Crypto symbol: '%s' for date: '%s'.";
    private static final String MSG_TEMPLATE_DATE = "No data for date: '%s'.";
    private final String symbol;
    private final LocalDate date;

    public CryptoDataNotAvailableException(String message) {
        super(message);
        this.symbol = "None";
        this.date = null;
    }

    public CryptoDataNotAvailableException(String message, String symbol, LocalDate date) {
        super(message);
        this.symbol = symbol;
        this.date = date;
    }

    public static CryptoDataNotAvailableException forSymbolAndDate(String symbol, LocalDate date) {
        String dateString = date == null ? "None" : DateUtils.toDateString(date);
        String message = String.format(MSG_TEMPLATE, symbol, dateString);
        return new CryptoDataNotAvailableException(message, symbol, date);
    }

    public static CryptoDataNotAvailableException forDate(LocalDate date) {
        String dateString = DateUtils.toDateString(date);
        String message = String.format(MSG_TEMPLATE_DATE, dateString);
        return new CryptoDataNotAvailableException(message, "None", date);
    }

}
