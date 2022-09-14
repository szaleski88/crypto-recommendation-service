package com.szaleski.xmcy.controller;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.model.CryptoData;
import com.szaleski.xmcy.model.CryptoReport;
import com.szaleski.xmcy.model.HighestNormalizedRange;
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

    @Operation(summary = "Get report of given crypto")
    @GetMapping(value = "/{currencySymbol}/report")
    public CryptoReport getCryptoReport(@Parameter(example = "BTC") @PathVariable String currencySymbol,
                                        @Parameter(example = "2022-01") @RequestParam(name = "month", required = false) @DateTimeFormat(pattern = "yyyy-MM") Date date) {
        List<CryptoData> reportData;

        if (Objects.isNull(date)) {
            reportData = cryptoService.getCryptoBySymbol(currencySymbol);
        } else {
            reportData = cryptoService.getCryptoDataBySymbolForMonth(currencySymbol, date);
        }

        return reportGenerator.generateReportFor(currencySymbol, reportData, date);
    }

    @Operation(summary = "Get normalized ranges of all available currencies")
    @GetMapping(value = "/normalizedRanges")
    public NormalizedRanges getNormalizedRangeForAll() {
        return cryptoService.getNormalizedRangesForAll();
    }

    @Operation(summary = "Get NORMALIZED value of given crypto for given day")
    @GetMapping(value = "/highestNormalizedRange/{date}")
    public HighestNormalizedRange getHighestNormalizedRangeForDay(@Parameter(example = "2022-01-01")
                                                                  @PathVariable(name = "date")
                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return cryptoService.getHighestNormalizedRangeForDay(date);
    }

}
