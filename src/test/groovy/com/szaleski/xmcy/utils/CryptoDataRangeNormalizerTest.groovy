package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.CryptoData
import spock.lang.Specification

import java.time.LocalDate

class CryptoDataRangeNormalizerTest extends Specification {

    private static CryptoDataRangeNormalizer normalizer = new CryptoDataRangeNormalizer()

    def "Crypto price properly normalized"() {
        given:
        def cryptoA = getCryptoData("A", min)
        def cryptoB = getCryptoData("B", max)

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

    def "Data normalization for multiple currencies"() {
        given:
        def cryptoA1 = getCryptoData("A", 1)
        def cryptoA2 = getCryptoData("A", 2)
        def cryptoB1 = getCryptoData("B", 2)
        def cryptoB2 = getCryptoData("B", 4)
        def cryptoC = getCryptoData("C", 7)

        def cryptoData = [cryptoA1, cryptoA2, cryptoB1, cryptoB2, cryptoC]
        when:
        def result = normalizer.getNormalizedRanges(cryptoData, null, null)

        then:
        def ranges = result.getNormalizedRanges()

        ranges.get("A") == 1
        ranges.get("B") == 1
        ranges.get("C") == 0
    }

    def "Get Highest normalized Range"() {
        given:
        def cryptoA1 = getCryptoData("A", 1)
        def cryptoA2 = getCryptoData("A", 2)
        def cryptoB = getCryptoData("B", 7)

        def cryptoData = [cryptoA1, cryptoA2, cryptoB]
        when:
        def result = normalizer.getHighestNormalizedRangeOfDay(cryptoData, LocalDate.now())

        then:
        1 == result.getNormalizedRanges().entrySet().size()
        with(result.getNormalizedRanges()) {
            it.get("A") == 1
        }
    }

    def "No crypto"() {
        when:
        def result = normalizer.getSingleNormalizedRange([])

        then:
        result == 0
    }


    private CryptoData getCryptoData(String symbol, double price) {
        return new CryptoData(null, symbol, BigDecimal.valueOf(price))
    }

}
