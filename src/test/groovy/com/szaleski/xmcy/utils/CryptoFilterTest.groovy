package com.szaleski.xmcy.utils

import com.szaleski.xmcy.model.CryptoData
import spock.lang.Specification

import static java.time.LocalDateTime.of

class CryptoFilterTest extends Specification {

    private static CryptoData olderSmaller = new CryptoData(of(2022, 01, 01, 00, 00), "aaa", BigDecimal.ONE)
    private static CryptoData newerBigger = new CryptoData(of(2023, 01, 01, 00, 00), "aaa", BigDecimal.TEN)
    private static CryptoFilter cryptoFilter = new CryptoFilter()

    def "GetNewest returns newer cryptoData"() {
        expect:
        newerBigger === cryptoFilter.getNewest([olderSmaller, newerBigger])
    }

    def "GetOldest returns older cryptoData"() {
        expect:
        olderSmaller === cryptoFilter.getOldest([olderSmaller, newerBigger])
    }

    def "GetMin returns smaller cryptoData"() {
        expect:
        olderSmaller === cryptoFilter.getMin([olderSmaller, newerBigger])
    }

    def "GetMax returns bigger cryptoData"() {
        expect:
        newerBigger === cryptoFilter.getMax([olderSmaller, newerBigger])
    }

    def "Thrown on empty MAX"() {
        when:
        cryptoFilter.getMax([])

        then:
        thrown NoSuchElementException
    }

    def "Thrown on empty MIN"() {
        when:
        cryptoFilter.getMin([])

        then:
        thrown NoSuchElementException
    }

    def "Thrown on empty OLDEST"() {
        when:
        cryptoFilter.getOldest([])

        then:
        thrown NoSuchElementException
    }

    def "Thrown on empty NEWEST"() {
        when:
        cryptoFilter.getNewest([])

        then:
        thrown NoSuchElementException
    }

}
