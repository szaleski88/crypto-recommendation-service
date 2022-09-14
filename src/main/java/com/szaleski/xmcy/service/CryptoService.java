package com.szaleski.xmcy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.HighestNormalizedRange;
import com.szaleski.xmcy.model.NormalizedRanges;
import com.szaleski.xmcy.repository.CryptoRepository;
import com.szaleski.xmcy.utils.CryptoDataRangeNormalizer;
import com.szaleski.xmcy.utils.DateUtils;

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

    public HighestNormalizedRange getHighestNormalizedRangeForDay(Date date) {
        List<CryptoData> cryptoData = getCryptoDataForDay(date);
        LinkedHashMap<String, BigDecimal> normalizedRanges = cryptoDataRangeNormalizer.getNormalizedRanges(cryptoData);
        Map.Entry<String, BigDecimal> currencyWithHighestNormalizedRange = normalizedRanges.entrySet().iterator().next();

        return new HighestNormalizedRange(currencyWithHighestNormalizedRange.getKey(),
                                          currencyWithHighestNormalizedRange.getValue(),
                                          DateUtils.toLocalDateTime(date));
    }

    public NormalizedRanges getNormalizedRangesForAll() {
        final List<Crypto> allCryptoData = cryptoRepository.findAll();

        if (allCryptoData.isEmpty()) {
            throw new CryptoDataNotAvailableException("No data available.");
        }

        final List<CryptoData> cryptoData = allCryptoData.stream()
                                                         .map(CryptoData::fromCrypto)
                                                         .collect(Collectors.toList());
        return new NormalizedRanges(cryptoDataRangeNormalizer.getNormalizedRanges(cryptoData));
    }

    public List<CryptoData> getCryptoDataBySymbolForMonth(String symbol, Date date) {
        final LocalDateTime localDateTime = DateUtils.toLocalDateTime(date);
        final LocalDateTime plusMonth = localDateTime.plusMonths(1);
        final List<Crypto> bySymbolBetweenDays = cryptoRepository.findBySymbolBetweenDays(symbol, localDateTime, plusMonth);

        return bySymbolBetweenDays.stream().map(CryptoData::fromCrypto).collect(Collectors.toList());
    }

    public List<String> getAvailableCryptos() {
        return cryptoRepository.findDistinctSymbols().stream().sorted().collect(Collectors.toList());
    }

    private List<CryptoData> getCryptoDataForDay(Date date) {
        final LocalDateTime fromDay = DateUtils.toLocalDateTime(date);
        final LocalDateTime toDay = fromDay.plusDays(1);
        final List<Crypto> cryptoValuesForDate = cryptoRepository.findBetweenDays(fromDay, toDay);

        if (cryptoValuesForDate.isEmpty()) {
            throw CryptoDataNotAvailableException.forDate(date);
        }

        return cryptoValuesForDate.stream()
                                  .map(CryptoData::fromCrypto)
                                  .collect(Collectors.toList());
    }
}
