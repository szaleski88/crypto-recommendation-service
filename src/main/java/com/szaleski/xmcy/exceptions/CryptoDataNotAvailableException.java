package com.szaleski.xmcy.exceptions;

import java.util.Date;

import com.szaleski.xmcy.utils.DateUtils;

public class CryptoDataNotAvailableException extends RuntimeException {

    private static final String MSG_TEMPLATE = "No data for Crypto symbol: '%s' for date: '%s'.";
    private final String symbol;
    private final Date date;

    public CryptoDataNotAvailableException(String message, String symbol, Date date) {
        super(message);
        this.symbol = symbol;
        this.date = date;
    }

    public static CryptoDataNotAvailableException forSymbolAndDate(String symbol, Date date) {
        String dateString = DateUtils.toDateString(date);
        String message = String.format(MSG_TEMPLATE, symbol, dateString);
        return new CryptoDataNotAvailableException(message, symbol, date);
    }

    public String getSymbol() {
        return symbol;
    }

    public Date getDate() {
        return date;
    }
}
