package com.szaleski.xmcy.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoReport;
import com.szaleski.xmcy.repository.CryptoRepository;

@Component
public class CryptoReportGenerator {

    private final CryptoRepository cryptoRepository;
    private final CryptoFilter cryptoFilter;
    private final Normalizer normalizer;

    public CryptoReportGenerator(CryptoRepository cryptoRepository, CryptoFilter cryptoFilter, Normalizer normalizer) {
        this.cryptoRepository = cryptoRepository;
        this.cryptoFilter = cryptoFilter;
        this.normalizer = normalizer;
    }

    public CryptoReport generateReportFor(String symbol, Date date) {
        final List<Crypto> cryptoValuesForMonth = fetchDataForReport(symbol, date);

        final Crypto min = cryptoFilter.getMin(cryptoValuesForMonth);
        final Crypto max = cryptoFilter.getMax(cryptoValuesForMonth);
        final Crypto oldest = cryptoFilter.getOldest(cryptoValuesForMonth);
        final Crypto newest = cryptoFilter.getNewest(cryptoValuesForMonth);
        final BigDecimal monthlyNormalized = normalizer.normalize(cryptoValuesForMonth);

        final CryptoReport report = new CryptoReport();
        report.setMaxValue(max.getPrice());
        report.setMaxValueDate(max.getTimestamp());

        report.setMinValue(min.getPrice());
        report.setMinValueDate(min.getTimestamp());

        report.setNewestValue(newest.getPrice());
        report.setNewestValueDate(newest.getTimestamp());

        report.setOldestValue(oldest.getPrice());
        report.setOldestValueDate(oldest.getTimestamp());

        report.setNormalizedValue(monthlyNormalized);

        report.setReportedMonth(DateUtils.getMonthName(date));
        report.setCryptoSymbol(symbol);

        return report;
    }

    private List<Crypto> fetchDataForReport(final String symbol, final Date date) {
        final LocalDateTime localDateTime = DateUtils.getLocalDateTime(date);
        final LocalDateTime plusMonth = localDateTime.plusMonths(1);
        return cryptoRepository.findBySymbolBetweenDays(symbol, localDateTime, plusMonth);
    }
}
