package com.szaleski.xmcy.exceptions;

import java.time.LocalDate;

import com.szaleski.xmcy.utils.DateUtils;

import lombok.Getter;

@Getter
public class CryptoDataNotAvailableException extends RuntimeException {

    private static final String MSG_TEMPLATE = "No data for Crypto symbol: '%s' for date range: '%s - %s'.";
    private static final String MSG_TEMPLATE_DATE = "No data for date: '%s'.";
    private final String symbol;
    private final LocalDate fromDate;
    private final LocalDate toDate;

    public CryptoDataNotAvailableException(String message) {
        super(message);
        this.symbol = "None";
        this.fromDate = null;
        this.toDate = null;
    }

    public CryptoDataNotAvailableException(String message, String symbol, LocalDate fromDate, LocalDate toDate) {
        super(message);
        this.symbol = symbol;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static CryptoDataNotAvailableException forSymbolAndDateRange(String symbol, LocalDate dateFrom, LocalDate dateTo) {
        String dateFromString = DateUtils.toDateString(dateFrom);
        String dateToString = DateUtils.toDateString(dateTo);
        String message = String.format(MSG_TEMPLATE, symbol, dateFromString, dateToString);
        return new CryptoDataNotAvailableException(message, symbol, dateFrom, dateTo);
    }

    public static CryptoDataNotAvailableException forDate(LocalDate dateFrom, LocalDate dateTo) {
        String dateFromString = DateUtils.toDateString(dateFrom);
        String dateToString = DateUtils.toDateString(dateTo);
        String message = String.format(MSG_TEMPLATE, "None", dateFromString, dateToString);
        return new CryptoDataNotAvailableException(message, "None", dateFrom, dateTo);
    }

    public static CryptoDataNotAvailableException forDate(LocalDate date) {
        return CryptoDataNotAvailableException.forDate(date, date.plusDays(1));
    }

}
