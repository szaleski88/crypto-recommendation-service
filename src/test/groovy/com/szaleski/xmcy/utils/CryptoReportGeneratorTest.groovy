package com.szaleski.xmcy.utils

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException
import com.szaleski.xmcy.model.CryptoData
import com.szaleski.xmcy.model.CryptoReport
import spock.lang.Specification

import static java.time.LocalDateTime.of

class CryptoReportGeneratorTest extends Specification {

    private static CryptoData OLD_MIN = new CryptoData(of(2022, 01, 01, 00, 00), "aaa", BigDecimal.ONE)
    private static CryptoData NEW_MAX = new CryptoData(of(2023, 01, 01, 00, 00), "aaa", BigDecimal.TEN)
    private static String SYMBOL = "SYMBOL"

    def "Exception thrown when no data for report"() {
        given:
        def generator = new CryptoReportGenerator(Mock(CryptoFilter), Mock(CryptoDataRangeNormalizer))

        when:
        generator.generateReportFor(SYMBOL, [], null)

        then:
        CryptoDataNotAvailableException exc = thrown()
        with(exc) {
            it.symbol == SYMBOL
            it.date == null
            it.getMessage() == "No data for Crypto symbol: '$SYMBOL' for date: 'None'."
        }
    }

    def "Report generated with expected values"() {
        given:
        def generator = new CryptoReportGenerator(cryptoFilterForTestData(), new CryptoDataRangeNormalizer())

        when:
        CryptoReport report = generator.generateReportFor(SYMBOL, [NEW_MAX, OLD_MIN], null)

        then:
        with(report) {
            it.getCurrency() == SYMBOL
            it.getFromDate() == OLD_MIN.getTimestamp()
            it.getToDate() == NEW_MAX.getTimestamp()
            it.getNormalizedValue() == BigDecimal.valueOf(9)

            with(it.getMaxPrice()) {
                it.getPrice() == NEW_MAX.getPrice()
                it.getDate() == NEW_MAX.getTimestamp()
            }
            with(it.getMinPrice()) {
                it.getPrice() == OLD_MIN.getPrice()
                it.getDate() == OLD_MIN.getTimestamp()
            }
            with(it.getNewestPrice()) {
                it.getPrice() == NEW_MAX.getPrice()
                it.getDate() == NEW_MAX.getTimestamp()
            }
            with(it.getOldestPrice()) {
                it.getPrice() == OLD_MIN.getPrice()
                it.getDate() == OLD_MIN.getTimestamp()
            }
        }

    }

    private CryptoFilter cryptoFilterForTestData() {
        return Mock(CryptoFilter) {
            it.getNewest(_) >> NEW_MAX
            it.getMax(_) >> NEW_MAX
            it.getOldest(_) >> OLD_MIN
            it.getMin(_) >> OLD_MIN
        }
    }
}
