package com.szaleski.xmcy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Crypto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@NamedNativeQuery(name = "findBySymbolBetweenDays",
                  query = "SELECT c.tstamp, c.symbol, c.price FROM CRYPTO c WHERE c.symbol = :symbol AND c.tstamp >= :fromDay and c.tstamp <= :toDay",
                  resultSetMapping = "crypto_data")
@NamedNativeQuery(name = "findBetweenDays",
                  query = "SELECT * FROM CRYPTO c WHERE c.tstamp >= :fromDay and c.tstamp < :toDay",
                  resultSetMapping = "crypto_data")
@SqlResultSetMapping(
    name = "crypto_data",
    classes = @ConstructorResult(
        targetClass = CryptoData.class,
        columns = {
            @ColumnResult(name = "tstamp", type = LocalDateTime.class),
            @ColumnResult(name = "symbol", type = String.class),
            @ColumnResult(name = "price", type = BigDecimal.class)
        }
    )
)
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final Crypto crypto = (Crypto) o;

        return new EqualsBuilder().append(timestamp, crypto.timestamp).append(symbol, crypto.symbol).append(price, crypto.price)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(timestamp).append(symbol).append(price).toHashCode();
    }
}
