package com.szaleski.xmcy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.model.CryptoData;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    List<CryptoData> findBySymbol(String symbol);

    @Query(value = "SELECT DISTINCT symbol FROM CRYPTO", nativeQuery = true)
    List<String> findDistinctSymbols();

    /**
     * @param symbol  currency symbol
     * @param fromDay FROM day (inclusive)
     * @param toDay   TO day (exclusive)
     */
    @Query(name = "findBySymbolBetweenDays", nativeQuery = true)
    List<CryptoData> findBySymbolBetweenDays(@Param("symbol") String symbol, @Param("fromDay") LocalDateTime fromDay, @Param("toDay") LocalDateTime toDay);

    /**
     * @param fromDay FROM day (inclusive)
     * @param toDay   TO day (exclusive)
     */
    @Query(name = "findBetweenDays", nativeQuery = true)
    List<CryptoData> findBetweenDays(@Param("fromDay") LocalDateTime fromDay, @Param("toDay") LocalDateTime toDay);

}
