package com.szaleski.xmcy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.szaleski.xmcy.loader.CsvCryptoLoader;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.repository.CryptoRepository;

@Component
@Profile("dev")
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    @Value("${crypto.initial.data}")
    private String pathToResources;

    private final ResourceLoader resourceLoader;
    private final CsvCryptoLoader csvCryptoLoader;
    private final CryptoRepository cryptoRepository;

    public DataLoader(ResourceLoader resourceLoader,
                      CsvCryptoLoader csvCryptoLoader,
                      CryptoRepository cryptoRepository) {
        this.resourceLoader = resourceLoader;
        this.csvCryptoLoader = csvCryptoLoader;
        this.cryptoRepository = cryptoRepository;
    }

    public void run(ApplicationArguments args) throws IOException {
        final File path = resourceLoader.getResource(pathToResources).getFile();

        if(path.listFiles() == null) {
            LOG.info("No resources to load");
            return;
        }

        for (final File file : Objects.requireNonNull(path.listFiles())) {
            LOG.info("Loading currency data from: {}", file.getName());
            final List<Crypto> cryptos = csvCryptoLoader.readCryptoFromCsv(file.toPath());
            cryptoRepository.saveAllAndFlush(cryptos);
            // cryptos.forEach(c -> System.out.println(c.getTimestamp()));
        }
    }
}