package com.szaleski.xmcy.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.szaleski.xmcy.model.CryptoReport;
import com.szaleski.xmcy.service.CryptoService;
import com.szaleski.xmcy.utils.CryptoReportGenerator;

@Controller
@RequestMapping("/crypto/")
public class CryptoController {

    private final CryptoService cryptoService;
    private final CryptoReportGenerator reportGenerator;

    public CryptoController(CryptoService cryptoService, CryptoReportGenerator cryptoReportGenerator) {
        this.cryptoService = cryptoService;
        this.reportGenerator = cryptoReportGenerator;
    }

    @GetMapping(value = "/{symbol}")
    public String getCryptoBySymbol(@PathVariable String symbol, Model model) {
        model.addAttribute("cryptoSymbol", symbol);
        model.addAttribute("crypto", cryptoService.getCryptoBySymbol(symbol));
        return "cryptoData";
    }

    @GetMapping(value = "/{symbol}/report")
    public String getCryptoReportBySymbol(@PathVariable String symbol,
                                          @RequestParam(name = "month") @DateTimeFormat(pattern = "yyyy-MM") Date date,
                                          Model model) {
        CryptoReport cryptoReport = reportGenerator.generateReportFor(symbol, date);
        model.addAttribute("cryptoReport", cryptoReport);

        return "cryptoReport";
    }

}
