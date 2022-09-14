package com.szaleski.xmcy.service

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException
import com.szaleski.xmcy.model.Crypto
import com.szaleski.xmcy.model.CryptoData
import com.szaleski.xmcy.repository.CryptoRepository
import com.szaleski.xmcy.utils.CryptoDataRangeNormalizer
import com.szaleski.xmcy.utils.DateUtils
import org.checkerframework.checker.units.qual.A
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import static java.time.LocalDateTime.of

class CryptoServiceTest extends Specification {

    @Shared
    String SYMBOL_A = "A"
    @Shared
    Crypto OLD_SMALL = new Crypto(1L, of(2022, 01, 01, 00, 00), SYMBOL_A, BigDecimal.ONE)
    @Shared
    Crypto NEW_BIG = new Crypto(2L, of(2023, 01, 01, 00, 00), SYMBOL_A, BigDecimal.TEN)
    @Shared
    CryptoData OLD_SMALL_D = CryptoData.fromCrypto(OLD_SMALL)
    @Shared
    CryptoData NEW_BIG_D = CryptoData.fromCrypto(NEW_BIG)


    CryptoService cryptoService = new CryptoService(getCryptoRepositoryMock(), new CryptoDataRangeNormalizer())


    def "GetCryptoBySymbol"() {
        when:
        List<CryptoData> result = cryptoService.getCryptoBySymbol(SYMBOL_A)

        then:
        result ==~ [OLD_SMALL_D, NEW_BIG_D]
    }

    def "GetCryptoBySymbol throws exception when no result from repository"() {
        given:
        CryptoRepository repositoryMock = Mock(CryptoRepository) {
            it.findBySymbol(_) >> []
        }
        CryptoService cryptoService = new CryptoService(repositoryMock, new CryptoDataRangeNormalizer())

        when:
        cryptoService.getCryptoBySymbol(SYMBOL_A)

        then:
        UnknownCryptoSymbolException exc = thrown()
        with(exc) {
            it.getSymbol() == SYMBOL_A
        }

    }

    def "GetHighestNormalizedRangeForDay"() {
        given:
        Date date = new Date()

        when:
        cryptoService.getHighestNormalizedRangeForDay(date)

        then:
        CryptoDataNotAvailableException exc = thrown()
        with(exc) {
            it.getDate() == date
        }
    }

    def "GetNormalizedRangesForAll"() {
        when:
        cryptoService.getNormalizedRangesForAll()

        then:
        CryptoDataNotAvailableException exc = thrown()
        with(exc) {
            it.getMessage().contains("No data available")
        }
    }

    def "GetCryptoDataBySymbolForMonth"() {
        given:
        Date date = new Date()

        when:
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth(SYMBOL_A, date)

        then:
        result ==~ [NEW_BIG_D]
    }

    def "GetCryptoDataBySymbolForRange when newer before older"() {
        given:
        Date newerDate = Date.from(NEW_BIG.getTimestamp().atZone(ZoneId.systemDefault()).toInstant())
        Date olderDate = Date.from(NEW_BIG.getTimestamp().atZone(ZoneId.systemDefault()).toInstant())

        when:
        List<CryptoData> range = cryptoService.getCryptoDataBySymbolForRange(SYMBOL_A, newerDate, olderDate)

        then:
        range ==~ [NEW_BIG_D]
    }

    def "GetAvailableCryptos"() {
        expect:
        cryptoService.getAvailableCryptos() ==~ ["A", "B", "C"]
    }

    private CryptoRepository getCryptoRepositoryMock() {
        return Mock(CryptoRepository) {
            it.findAll() >> []
            it.findBetweenDays(_, _) >> []
            it.findBySymbol(_) >> [OLD_SMALL, NEW_BIG]
            it.findBySymbolBetweenDays(_, _, _) >> [NEW_BIG]
            it.findDistinctSymbols() >> ["A", "B", "C"]
        }
    }

    Crypto crypto(String symbol) {
        return Mock(Crypto) {
            it.symbol >> symbol
        }

    }

}
