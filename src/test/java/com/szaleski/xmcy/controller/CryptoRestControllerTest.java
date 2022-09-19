package com.szaleski.xmcy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;

import com.szaleski.xmcy.exceptions.CryptoDataNotAvailableException;
import com.szaleski.xmcy.model.CryptoReport;
import com.szaleski.xmcy.service.CryptoService;
import com.szaleski.xmcy.utils.CryptoReportGenerator;

@WebMvcTest(controllers = {CryptoRestController.class})
class CryptoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoService cryptoService;

    @MockBean
    private CryptoReportGenerator reportGenerator;

    @MockBean
    private CacheManager cacheManager;

    @Test
    public void missing_Month_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'month' for method parameter type Date is not present"));

    }

    @Test
    public void dateConvertedWhenProperParamsUsed_reportIsReturned() throws Exception {
        // given
        CryptoReport cryptoReport = CryptoReport.builder()
                                                .currency("CUR")
                                                .normalizedValue(BigDecimal.TEN)
                                                .build();
        given(reportGenerator.generateReportFor(anyString(), any(), any(LocalDate.class))).willReturn(cryptoReport);

        // when

        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?month=2022-01"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("currency").value(cryptoReport.getCurrency()))
               .andExpect(jsonPath("normalizedValue").value(cryptoReport.getNormalizedValue()));

        verify(cryptoService).getCryptoDataBySymbolForMonth("BTC", LocalDate.of(2022, 1, 1));
    }

    @Test
    public void missing_parameters_returnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForRange(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/customReport"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'dateFrom' for method parameter type Date is not present"));

    }

    @Test
    public void missing_dateFrom_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForRange(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/customReport?dateTo=2022-01-01"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'dateFrom' for method parameter type Date is not present"));

    }

    @Test
    public void missing_dateTo_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForRange(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/customReport?dateFrom=2022-01-01"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'dateTo' for method parameter type Date is not present"));

    }

    @Test
    public void invalid_dateFrom_format() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForRange(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/customReport?dateFrom=2022-01&dateTo=2022-01-01"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Invalid parameter 'dateFrom' value. [Unparseable date: \"2022-01\"]"));

    }

    @Test
    public void invalid_dateTo_format() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForRange(anyString(), any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/customReport?dateFrom=2022-01-01&dateTo=2022-01"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Invalid parameter 'dateTo' value. [Unparseable date: \"2022-01\"]"));
    }

    @Test
    public void httpNotFoundWhenExceptionThrownOnGetNormalizedRangesForAll() throws Exception {
        // given
        given(cryptoService.getNormalizedRangesForAll()).willThrow(CryptoDataNotAvailableException.class);

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/normalizedRanges"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void missing_date_onGetHighestNormalizedRangeReturnsBadRequest() throws Exception {
        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/highestNormalizedRange"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'date' for method parameter type Date is not present"));

    }

    @Test
    public void invalid_date_formatGetHighestNormalizedRangeReturnsBadRequest() throws Exception {
        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/highestNormalizedRange?date=2022-01"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Invalid parameter 'date' value. [Unparseable date: \"2022-01\"]"));
    }

}