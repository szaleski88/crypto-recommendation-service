package com.szaleski.xmcy.utils;

import static com.szaleski.xmcy.model.PriceData.from;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.CryptoReport;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CryptoReportGenerator {

    private final CryptoFilter cryptoFilter;
    private final CryptoDataRangeNormalizer cryptoDataRangeNormalizer;

    public CryptoReport generateReportFor(String symbol, List<CryptoData> dataForReport) {
        if(dataForReport.isEmpty()) {
            return new CryptoReport();
        }
        
        final CryptoData min = cryptoFilter.getMin(dataForReport);
        final CryptoData max = cryptoFilter.getMax(dataForReport);
        final CryptoData oldest = cryptoFilter.getOldest(dataForReport);
        final CryptoData newest = cryptoFilter.getNewest(dataForReport);
        final BigDecimal normalizedPrice = cryptoDataRangeNormalizer.getSingleNormalizedRange(dataForReport);

        final CryptoReport report = new CryptoReport();

        report.setCurrency(symbol);
        report.setFromDate(oldest.getTimestamp());
        report.setToDate(newest.getTimestamp());
        report.setNormalizedValue(normalizedPrice);

        report.setOldestPrice(from(oldest));
        report.setNewestPrice(from(newest));
        report.setMaxPrice(from(max));
        report.setMinPrice(from(min));

        return report;
    }

}
