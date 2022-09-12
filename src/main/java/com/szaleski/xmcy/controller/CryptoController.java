package com.szaleski.xmcy.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.service.CryptoService;
import com.szaleski.xmcy.utils.DateUtils;

@RestController
@RequestMapping("/api/v1/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping(value = "/{symbol}")
    public List<Crypto> getCryptoBySymbol(@PathVariable String symbol) {
        return cryptoService.getCryptoBySymbol(symbol);
    }

    @GetMapping(value = "/{symbol}/max")
    public Crypto getMaxValueForCrypto(@PathVariable String symbol) {
        return cryptoService.getMaxValue(symbol);
    }

    @GetMapping(value = "/{symbol}/min")
    public Crypto getMinValueForCrypto(@PathVariable String symbol) {
        return cryptoService.getMinValue(symbol);
    }

    @GetMapping(value = "/{symbol}/oldest")
    public Crypto getOldestValueForCrypto(@PathVariable String symbol) {
        return cryptoService.getOldest(symbol);
    }

    @GetMapping(value = "/{symbol}/newest")
    public Crypto getNewestValueForCrypto(@PathVariable String symbol) {
        return cryptoService.getNewest(symbol);
    }

    @GetMapping(value = "/{symbol}/normalized")
    public BigDecimal getNormalizedValueForCrypto(@PathVariable String symbol, @RequestParam(required = false) Date date) {
        if(Objects.nonNull(date)) {
            return cryptoService.getNormalizedForDate(symbol, date);
        }
        return cryptoService.getNormalized(symbol);
    }

}
