package com.szaleski.xmcy.repository;

import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.szaleski.xmcy.model.Crypto;

@DataJpaTest
class CryptoRepositoryTest {

    public static final LocalDateTime JANUARY = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
    public static final LocalDateTime FEBRUARY = LocalDateTime.of(2022, 2, 1, 12, 0, 0);
    public static final LocalDateTime MARCH = LocalDateTime.of(2022, 3, 1, 12, 0, 0);
    public static final Crypto CRYPTO_JAN1 = new Crypto(null, JANUARY, symbolFromMonth(JANUARY), BigDecimal.ONE);
    public static final Crypto CRYPTO_JAN2 = new Crypto(null, JANUARY.plusDays(1), symbolFromMonth(JANUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_FEB = new Crypto(null, FEBRUARY, symbolFromMonth(FEBRUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_MAR = new Crypto(null, MARCH, symbolFromMonth(MARCH), BigDecimal.ZERO);

    @Autowired
    private CryptoRepository cryptoRepository;

    @BeforeEach
    public void setUp() {
        cryptoRepository.saveAll(List.of(CRYPTO_JAN1, CRYPTO_JAN2, CRYPTO_FEB, CRYPTO_MAR));
    }

    public static Stream<Arguments> findBySymbolBetweenDaysTestData() {
        return Stream.of(
            Arguments.of(symbolFromMonth(JANUARY), FEBRUARY, MARCH, List.of()),
            Arguments.of(symbolFromMonth(FEBRUARY), FEBRUARY, MARCH, List.of(CRYPTO_FEB)),
            Arguments.of(symbolFromMonth(MARCH), FEBRUARY, MARCH, List.of(CRYPTO_MAR)),
            Arguments.of(symbolFromMonth(JANUARY), JANUARY, FEBRUARY, List.of(CRYPTO_JAN1, CRYPTO_JAN2)),
            Arguments.of(symbolFromMonth(JANUARY), FEBRUARY, JANUARY, List.of()),
            Arguments.of(symbolFromMonth(JANUARY), JANUARY, JANUARY, List.of(CRYPTO_JAN1))
                        );
    }

    @ParameterizedTest
    @MethodSource("findBySymbolBetweenDaysTestData")
    void findBySymbolBetweenDays(String symbol, LocalDateTime dateFrom, LocalDateTime dateTo, List<Crypto> expectedResult) {
        // when
        List<Crypto> result = cryptoRepository.findBySymbolBetweenDays(symbol, dateFrom, dateTo);

        // then
        then(result).containsExactlyInAnyOrderElementsOf(expectedResult);
    }

    public static Stream<Arguments> findBetweenDaysTestData() {
        return Stream.of(
            Arguments.of(JANUARY, MARCH, List.of(CRYPTO_JAN1, CRYPTO_JAN2, CRYPTO_FEB)),
            Arguments.of(FEBRUARY, MARCH, List.of(CRYPTO_FEB)),
            Arguments.of(MARCH, MARCH.plusMonths(1), List.of(CRYPTO_MAR)),
            Arguments.of(JANUARY, JANUARY, List.of()),
            Arguments.of(JANUARY, JANUARY.plusDays(1), List.of(CRYPTO_JAN1)),
            Arguments.of(JANUARY, JANUARY.plusDays(5), List.of(CRYPTO_JAN1, CRYPTO_JAN2))
                        );
    }

    @ParameterizedTest
    @MethodSource("findBetweenDaysTestData")
    void findBetweenDays(LocalDateTime dateFrom, LocalDateTime dateTo, List<Crypto> expectedResult) {
        // when
        List<Crypto> result = cryptoRepository.findBetweenDays(dateFrom, dateTo);

        // then
        then(result).containsExactlyInAnyOrderElementsOf(expectedResult);
    }

    @Test
    void findBySymbol() {
        // when
        List<Crypto> result = cryptoRepository.findBySymbol(symbolFromMonth(JANUARY));

        // then
        then(result).containsExactlyInAnyOrder(CRYPTO_JAN1, CRYPTO_JAN2);
    }

    @Test
    void findDistinctSymbols() {
        // when
        List<String> result = cryptoRepository.findDistinctSymbols();

        // then
        then(result).containsExactlyInAnyOrder(symbolFromMonth(JANUARY), symbolFromMonth(FEBRUARY), symbolFromMonth(MARCH));
    }

    public static String symbolFromMonth(LocalDateTime aTime) {
        return aTime.getMonth().toString();
    }
}