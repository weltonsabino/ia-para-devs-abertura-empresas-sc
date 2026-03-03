package br.com.weltonsabino.etl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static List<Row> load(Path csvPath) throws Exception {
        if (!Files.exists(csvPath)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + csvPath.toAbsolutePath());
        }

        List<Row> rows = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            for (CSVRecord r : records) {
                String municipio = r.get("municipio");
                String segmento = r.get("segmento");
                String data = r.get("data_abertura");
                String qtd = r.get("qtd_aberturas");

                YearMonth ym = YearMonth.parse(data); // formato YYYY-MM
                int qtdInt = Integer.parseInt(qtd);

                rows.add(new Row(municipio, segmento, ym, qtdInt));
            }
        }

        return rows;
    }
}