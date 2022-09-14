package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.Crypto
import com.szaleski.xmcy.model.CryptoData
import spock.lang.Specification

import java.time.LocalDateTime

class CryptoFilterTest extends Specification {

    private static CryptoData cryptoA = new CryptoData( LocalDateTime.of(2022, 01, 01, 00, 00), "aaa", BigDecimal.ONE)
    private static CryptoData cryptoB = new CryptoData( LocalDateTime.of(2022, 12, 01, 00, 00), "aaa", BigDecimal.TEN)
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
