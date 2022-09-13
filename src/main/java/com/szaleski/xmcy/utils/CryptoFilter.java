package com.szaleski.xmcy.utils;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.CryptoData;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CryptoFilter {

    private final CryptoDataRangeNormalizer cryptoDataRangeNormalizer;

    public CryptoFilter() {
        this.cryptoDataRangeNormalizer = new CryptoDataRangeNormalizer();
    }

    public CryptoData getNewest(List<CryptoData> cryptoList) {
        return cryptoList.parallelStream().max(Comparator.comparing(CryptoData::getTimestamp)).orElseThrow(NoSuchElementException::new);
    }

    public CryptoData getOldest(List<CryptoData> cryptoList) {
        return cryptoList.parallelStream().min(Comparator.comparing(CryptoData::getTimestamp)).orElseThrow(NoSuchElementException::new);
    }

    public CryptoData getMin(List<CryptoData> cryptoList) {
        return cryptoList.parallelStream().min(Comparator.comparing(CryptoData::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public CryptoData getMax(List<CryptoData> cryptoList) {
        return cryptoList.parallelStream().max(Comparator.comparing(CryptoData::getPrice)).orElseThrow(NoSuchElementException::new);
    }

}
