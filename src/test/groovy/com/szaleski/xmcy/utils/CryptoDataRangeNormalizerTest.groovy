package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.Crypto
import spock.lang.Specification

class CryptoDataRangeNormalizerTest extends Specification {

    private static CryptoDataRangeNormalizer normalizer = new CryptoDataRangeNormalizer()

    def "Crypto price properly normalized"() {
        given:

        def cryptoA = Mock(Crypto) {
            it.getPrice() >> min
        }
        def cryptoB = Mock(Crypto) {
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
        expect:
        -1 == normalizer.getSingleNormalizedRange([])
    }
}
