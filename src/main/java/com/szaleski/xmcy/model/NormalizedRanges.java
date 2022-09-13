package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NormalizedRanges {

    final LinkedHashMap<String, BigDecimal> normalizedRanges;

}
