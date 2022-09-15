package com.szaleski.xmcy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.szaleski.xmcy.loader.CsvCryptoLoader;
import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.repository.CryptoRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@Profile("dev")
public class DataLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    private final ResourceLoader resourceLoader;
    private final CsvCryptoLoader csvCryptoLoader;
    private final CryptoRepository cryptoRepository;

    public void run(ApplicationArguments args) throws IOException {
        final List<String> dataFiles = List.of("BTC_values.csv", "DOGE_values.csv", "ETH_values.csv", "LTC_values.csv", "XRP_values.csv", "TEST_values.csv");

        for (final String fname : dataFiles) {
            loadToDb(fname);
        }

    }

    private void loadToDb(final String fname) throws IOException {
        final InputStream inputStream = resourceLoader.getResource(String.format("classpath:/initial_data/%s", fname)).getInputStream();

        // for (final File file : Objects.requireNonNull(path.listFiles())) {
        LOG.info("Loading currency data from: {}", fname);
        final List<Crypto> cryptos = csvCryptoLoader.readCryptoFromCsv(inputStream);
        cryptoRepository.saveAllAndFlush(cryptos);
        // cryptos.forEach(c -> System.out.println(c.getTimestamp()));
        // }
    }

    // public static String readFile(File file) throws IOException {
    //     StringBuilder sb = new StringBuilder();
    //     InputStream in = new FileInputStream(file);
    //     BufferedReader br = new BufferedReader(new InputStreamReader(in));
    //
    //     String line;
    //     while ((line = br.readLine()) != null) {
    //         sb.append(line + System.lineSeparator());
    //     }
    //
    //     return sb.toString();
    // }
}