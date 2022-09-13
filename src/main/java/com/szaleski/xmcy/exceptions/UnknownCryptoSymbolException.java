package com.szaleski.xmcy.exceptions;

public class UnknownCryptoSymbolException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Unknown Crypto symbol: '%s'. Data not available.";
    private final String symbol;

    public UnknownCryptoSymbolException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
    }

    public static UnknownCryptoSymbolException forSymbol(String symbol) {
        return new UnknownCryptoSymbolException(String.format(MSG_TEMPLATE, symbol), symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
