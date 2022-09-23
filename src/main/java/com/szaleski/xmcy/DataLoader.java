package com.szaleski.xmcy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.szaleski.xmcy.loader.CsvCryptoLoader;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.repository.CryptoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "crypto.initial-data", value = "load")
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    @Value("${crypto.initial-data.path}")
    private String pathToResources;

    private final ResourceLoader resourceLoader;
    private final CsvCryptoLoader csvCryptoLoader;
    private final CryptoRepository cryptoRepository;

    public void run(ApplicationArguments args) throws IOException {
        final File path = pathToResources.toLowerCase().startsWith("classpath") ?
                          resourceLoader.getResource(pathToResources).getFile() :
                          Paths.get(pathToResources).toFile();

        if (path.listFiles() == null) {
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