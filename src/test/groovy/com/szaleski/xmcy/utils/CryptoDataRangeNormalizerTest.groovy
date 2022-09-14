package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.CryptoData
import spock.lang.Specification

class CryptoDataRangeNormalizerTest extends Specification {

    private static CryptoDataRangeNormalizer normalizer = new CryptoDataRangeNormalizer()

    def "Crypto price properly normalized"() {
        given:
        def cryptoA = getMockWithSymbolAndPrice("A", BigDecimal.valueOf(min))
        def cryptoB = getMockWithSymbolAndPrice("B", BigDecimal.valueOf(max))

        when:
        def result = normalizer.getSingleNormalizedRange([cryptoA, cryptoB])

        then:
        result == expected

        where:
        min    | max       | expected
        2      | 1         | 1
        4      | 2         | 1
        3.1234 | 2.3456789 | 0.3315
    }

    def "Data normalization for multiple currencies" () {
        given:
        def cryptoA1 = getMockWithSymbolAndPrice("A", 1)
        def cryptoA2 = getMockWithSymbolAndPrice("A", 2)
        def cryptoB1 = getMockWithSymbolAndPrice("B", 2)
        def cryptoB2 = getMockWithSymbolAndPrice("B", 4)
        def cryptoC = getMockWithSymbolAndPrice("C", 7)

        def cryptoData = [cryptoA1, cryptoA2, cryptoB1, cryptoB2, cryptoC]
        when:
        def result = normalizer.getNormalizedRanges(cryptoData)

        then:
        result.get("A") == 1
        result.get("B") == 1
        result.get("C") == 0
    }

    def "No crypto"(){
        when:
        normalizer.getSingleNormalizedRange([])

        then:
        thrown NoSuchElementException
    }


    private CryptoData getMockWithSymbolAndPrice(String symbol, int price) {
        return getMockWithSymbolAndPrice(symbol, BigDecimal.valueOf(price))
    }

    private CryptoData getMockWithSymbolAndPrice(String symbol, BigDecimal price) {
        return Mock(CryptoData) {
            it.getSymbol() >> symbol
            it.getPrice() >> price
        }
    }
}
