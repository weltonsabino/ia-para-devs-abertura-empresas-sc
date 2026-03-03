package br.com.weltonsabino.etl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Exporter {

    public static void writeCsv(List<Row> rows, Path output) throws Exception {
        Files.createDirectories(output.getParent());

        try (Writer w = Files.newBufferedWriter(output);
             CSVPrinter printer = new CSVPrinter(w, CSVFormat.DEFAULT
                     .builder()
                     .setHeader("municipio", "segmento", "data_abertura", "qtd_aberturas")
                     .build())) {

            for (Row r : rows) {
                printer.printRecord(
                        r.municipio(),
                        r.segmento(),
                        r.dataAbertura().toString(), // YYYY-MM
                        r.qtdAberturas()
                );
            }
        }
    }
}