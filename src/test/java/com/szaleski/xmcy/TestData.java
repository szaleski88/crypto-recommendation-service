package com.szaleski.xmcy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.szaleski.xmcy.model.Crypto;

import lombok.Getter;

@Getter
public class TestData {

    public static final LocalDateTime JANUARY = LocalDateTime.of(2022, 1, 1, 12, 0,0);
    public static final LocalDateTime FEBRUARY = LocalDateTime.of(2022, 2, 1, 12, 0,0);
    public static final LocalDateTime MARCH = LocalDateTime.of(2022, 3, 1, 12, 0,0);
    public static final Crypto CRYPTO_JAN1 = new Crypto(null, JANUARY, symbolFromMonth(JANUARY), BigDecimal.ONE);
    public static final Crypto CRYPTO_JAN2 = new Crypto(null, JANUARY.plusDays(1), symbolFromMonth(JANUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_FEB = new Crypto(null, FEBRUARY, symbolFromMonth(FEBRUARY), BigDecimal.TEN);
    public static final Crypto CRYPTO_MAR = new Crypto(null, MARCH, symbolFromMonth(MARCH), BigDecimal.ZERO);

    public static List<Crypto> getAll() {
        return List.of(CRYPTO_JAN1, CRYPTO_JAN2, CRYPTO_FEB, CRYPTO_MAR);
    }

    public static String symbolFromMonth(LocalDateTime aTime) {
        return aTime.getMonth().toString();
    }
}
