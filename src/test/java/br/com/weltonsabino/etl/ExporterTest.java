package br.com.weltonsabino.etl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldWriteCsvWithHeaderAndEscapedValues() throws Exception {
        Path outputFile = tempDir.resolve("saida/teste.csv");

        List<Row> rows = List.of(
                new Row(2025, "Janeiro", "Florianópolis", "Empresa \"LTDA\"", "Sul", "Sim", "SC", "Microempresa", "Ativa", 10)
        );

        Exporter.writeCsv(rows, outputFile);

        String content = Files.readString(outputFile);

        assertTrue(content.contains("ano_abertura,mes_abertura,municipio,natureza_juridica,regiao,opcao_mei,uf,porte,tipo_situacao,quantidade_empresas"));
        assertTrue(content.contains("\"2025\",\"Janeiro\",\"Florianópolis\",\"Empresa \"\"LTDA\"\"\",\"Sul\",\"Sim\",\"SC\",\"Microempresa\",\"Ativa\",\"10\""));
    }
}