package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.CryptoData
import spock.lang.Specification

class CryptoDataRangeNormalizerTest extends Specification {

    private static CryptoDataRangeNormalizer normalizer = new CryptoDataRangeNormalizer()

    def "Crypto price properly normalized"() {
        given:

        def cryptoA = Mock(CryptoData) {
            it.getPrice() >> min
        }
        def cryptoB = Mock(CryptoData) {
            it.getPrice() >> max
        }
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

    def "No crypto"(){
        when:
        normalizer.getSingleNormalizedRange([])

        then:
        thrown NoSuchElementException
    }
}
