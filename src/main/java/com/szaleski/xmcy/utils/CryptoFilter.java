package com.szaleski.xmcy.utils;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.Crypto;

@Component
public class CryptoFilter {

    private final Normalizer normalizer;

    public CryptoFilter(Normalizer normalizer) {
        this.normalizer = normalizer;
    }

    public CryptoFilter() {
        this.normalizer = new Normalizer();
    }

    public Crypto getNewest(List<Crypto> cryptoList) {
        return cryptoList.parallelStream().max(Comparator.comparing(Crypto::getTimestamp)).orElseThrow(NoSuchElementException::new);
    }

    public Crypto getOldest(List<Crypto> cryptoList) {
        return cryptoList.parallelStream().min(Comparator.comparing(Crypto::getTimestamp)).orElseThrow(NoSuchElementException::new);
    }

    public Crypto getMin(List<Crypto> cryptoList) {
        return cryptoList.parallelStream().min(Comparator.comparing(Crypto::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public Crypto getMax(List<Crypto> cryptoList) {
        return cryptoList.parallelStream().max(Comparator.comparing(Crypto::getPrice)).orElseThrow(NoSuchElementException::new);
    }

}
