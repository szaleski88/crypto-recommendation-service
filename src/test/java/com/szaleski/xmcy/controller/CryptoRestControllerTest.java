package com.szaleski.xmcy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

import lombok.RequiredArgsConstructor;

@WebMvcTest
@RequiredArgsConstructor
@Profile("test")
class CryptoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

}