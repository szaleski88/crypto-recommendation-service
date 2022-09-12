package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.Crypto
import spock.lang.Specification

import java.time.LocalDateTime

class CryptoFilterTest extends Specification {

    private static Crypto cryptoA = new Crypto(1L, LocalDateTime.of(2022, 01, 01, 00, 00), "aaa", BigDecimal.ONE)
    private static Crypto cryptoB = new Crypto(1L, LocalDateTime.of(2022, 12, 01, 00, 00), "aaa", BigDecimal.TEN)
    private static CryptoFilter cryptoFilter = new CryptoFilter()

    def "GetNewest"() {
        expect:
        cryptoB === cryptoFilter.getNewest([cryptoA, cryptoB])
    }

    def "GetOldest"() {
        expect:
        cryptoA === cryptoFilter.getOldest([cryptoA, cryptoB])
    }

    def "GetMin"() {
        expect:
        cryptoA === cryptoFilter.getMin([cryptoA, cryptoB])
    }

    def "GetMax"() {
        expect:
        cryptoB === cryptoFilter.getMax([cryptoA, cryptoB])
    }
}
