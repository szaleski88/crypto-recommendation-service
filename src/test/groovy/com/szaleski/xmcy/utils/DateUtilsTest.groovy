package com.szaleski.xmcy.utils

import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

class DateUtilsTest extends Specification {

    private static long MILIS = 1641038400000
    private static EXPECTED_RESULT = LocalDateTime.of(2022,01,01,13,00,00)

    def "GetLocalDateTime"() {
        expect:
        EXPECTED_RESULT == DateUtils.strMillisToLocalDateTime(MILIS.toString())
    }

    def "TestGetLocalDateTime"() {
        given:
        def date = Date.from(Instant.ofEpochMilli(MILIS))

        expect:
        EXPECTED_RESULT == DateUtils.toLocalDateTime(date)
    }

    def "ToDateString"() {
        given:
        def date = Date.from(Instant.ofEpochMilli(MILIS))
        def expectedString = "2022-01-01"

        expect:
        expectedString == DateUtils.toDateString(date)
    }
}
