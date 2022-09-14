package com.szaleski.xmcy.service

import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException
import com.szaleski.xmcy.model.Crypto
import com.szaleski.xmcy.model.CryptoData
import com.szaleski.xmcy.repository.CryptoRepository
import com.szaleski.xmcy.utils.CryptoDataRangeNormalizer
import spock.lang.Shared
import spock.lang.Specification

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
        CryptoService cryptoService = new CryptoService( repositoryMock, new CryptoDataRangeNormalizer())

        when:
        cryptoService.getCryptoBySymbol(SYMBOL_A)

        then:
        UnknownCryptoSymbolException exc = thrown()
        with(exc) {
            it.getSymbol() == SYMBOL_A
        }

    }

    def "GetHighestNormalizedRangeForDay"() {
        expect:
        true
    }

    def "GetNormalizedRangesForAll"() {
        expect:
        true
    }

    def "GetCryptoDataBySymbolForMonth"() {
        expect:
        true

    }

    def "GetCryptoDataBySymbolForRange"() {
        expect:
        true
    }

    def "GetAvailableCryptos"() {
        expect:
        true
    }

    private CryptoRepository getCryptoRepositoryMock() {
        return Mock(CryptoRepository) {
            it.findBetweenDays(_, _) >> []
            it.findBySymbol(_) >> [OLD_SMALL, NEW_BIG]
            it.findBySymbolBetweenDays(_, _, _) >> []
            it.findDistinctSymbols() >> ["A", "B", "C"]
        }
    }

    Crypto crypto(String symbol) {
        return Mock(Crypto) {
            it.symbol >> symbol
        }

    }

}
