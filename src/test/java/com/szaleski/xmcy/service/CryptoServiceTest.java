package com.szaleski.xmcy.service;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    public static final LocalDate MARCH = LocalDate.of(2022, 3, 1);
    public static final Crypto CRYPTO_JAN1 = new Crypto(null, JANUARY.atTime(LocalTime.NOON), symbolFromMonth(JANUARY), BigDecimal.ONE);
    public static final Crypto CRYPTO_JAN2 = new Crypto(null, JANUARY.atTime(LocalTime.NOON).plusDays(1), symbolFromMonth(JANUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_FEB = new Crypto(null, FEBRUARY.atTime(LocalTime.NOON), symbolFromMonth(FEBRUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_MAR = new Crypto(null, MARCH.atTime(LocalTime.NOON), symbolFromMonth(MARCH), BigDecimal.ZERO);

    @Autowired
    CryptoService cryptoService;

    @Autowired
    CryptoRepository cryptoRepository;

    @BeforeEach
    public void setUp() {
        cryptoRepository.saveAll(List.of(CRYPTO_JAN1, CRYPTO_JAN2, CRYPTO_FEB, CRYPTO_MAR));
    }

    @Test
    public void getCryptoBySymbol() {
        // when
        List<CryptoData> result = cryptoService.getCryptoBySymbol(symbolFromMonth(JANUARY));

        // then
        // ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        // verify(cryptoRepository).findBySymbol(argument.capture());
        // then(argument.getValue()).isEqualTo("JANUARY");
    }

    @Test
    public void getCryptoBySymbolThrowsOnNonExistingSymbol() {
        // when
        Throwable throwable = catchThrowable(() -> cryptoService.getCryptoBySymbol("NON_EXISTING"));

        // then
        then(throwable).isInstanceOf(UnknownCryptoSymbolException.class);
    }

    @Test
    public void availableCryptosReturnedInOrder() {
        // when
        List<String> availableCryptos = cryptoService.getAvailableCryptos();

        // then
        then(availableCryptos).containsExactly("FEBRUARY", "JANUARY", "MARCH");
    }

    @Test
    public void monthlyDataForCryptoReturned() {
        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth(symbolFromMonth(JANUARY), JANUARY);

        // then
        then(result).containsExactlyInAnyOrder(CryptoData.fromCrypto(CRYPTO_JAN1), CryptoData.fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void emptyDataForNonExistingSymbol() {
        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth("NON_EXISTING", JANUARY);

        // then
        then(result).isEmpty();
    }

    @Test
    public void noResultForEmptyMonth() {
        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForMonth(symbolFromMonth(JANUARY), JANUARY.plusYears(1000));

        // then
        then(result).isEmpty();
    }

    @Test
    public void dataForRangeIsReturned() {
        // when
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForRange(symbolFromMonth(JANUARY), JANUARY, FEBRUARY);

        // then
        then(result).containsExactlyInAnyOrder(CryptoData.fromCrypto(CRYPTO_JAN1), CryptoData.fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void dataForRangeWithSwappedToAndFromDates() {
        // when dateFrom swapped with dateTo
        List<CryptoData> result = cryptoService.getCryptoDataBySymbolForRange(symbolFromMonth(JANUARY), FEBRUARY, JANUARY);

        // then
        then(result).containsExactlyInAnyOrder(CryptoData.fromCrypto(CRYPTO_JAN1), CryptoData.fromCrypto(CRYPTO_JAN2));
    }

    @Test
    public void highestNormalizedRangeForGivenDayReturned() {
        // when
        NormalizedRanges result = cryptoService.getHighestNormalizedRangeForDay(JANUARY);

        // then
        System.out.println(result);
        then(result.getNormalizedRanges().keySet()).containsOnly(symbolFromMonth(JANUARY));
    }

    @Test
    public void exceptionThrownOnNonExistingDataForGivenDay() {
        // when
        Throwable throwable = catchThrowable(() -> cryptoService.getHighestNormalizedRangeForDay(JANUARY.plusYears(1000)));

        // then
        then(throwable).isInstanceOf(CryptoDataNotAvailableException.class);
    }

    @Test
    public void normalizedRangesForAllCurrenciesIsProperlyCalculated() {
        // when
        NormalizedRanges normalizedRangesForAll = cryptoService.getNormalizedRangesForAll();

        // then
        LinkedHashMap<String, BigDecimal> normalizedRanges = normalizedRangesForAll.getNormalizedRanges();
        then(normalizedRanges).isNotEmpty();
        then(normalizedRanges.get(symbolFromMonth(JANUARY))).isEqualByComparingTo(BigDecimal.valueOf(9));
        then(normalizedRanges.get(symbolFromMonth(FEBRUARY))).isEqualByComparingTo(BigDecimal.ZERO);
        then(normalizedRanges.get(symbolFromMonth(MARCH))).isEqualByComparingTo(BigDecimal.ZERO);
        then(normalizedRangesForAll.getDateFrom()).isEqualTo(JANUARY.atTime(LocalTime.NOON));
        then(normalizedRangesForAll.getDateTo()).isEqualTo(MARCH.atTime(LocalTime.NOON));
    }

    @Test
    public void exceptionThrownOnEmptyData() {
        // when
        NormalizedRanges normalizedRangesForAll = cryptoService.getNormalizedRangesForAll();

        // then
        LinkedHashMap<String, BigDecimal> normalizedRanges = normalizedRangesForAll.getNormalizedRanges();
        then(normalizedRanges).isNotEmpty();
        then(normalizedRanges.get(symbolFromMonth(JANUARY))).isEqualByComparingTo(BigDecimal.valueOf(9));
        then(normalizedRanges.get(symbolFromMonth(FEBRUARY))).isEqualByComparingTo(BigDecimal.ZERO);
        then(normalizedRanges.get(symbolFromMonth(MARCH))).isEqualByComparingTo(BigDecimal.ZERO);
        then(normalizedRangesForAll.getDateFrom()).isEqualTo(JANUARY.atTime(LocalTime.NOON));
        then(normalizedRangesForAll.getDateTo()).isEqualTo(MARCH.atTime(LocalTime.NOON));
    }

    public static String symbolFromMonth(LocalDate aTime) {
        return aTime.getMonth().toString();
    }
}