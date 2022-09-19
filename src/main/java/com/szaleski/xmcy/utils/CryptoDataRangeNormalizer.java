package com.szaleski.xmcy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.NormalizedRanges;

@Component
public class CryptoDataRangeNormalizer {

    private final static Comparator<Map.Entry<String, BigDecimal>> COMPARATOR = (o1, o2) -> {
        int compare = o1.getValue().compareTo(o2.getValue());
        if (compare == 0) {
            return o1.getKey().compareTo(o2.getKey());
        }
        return compare < 0 ? 1 : -1;
    };

    public BigDecimal getSingleNormalizedRange(List<CryptoData> cryptoData) {
        final BigDecimal minPrice = cryptoData.parallelStream()
                                              .filter(cd -> !cd.getPrice().equals(BigDecimal.ZERO))
                                              .min(Comparator.comparing(CryptoData::getPrice))
                                              .map(CryptoData::getPrice)
                                              .orElse(null);
        if (minPrice == null) {
            return BigDecimal.ZERO;
        }

        final BigDecimal maxPrice = cryptoData.parallelStream()
                                              .filter(cd -> !cd.getPrice().equals(BigDecimal.ZERO))
                                              .max(Comparator.comparing(CryptoData::getPrice))
                                              .map(CryptoData::getPrice)
                                              .orElse(null);

        return maxPrice.subtract(minPrice).divide(minPrice, 4, RoundingMode.FLOOR);
    }

    public NormalizedRanges getNormalizedRanges(List<CryptoData> cryptoData, LocalDateTime dateFrom, LocalDateTime dateTo) {
        Map<String, List<CryptoData>> cryptosBySymbol = cryptoData.parallelStream().collect(Collectors.groupingBy(CryptoData::getSymbol));

        final LinkedHashMap<String, BigDecimal> normalizedRangesByCryptoSymbol = cryptosBySymbol.entrySet().stream()
                                                                                                .map(entry -> Map.entry(entry.getKey(),
                                                                                                                        getSingleNormalizedRange(
                                                                                                                            entry.getValue())))
                                                                                                .sorted(COMPARATOR)
                                                                                                .collect(
                                                                                                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                                                                     (v1, v2) -> v2,
                                                                                                                     LinkedHashMap::new));
        return new NormalizedRanges(dateFrom, dateTo, normalizedRangesByCryptoSymbol);
    }

    public NormalizedRanges getHighestNormalizedRangeOfDay(List<CryptoData> cryptoData, LocalDateTime date) {
        NormalizedRanges normalizedRanges = getNormalizedRanges(cryptoData, date, date);
        BigDecimal highestNormalizedRange = normalizedRanges.getNormalizedRanges().entrySet().iterator().next().getValue();

        LinkedHashMap<String, BigDecimal> currenciesWithHighestNormalizedRanges =
            normalizedRanges.getNormalizedRanges().entrySet().stream().filter(entry -> entry.getValue().equals(highestNormalizedRange))
                            .collect(
                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                 (v1, v2) -> v2,
                                                 LinkedHashMap::new));

        return new NormalizedRanges(date, date, currenciesWithHighestNormalizedRanges);
    }
}
