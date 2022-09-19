package com.szaleski.xmcy.controller;

import static com.szaleski.xmcy.utils.DateUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.CryptoReport;
import com.szaleski.xmcy.model.NormalizedRanges;
import com.szaleski.xmcy.service.CryptoService;
import com.szaleski.xmcy.utils.CryptoReportGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/crypto")
@AllArgsConstructor
public class CryptoRestController {

    private CryptoService cryptoService;
    private CryptoReportGenerator reportGenerator;

    @Operation(summary = "Get monthly report of given crypto")
    @GetMapping(value = "/{currencySymbol}/monthlyReport")
    public CryptoReport getCryptoReportForMonth(@Parameter(example = "BTC") @PathVariable String currencySymbol,
                                                @Parameter(example = "2022-01")
                                                @RequestParam(name = "month")
                                                @DateTimeFormat(pattern = "yyyy-MM") @Valid Date month) {
        List<CryptoData> reportData;

        if (Objects.isNull(month)) {
            reportData = cryptoService.getCryptoBySymbol(currencySymbol);
        } else {
            reportData = cryptoService.getCryptoDataBySymbolForMonth(currencySymbol, toLocalDateTime(month));
        }

        return reportGenerator.generateReportFor(currencySymbol, reportData, toLocalDateTime(month));
    }

    @Operation(summary = "Get report of given crypto for custom date range")
    @GetMapping(value = "/{currencySymbol}/customReport")
    public CryptoReport getCryptoReport(@Parameter(example = "BTC") @PathVariable String currencySymbol,
                                        @Parameter(example = "2022-01-01") @RequestParam(name = "dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dddd") @Valid Date dateFrom,
                                        @Parameter(example = "2022-01-02") @RequestParam(name = "dateTo") @DateTimeFormat(pattern = "yyyy-MM-dddd") @Valid Date dateTo) {
        LocalDateTime asLocalDateTime = toLocalDateTime(dateFrom);

        List<CryptoData>
            reportData = cryptoService.getCryptoDataBySymbolForRange(currencySymbol, asLocalDateTime, toLocalDateTime(dateTo));

        return reportGenerator.generateReportFor(currencySymbol, reportData, asLocalDateTime);
    }

    @Operation(summary = "Get normalized ranges of all available currencies")
    @GetMapping(value = "/normalizedRanges")
    public NormalizedRanges getNormalizedRangeForAll() {
        return cryptoService.getNormalizedRangesForAll();
    }

    @Operation(summary = "Get NORMALIZED value of given crypto for given day")
    @GetMapping(value = "/highestNormalizedRange/{date}")
    public NormalizedRanges getHighestNormalizedRangeForDay(@Parameter(example = "2022-01-01")
                                                                  @PathVariable(name = "date")
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @Valid Date date) {
        return cryptoService.getHighestNormalizedRangeForDay(toLocalDateTime(date));
    }

}
