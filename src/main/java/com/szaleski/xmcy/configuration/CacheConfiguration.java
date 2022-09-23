package com.szaleski.xmcy.configuration;

import static com.szaleski.xmcy.configuration.CacheConstants.DATABASE_CACHE;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.Data;

@Data
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CaffeineCache databaseCache() {
        return new CaffeineCache(DATABASE_CACHE, Caffeine.newBuilder()
                                                         .expireAfterWrite(5, TimeUnit.SECONDS).build());
    }

}
