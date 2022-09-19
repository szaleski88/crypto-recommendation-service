package com.szaleski.xmcy.service;

import static com.szaleski.xmcy.TestData.CRYPTO_JAN1;
import static com.szaleski.xmcy.TestData.CRYPTO_JAN2;
import static com.szaleski.xmcy.TestData.FEBRUARY;
import static com.szaleski.xmcy.TestData.JANUARY;
import static com.szaleski.xmcy.TestData.MARCH;
import static com.szaleski.xmcy.TestData.symbolFromMonth;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.szaleski.xmcy.TestData;
import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.exceptions.UnknownCryptoSymbolException;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.NormalizedRanges;
import com.szaleski.xmcy.repository.CryptoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
class CryptoServiceTest {

    @Autowired
    CryptoService cryptoService;

    @Autowired
    CryptoRepository cryptoRepository;

    @BeforeEach
    public void setUp() {
        cryptoRepository.saveAll(TestData.getAll());
    }

    @Test
    public void getCryptoBySymbol() {
        // when
        List<CryptoData> result = cryptoService.getCryptoBySymbol(symbolFromMonth(JANUARY));

        // then
        then(result).containsExactlyInAnyOrder(CryptoData.fromCrypto(CRYPTO_JAN1), CryptoData.fromCrypto(CRYPTO_JAN2));
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
        then(normalizedRangesForAll.getDateFrom()).isEqualTo(JANUARY);
        then(normalizedRangesForAll.getDateTo()).isEqualTo(MARCH);
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
        then(normalizedRangesForAll.getDateFrom()).isEqualTo(JANUARY);
        then(normalizedRangesForAll.getDateTo()).isEqualTo(MARCH);
    }

}