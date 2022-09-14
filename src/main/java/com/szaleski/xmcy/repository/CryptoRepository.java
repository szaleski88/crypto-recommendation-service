package com.szaleski.xmcy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.szaleski.xmcy.model.Crypto;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    List<Crypto> findBySymbol(String symbol);

    @Query(value = "SELECT DISTINCT symbol FROM CRYPTO", nativeQuery = true)
    List<String> findDistinctSymbols();

    @Query(value = "SELECT * FROM CRYPTO c WHERE c.symbol = :symbol AND c.tstamp >= :fromDay and c.tstamp < :toDay", nativeQuery = true)
    List<Crypto> findBySymbolBetweenDays(@Param("symbol") String symbol, @Param("fromDay") LocalDateTime fromDay, @Param("toDay") LocalDateTime toDay);

    @Query(value = "SELECT * FROM CRYPTO c WHERE c.tstamp >= :fromDay and c.tstamp < :toDay", nativeQuery = true)
    List<Crypto> findBetweenDays(@Param("fromDay") LocalDateTime fromDay, @Param("toDay") LocalDateTime toDay);

}
