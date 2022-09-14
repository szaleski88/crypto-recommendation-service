package com.szaleski.xmcy.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
        return cryptoDataRangeNormalizer.getHighestNormalizedRange(cryptoData, date);
    }

    public NormalizedRanges getNormalizedRangesForAll() {
        List<Crypto> allCryptoData = cryptoRepository.findAll();

        if (allCryptoData.isEmpty()) {
            throw new CryptoDataNotAvailableException("No data available.");
        }

        List<CryptoData> cryptoData = allCryptoData.stream()
                                                   .map(CryptoData::fromCrypto)
                                                   .collect(Collectors.toList());
        return cryptoDataRangeNormalizer.getNormalizedRanges(cryptoData);
    }

    public List<CryptoData> getCryptoDataBySymbolForMonth(String symbol, Date date) {
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(date);
        LocalDateTime plusMonth = localDateTime.plusMonths(1);
        List<Crypto> bySymbolBetweenDays = cryptoRepository.findBySymbolBetweenDays(symbol, localDateTime, plusMonth);

        return bySymbolBetweenDays.stream().map(CryptoData::fromCrypto).collect(Collectors.toList());
    }

    public List<CryptoData> getCryptoDataBySymbolForRange(String symbol, Date dateFrom, Date dateTo) {
        LocalDateTime from = DateUtils.toLocalDateTime(dateFrom);
        LocalDateTime to = DateUtils.toLocalDateTime(dateTo).plusDays(1);

        LocalDateTime realFrom = from.isBefore(to) ? from : to;
        LocalDateTime realTo = to.isAfter(from) ? to : from;

        List<Crypto> bySymbolBetweenDays = cryptoRepository.findBySymbolBetweenDays(symbol, realFrom, realTo);

        return bySymbolBetweenDays.stream().map(CryptoData::fromCrypto).collect(Collectors.toList());
    }

    public List<String> getAvailableCryptos() {
        return cryptoRepository.findDistinctSymbols().stream().sorted().collect(Collectors.toList());
    }

    private List<CryptoData> getCryptoDataForDay(Date date) {
        LocalDateTime fromDay = DateUtils.toLocalDateTime(date);
        LocalDateTime toDay = fromDay.plusDays(1);
        List<Crypto> cryptoValuesForDate = cryptoRepository.findBetweenDays(fromDay, toDay);

        if (cryptoValuesForDate.isEmpty()) {
            throw CryptoDataNotAvailableException.forDate(date);
        }

        return cryptoValuesForDate.stream()
                                  .map(CryptoData::fromCrypto)
                                  .collect(Collectors.toList());
    }
}
