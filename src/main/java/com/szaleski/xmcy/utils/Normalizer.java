package com.szaleski.xmcy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.Crypto;

@Component
public class Normalizer {

    public BigDecimal normalize(List<Crypto> cryptos) {
        final BigDecimal minPrice = cryptos.parallelStream().min(Comparator.comparing(Crypto::getPrice)).orElseThrow(NoSuchElementException::new).getPrice();
        final BigDecimal maxPrice = cryptos.parallelStream().max(Comparator.comparing(Crypto::getPrice)).orElseThrow(NoSuchElementException::new).getPrice();

        return maxPrice.subtract(minPrice).divide(minPrice, 4, RoundingMode.FLOOR);
    }

}
