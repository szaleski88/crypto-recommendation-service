package com.szaleski.xmcy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.szaleski.xmcy.loader.CsvCryptoLoader;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.repository.CryptoRepository;

@Component
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    @Value("${crypto.initial.data}")
    private String pathToResources;

    private ResourceLoader resourceLoader;
    private CsvCryptoLoader csvCryptoLoader;
    private CryptoRepository cryptoRepository;

    public DataLoader(ResourceLoader resourceLoader,
                      CsvCryptoLoader csvCryptoLoader,
                      CryptoRepository cryptoRepository) {
        this.resourceLoader = resourceLoader;
        this.csvCryptoLoader = csvCryptoLoader;
        this.cryptoRepository = cryptoRepository;
    }

    public void run(ApplicationArguments args) throws IOException {
        final File path = resourceLoader.getResource(pathToResources).getFile();

        for (final File file : Objects.requireNonNull(path.listFiles())) {
            LOG.info("Loading {}", file);
            final List<Crypto> cryptos = csvCryptoLoader.readCryptoFromCsv(file.toPath());
            cryptoRepository.saveAllAndFlush(cryptos);
            cryptos.forEach(c -> System.out.println(c.getTimestamp()));
        }
    }
}