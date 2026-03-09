package br.com.weltonsabino.etl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Exporter {

    public static void writeCsv(List<Row> rows, Path outputPath) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("ano_abertura,mes_abertura,municipio,natureza_juridica,regiao,opcao_mei,uf,porte,tipo_situacao,quantidade_empresas");
            writer.newLine();

            for (Row row : rows) {
                writer.write(String.join(",",
                        escape(String.valueOf(row.anoAbertura())),
                        escape(row.mesAbertura()),
                        escape(row.municipio()),
                        escape(row.naturezaJuridica()),
                        escape(row.regiao()),
                        escape(row.opcaoMei()),
                        escape(row.uf()),
                        escape(row.porte()),
                        escape(row.tipoSituacao()),
                        escape(String.valueOf(row.quantidadeEmpresas()))
                ));
                writer.newLine();
            }
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}