package com.szaleski.xmcy.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    public void invalidValueFor_Month_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?year=2022&month=-1")).andReturn();

        then(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        then(mvcResult.getResolvedException().getMessage()).contains("Month must be between 1 or 12");
    }

    @Test
    public void invalidValueFor_Year_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?month=1&year=2020")).andReturn();

        then(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        then(mvcResult.getResolvedException().getMessage()).contains("Year shouldn't be before 2022");
    }

    @Test
    public void invalidValueFor_YearAndMonth_parametersReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?month=-1&year=2020")).andReturn();

        then(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        then(mvcResult.getResolvedException().getMessage()).contains("Year shouldn't be before 2022", "Month must be between 1 or 12");
    }

    @Test
    public void missing_Month_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'month' for method parameter type int is not present"));
    }

    @Test
    public void missing_Year_parameterReturnsBadRequest() throws Exception {
        // given
        given(cryptoService.getCryptoDataBySymbolForMonth(anyString(), any(LocalDate.class))).willReturn(List.of());

        // when
        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?month=1"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Required request parameter 'year' for method parameter type int is not present"));
    }

    @Test
    public void dateConvertedWhenProperParamsUsed_reportIsReturned() throws Exception {
        // given
        CryptoReport cryptoReport = CryptoReport.builder()
                                                .currency("CUR")
                                                .normalizedValue(BigDecimal.TEN)
                                                .build();
        given(reportGenerator.generateReportFor(anyString(), any())).willReturn(cryptoReport);

        // when

        // then
        mockMvc.perform(get("/api/v1/crypto/BTC/monthlyReport?month=01&year=2022"))
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