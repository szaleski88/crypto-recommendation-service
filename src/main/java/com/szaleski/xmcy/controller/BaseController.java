package com.szaleski.xmcy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.szaleski.xmcy.service.CryptoService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class BaseController {

    private final CryptoService cryptoService;

    @GetMapping
    public String helloPage() {
        return "index";
    }

    @GetMapping("/crypto")
    public String getAvailableCryptos(Model model) {
        model.addAttribute("cryptos", cryptoService.getAvailableCryptos());
        return "cryptoList";
    }

    @GetMapping(value = "/crypto/{symbol}")
    public String getCryptoBySymbol(@PathVariable String symbol, Model model) {
        model.addAttribute("cryptoSymbol", symbol);
        model.addAttribute("crypto", cryptoService.getCryptoBySymbol(symbol));
        return "cryptoData";
    }

}
