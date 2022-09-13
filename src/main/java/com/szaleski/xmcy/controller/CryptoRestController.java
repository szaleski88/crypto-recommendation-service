package com.szaleski.xmcy.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.szaleski.xmcy.model.CryptoDto;
import com.szaleski.xmcy.model.CryptoReport;
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

    @Operation(summary = "Get data dump for given crypto")
    @GetMapping(value = "/{symbol}/data")
    public List<CryptoDto> getCryptoBySymbol(@Parameter(example = "BTC") @PathVariable String symbol) {
        return cryptoService.getCryptoBySymbol(symbol);
    }

    @Operation(summary = "Get MAX value of given crypto")

    @GetMapping(value = "/{symbol}/max")
    public CryptoDto getMaxValueForCrypto(@Parameter(example = "BTC") @PathVariable String symbol) {
        return cryptoService.getMaxValue(symbol);
    }

    @Operation(summary = "Get MIN value of given crypto")

    @GetMapping(value = "/{symbol}/min")
    public CryptoDto getMinValueForCrypto(@Parameter(example = "BTC") @PathVariable String symbol) {
        return cryptoService.getMinValue(symbol);
    }

    @Operation(summary = "Get OLDEST value of given crypto")

    @GetMapping(value = "/{symbol}/oldest")
    public CryptoDto getOldestValueForCrypto(@Parameter(example = "BTC") @PathVariable String symbol) {
        return cryptoService.getOldest(symbol);
    }

    @Operation(summary = "Get VALUE value of given crypto")

    @GetMapping(value = "/{symbol}/newest")
    public CryptoDto getNewestValueForCrypto(@Parameter(example = "BTC") @PathVariable String symbol) {
        return cryptoService.getNewest(symbol);
    }

    @Operation(summary = "Get NORMALIZED value of given crypto for given day")

    @GetMapping(value = "/{symbol}/normalized")
    public BigDecimal getNormalizedValueForCrypto(@Parameter(example = "BTC") @PathVariable String symbol,
                                                  @Parameter(example = "2022-01-01")
                                                  @RequestParam(name = "date")
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return cryptoService.getNormalizedForSymbolAndDate(symbol, date);
    }

    @Operation(summary = "Get monthly report of given crypto")

    @GetMapping(value = "/{symbol}/report")
    public CryptoReport getCryptoReport(@Parameter(example = "BTC") @PathVariable String symbol,
                                        @Parameter(example = "2022-01") @RequestParam(name = "month") @DateTimeFormat(pattern = "yyyy-MM") Date date) {
        return reportGenerator.generateReportFor(symbol, date);
    }

}
