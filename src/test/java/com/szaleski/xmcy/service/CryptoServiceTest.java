package com.szaleski.xmcy.service;

import static com.szaleski.xmcy.model.CryptoData.fromCrypto;
import static java.math.RoundingMode.FLOOR;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.NormalizedRanges;
import com.szaleski.xmcy.repository.CryptoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
class CryptoServiceTest {

    public static final LocalDate JANUARY = LocalDate.of(2022, 1, 1);
    public static final LocalDate FEBRUARY = LocalDate.of(2022, 2, 1);
    public static final Crypto CRYPTO_JAN1 = new Crypto(null, JANUARY.atTime(LocalTime.NOON), symbolFromMonth(JANUARY), BigDecimal.ONE.setScale(3, FLOOR));
    public static final Crypto CRYPTO_JAN2 = new Crypto(null, JANUARY.atTime(LocalTime.NOON).plusDays(1), symbolFromMonth(JANUARY), BigDecimal.TEN.setScale(3, FLOOR));
    public static final Crypto CRYPTO_FEB = new Crypto(null, FEBRUARY.atTime(LocalTime.NOON), symbolFromMonth(FEBRUARY), BigDecimal.TEN.setScale(3, FLOOR));

    @Autowired
    CryptoService cryptoService;

    @MockBean
    CryptoRepository cryptoRepository;

    @Test
    public void getCryptoBySymbol() {
        // given
        given(cryptoRepository.findBySymbol("A")).willReturn(List.of(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2)));

        // when
        List<CryptoData> result = cryptoService.getCryptoBySymbol("A");

        // then
        then(result).containsExactlyInAnyOrder(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void getCryptoBySymbolThrowsOnNonExistingSymbol() {
        // given
        given(cryptoRepository.findBySymbol(anyString())).willReturn(List.of());

        // when
        Throwable throwable = catchThrowable(() -> cryptoService.getCryptoBySymbol("NON_EXISTING"));

        // then
        then(throwable).isInstanceOf(UnknownCryptoSymbolException.class);
    }

    @Test
    public void availableCryptosReturnedInOrder() {
        // given
        given(cryptoRepository.findDistinctSymbols()).willReturn(List.of("A", "Z", "D"));

        // when
        List<String> availableCryptos = cryptoService.getAvailableCryptos();

        // then
        then(availableCryptos).containsExactly("A", "D", "Z");
    }

    @Test
    public void monthlyDataForCryptoReturned() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(
            List.of(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2)));

        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth("A", JANUARY);

        // then
        LocalDateTime beginningOfJanuary = JANUARY.atStartOfDay();
        LocalDateTime endOfJanuary = FEBRUARY.minusDays(1).atTime(LocalTime.MAX);

        verify(cryptoRepository).findBySymbolBetweenDays("A", beginningOfJanuary, endOfJanuary);
        then(result).containsExactlyInAnyOrder(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void emptyDataForNonExistingSymbol() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(List.of());

        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth("NON_EXISTING", JANUARY);

        // then
        LocalDateTime beginningOfJanuary = JANUARY.atStartOfDay();
        LocalDateTime endOfJanuary = FEBRUARY.minusDays(1).atTime(LocalTime.MAX);

        verify(cryptoRepository).findBySymbolBetweenDays("NON_EXISTING", beginningOfJanuary, endOfJanuary);
        then(result).isEmpty();
    }

    @Test
    public void dataForRangeIsReturned() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(List.of(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2)));

        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForRange("A", JANUARY, FEBRUARY);

        // then
        LocalDateTime beginningOfJanuary = JANUARY.atStartOfDay();
        LocalDateTime beginningOfFebruary = FEBRUARY.atTime(LocalTime.MAX);

        verify(cryptoRepository).findBySymbolBetweenDays("A", beginningOfJanuary, beginningOfFebruary);
        then(result).containsExactlyInAnyOrder(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void dataForRangeWithSwappedToAndFromDates() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(List.of(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2)));

        // when dateFrom swapped with dateTo
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForRange("A", FEBRUARY, JANUARY);

        // then
        LocalDateTime beginningOfJanuary = JANUARY.atStartOfDay();
        LocalDateTime beginningOfFebruary = FEBRUARY.atTime(LocalTime.MAX);

        verify(cryptoRepository).findBySymbolBetweenDays("A", beginningOfJanuary, beginningOfFebruary);
        then(result).containsExactlyInAnyOrder(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void emptyResultWhenNoDataForGivenSymbolInRange() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(List.of());

        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForRange("A", FEBRUARY, JANUARY);

        // then
        then(result).isEmpty();
    }

    @Test
    public void highestNormalizedRangeForGivenDayProperlyConvertsDateToBeginningAndEndOfADay() {
        // given
        given(cryptoRepository.findBetweenDays(any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(List.of(fromCrypto(CRYPTO_JAN1), fromCrypto(CRYPTO_JAN2)));

        // when
        NormalizedRanges result = cryptoService.getHighestNormalizedRangeForDay(JANUARY);

        // then
        LocalDateTime beginningOfJanuary = JANUARY.atStartOfDay();
        LocalDateTime endOfFirstOfJanuary = JANUARY.atTime(LocalTime.MAX);

        verify(cryptoRepository).findBetweenDays(beginningOfJanuary, endOfFirstOfJanuary);
        then(result.getNormalizedRanges().keySet()).containsOnly(symbolFromMonth(JANUARY));
    }

    @Test
    public void exceptionThrownOnNonExistingDataForGivenDay() {
        // given
        given(cryptoRepository.findBySymbolBetweenDays(anyString(), any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(List.of());

        // when
        Throwable throwable = catchThrowable(() -> cryptoService.getHighestNormalizedRangeForDay(LocalDate.now()));

        // then
        then(throwable).isInstanceOf(CryptoDataNotAvailableException.class);
    }

    @Test
    public void normalizedRangesForAllCurrenciesIsProperlyCalculated() {
        // given
        given(cryptoRepository.findAll()).willReturn(List.of(CRYPTO_JAN1, CRYPTO_JAN2, CRYPTO_FEB));

        // when
        NormalizedRanges normalizedRangesForAll = cryptoService.getNormalizedRangesForAll();

        // then
        LinkedHashMap<String, BigDecimal> normalizedRanges = normalizedRangesForAll.getNormalizedRanges();
        then(normalizedRanges).isNotEmpty();
        then(normalizedRanges.get(symbolFromMonth(JANUARY))).isEqualByComparingTo(BigDecimal.valueOf(9));
        then(normalizedRanges.get(symbolFromMonth(FEBRUARY))).isEqualByComparingTo(BigDecimal.ZERO);
        then(normalizedRangesForAll.getDateFrom()).isEqualTo(JANUARY.atTime(LocalTime.NOON));
        then(normalizedRangesForAll.getDateTo()).isEqualTo(FEBRUARY.atTime(LocalTime.NOON));
    }

    @Test
    public void exceptionThrownOnEmptyData() {
        // given
        given(cryptoRepository.findAll()).willReturn(List.of());

        // when
        Throwable throwable = catchThrowable(() -> cryptoService.getNormalizedRangesForAll());

        // then
        then(throwable).isInstanceOf(CryptoDataNotAvailableException.class);
    }

    public static String symbolFromMonth(LocalDate aTime) {
        return aTime.getMonth().toString();
    }
}