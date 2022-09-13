package com.szaleski.xmcy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoDto;
import com.szaleski.xmcy.repository.CryptoRepository;
import com.szaleski.xmcy.utils.CryptoFilter;
import com.szaleski.xmcy.utils.DateUtils;
import com.szaleski.xmcy.utils.Normalizer;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoFilter cryptoFilter;
    private final Normalizer normalizer;

    public List<CryptoDto> getCryptoBySymbol(String symbol) {
        List<Crypto> cryptoBySymbol = cryptoRepository.findBySymbol(symbol);
        if(cryptoBySymbol.isEmpty()) {
            throw UnknownCryptoSymbolException.forSymbol(symbol);
        }
        return cryptoBySymbol.stream()
                             .map(CryptoDto::fromCrypto)
                             .sorted(Comparator.comparing(CryptoDto::getTimestamp))
                             .collect(Collectors.toList());
    }

    public CryptoDto getMaxValue(String symbol) {
        return getCryptoOrThrow(symbol, cryptoFilter::getMax);
    }

    public CryptoDto getMinValue(String symbol) {
        return getCryptoOrThrow(symbol, cryptoFilter::getMin);
    }

    public CryptoDto getNewest(String symbol) {
        return getCryptoOrThrow(symbol, cryptoFilter::getNewest);
    }

    public CryptoDto getOldest(String symbol) {
        return getCryptoOrThrow(symbol, cryptoFilter::getOldest);
    }

    public BigDecimal getNormalizedForSymbolAndDate(String symbol, Date date) {
        final LocalDateTime localDateTime = DateUtils.getLocalDateTime(date);
        final LocalDateTime plusDay = localDateTime.plusDays(1);
        final List<Crypto> cryptoValuesForDate = cryptoRepository.findBySymbolBetweenDays(symbol, localDateTime, plusDay);
        if (cryptoValuesForDate.isEmpty()) {
            throw CryptoDataNotAvailableException.forSymbolAndDate(symbol, date);
        }
        return normalizer.normalize(cryptoValuesForDate);
    }

    private CryptoDto getCryptoOrThrow(String symbol, Function<List<Crypto>, Crypto> filteringFunction) {
        try {
            List<Crypto> cryptoBySymbol = cryptoRepository.findBySymbol(symbol);
            Crypto crypto = filteringFunction.apply(cryptoBySymbol);
            return CryptoDto.fromCrypto(crypto);
        } catch (NoSuchElementException ex) {
            throw UnknownCryptoSymbolException.forSymbol(symbol);
        }
    }

}
