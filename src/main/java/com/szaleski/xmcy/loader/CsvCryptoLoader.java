package com.szaleski.xmcy.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.szaleski.xmcy.model.Crypto;
import com.szaleski.xmcy.utils.DateUtils;

@Component
public class CsvCryptoLoader {

    public List<Crypto> readCryptoFromCsv(Path pathToFile) {
        List<Crypto> cryptos = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            br.readLine(); // skip the header

            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                Crypto crypto = toCryptoDto(attributes);
                cryptos.add(crypto);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return cryptos;
    }

    private static Crypto toCryptoDto(String[] metadata) {
        return new Crypto(DateUtils.strMillisToLocalDateTime(metadata[0]), metadata[1], new BigDecimal(metadata[2]));
    }

}
