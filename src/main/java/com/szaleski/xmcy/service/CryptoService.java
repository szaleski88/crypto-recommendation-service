package com.szaleski.xmcy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.repository.CryptoRepository;
import com.szaleski.xmcy.utils.CryptoFilter;
import com.szaleski.xmcy.utils.DateUtils;

@Service
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoFilter cryptoFilter;

    public CryptoService(CryptoRepository cryptoRepository, CryptoFilter cryptoFilter) {
        this.cryptoRepository = cryptoRepository;
        this.cryptoFilter = cryptoFilter;
    }

    public List<Crypto> getCryptoBySymbol(String symbol) {
        return cryptoRepository.findBySymbol(symbol);
    }

    public Crypto getMaxValue(String symbol) {
        List<Crypto> cryptoBySymbol = getCryptoBySymbol(symbol);
        return cryptoFilter.getMax(cryptoBySymbol);
    }

    public Crypto getMinValue(String symbol) {
        List<Crypto> cryptoBySymbol = getCryptoBySymbol(symbol);
        return cryptoFilter.getMin(cryptoBySymbol);
    }

    public Crypto getNewest(String symbol) {
        List<Crypto> cryptoBySymbol = getCryptoBySymbol(symbol);
        return cryptoFilter.getNewest(cryptoBySymbol);
    }

    public Crypto getOldest(String symbol) {
        List<Crypto> cryptoBySymbol = getCryptoBySymbol(symbol);
        return cryptoFilter.getOldest(cryptoBySymbol);
    }

    public BigDecimal getNormalized(String symbol) {
        List<Crypto> cryptoBySymbol = getCryptoBySymbol(symbol);
        return cryptoFilter.getNormalized(cryptoBySymbol);
    }

    public BigDecimal getNormalizedForDate(String symbol, Date date) {
        List<Crypto> cryptoValuesForDate = getCryptoBySymbolForDate(symbol, date);
        return cryptoFilter.getNormalized(cryptoValuesForDate);
    }

    private List<Crypto> getCryptoBySymbolForDate(String symbol, Date date) {
        final LocalDateTime localDateTime = DateUtils.getLocalDateTime(date);
        final LocalDateTime plusDay = localDateTime.plusDays(1);
        return cryptoRepository.findBySymbolBetweenDays(symbol, localDateTime, plusDay);
    }
}
