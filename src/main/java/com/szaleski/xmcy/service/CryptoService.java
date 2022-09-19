package com.szaleski.xmcy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.NormalizedRanges;
import com.szaleski.xmcy.repository.CryptoRepository;
import com.szaleski.xmcy.utils.CryptoDataRangeNormalizer;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoDataRangeNormalizer cryptoDataRangeNormalizer;

    public List<CryptoData> getCryptoBySymbol(String symbol) {
        List<Crypto> cryptoBySymbol = cryptoRepository.findBySymbol(symbol);

        if (cryptoBySymbol.isEmpty()) {
            throw UnknownCryptoSymbolException.forSymbol(symbol);
        }
        return cryptoBySymbol.stream()
                             .map(CryptoData::fromCrypto)
                             .sorted(Comparator.comparing(CryptoData::getTimestamp))
                             .collect(Collectors.toList());
    }

    public NormalizedRanges getHighestNormalizedRangeForDay(LocalDate date) {
        List<CryptoData> cryptoData = getCryptoDataForDay(date);
        return cryptoDataRangeNormalizer.getHighestNormalizedRangeOfDay(cryptoData, date);
    }

    public NormalizedRanges getNormalizedRangesForAll() {
        List<Crypto> allCryptoData = cryptoRepository.findAll();

        if (allCryptoData.isEmpty()) {
            throw new CryptoDataNotAvailableException("No data available.");
        }

        List<CryptoData> cryptoData = allCryptoData.stream()
                                                   .map(CryptoData::fromCrypto)
                                                   .collect(Collectors.toList());
        LocalDateTime minDate = cryptoData.stream().map(CryptoData::getTimestamp).min(Comparator.comparing(Function.identity())).get();
        LocalDateTime maxDate = cryptoData.stream().map(CryptoData::getTimestamp).max(Comparator.comparing(Function.identity())).get();

        return cryptoDataRangeNormalizer.getNormalizedRanges(cryptoData, minDate, maxDate);
    }

    public List<CryptoData> getCryptoDataBySymbolForMonth(String symbol, LocalDate date) {
        LocalDate lastDayOfGivenMonth = date.plusMonths(1).minusDays(1);
        List<Crypto> bySymbolBetweenDays = cryptoRepository.findBySymbolBetweenDays(symbol, date.atStartOfDay(), lastDayOfGivenMonth.atTime(LocalTime.MAX));

        return bySymbolBetweenDays.stream().map(CryptoData::fromCrypto).collect(Collectors.toList());
    }

    public List<CryptoData> getCryptoDataBySymbolForRange(String symbol, LocalDate dateFrom, LocalDate dateTo) {
        LocalDate realFrom = dateFrom.isBefore(dateTo) ? dateFrom : dateTo;
        LocalDate realTo = dateFrom.isBefore(dateTo) ? dateTo : dateFrom;

        List<Crypto> bySymbolBetweenDays = cryptoRepository.findBySymbolBetweenDays(symbol, realFrom.atStartOfDay(), realTo.atTime(LocalTime.MAX));

        return bySymbolBetweenDays.stream().map(CryptoData::fromCrypto).collect(Collectors.toList());
    }

    public List<String> getAvailableCryptos() {
        return cryptoRepository.findDistinctSymbols().stream().sorted().collect(Collectors.toList());
    }

    private List<CryptoData> getCryptoDataForDay(LocalDate date) {
        List<Crypto> cryptoValuesForDate = cryptoRepository.findBetweenDays(date.atStartOfDay(), date.atTime(LocalTime.MAX));

        if (cryptoValuesForDate.isEmpty()) {
            throw CryptoDataNotAvailableException.forDate(date);
        }

        return cryptoValuesForDate.stream()
                                  .map(CryptoData::fromCrypto)
                                  .collect(Collectors.toList());
    }
}
