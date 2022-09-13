package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Crypto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TSTAMP")
    private LocalDateTime timestamp;

    private String symbol;

    private BigDecimal price;

    public Crypto(LocalDateTime timestamp, String symbol, BigDecimal price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

}
