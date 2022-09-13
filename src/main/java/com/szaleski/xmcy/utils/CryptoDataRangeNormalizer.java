package com.szaleski.xmcy.utils;

import static com.szaleski.xmcy.utils.DateUtils.getLocalDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.HighestNormalizedRange;

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
                                              .min(Comparator.comparing(CryptoData::getPrice))
                                              .orElseThrow(NoSuchElementException::new)
                                              .getPrice();
        final BigDecimal maxPrice = cryptoData.parallelStream()
                                              .max(Comparator.comparing(CryptoData::getPrice))
                                              .orElseThrow(NoSuchElementException::new)
                                              .getPrice();

        return maxPrice.subtract(minPrice).divide(minPrice, 4, RoundingMode.FLOOR);
    }

    public LinkedHashMap<String, BigDecimal> getNormalizedRanges(List<CryptoData> cryptoData) {
        Map<String, List<CryptoData>> cryptosBySymbol = cryptoData.parallelStream().collect(Collectors.groupingBy(CryptoData::getSymbol));

        return cryptosBySymbol.entrySet().stream()
                              .map(entry -> Map.entry(entry.getKey(), getSingleNormalizedRange(entry.getValue())))
                              .sorted(COMPARATOR)
                              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                        (v1, v2) -> v2,
                                                        LinkedHashMap::new));
    }
}
