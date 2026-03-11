package br.com.weltonsabino.etl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadRowsFromExcel() throws Exception {
        Path file = tempDir.resolve("teste.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file.toFile())) {

            var sheet = workbook.createSheet();

            createHeader(sheet.createRow(0));

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue(2025);
            data.createCell(1).setCellValue("Janeiro");
            data.createCell(2).setCellValue("Florianópolis");
            data.createCell(3).setCellValue("LTDA");
            data.createCell(4).setCellValue("Sul");
            data.createCell(5).setCellValue("Sim");
            data.createCell(6).setCellValue("SC");
            data.createCell(7).setCellValue("Microempresa");
            data.createCell(8).setCellValue("Ativa");
            data.createCell(9).setCellValue(10);

            workbook.write(out);
        }

        ExcelLoader loader = new ExcelLoader();
        List<br.com.weltonsabino.etl.Row> rows = loader.load(file.toString());

        assertEquals(1, rows.size());

        br.com.weltonsabino.etl.Row row = rows.get(0);
        assertEquals(2025, row.anoAbertura());
        assertEquals("Janeiro", row.mesAbertura());
        assertEquals("Florianópolis", row.municipio());
        assertEquals("LTDA", row.naturezaJuridica());
        assertEquals("Sul", row.regiao());
        assertEquals("Sim", row.opcaoMei());
        assertEquals("SC", row.uf());
        assertEquals("Microempresa", row.porte());
        assertEquals("Ativa", row.tipoSituacao());
        assertEquals(10, row.quantidadeEmpresas());
    }

    @Test
    void shouldIgnoreTotaisRow() throws Exception {
        Path file = tempDir.resolve("teste-totais.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file.toFile())) {

            var sheet = workbook.createSheet();

            createHeader(sheet.createRow(0));

            Row totais = sheet.createRow(1);
            totais.createCell(0).setCellValue("Totais");
            totais.createCell(1).setCellValue("Janeiro");
            totais.createCell(9).setCellValue(999);

            Row data = sheet.createRow(2);
            data.createCell(0).setCellValue(2025);
            data.createCell(1).setCellValue("Fevereiro");
            data.createCell(2).setCellValue("Joinville");
            data.createCell(3).setCellValue("MEI");
            data.createCell(4).setCellValue("Norte");
            data.createCell(5).setCellValue("Não");
            data.createCell(6).setCellValue("SC");
            data.createCell(7).setCellValue("Microempresa");
            data.createCell(8).setCellValue("Ativa");
            data.createCell(9).setCellValue(20);

            workbook.write(out);
        }

        ExcelLoader loader = new ExcelLoader();
        List<br.com.weltonsabino.etl.Row> rows = loader.load(file.toString());

        assertEquals(1, rows.size());
        assertEquals(2025, rows.get(0).anoAbertura());
        assertEquals("Joinville", rows.get(0).municipio());
        assertEquals(20, rows.get(0).quantidadeEmpresas());
    }

    @Test
    void shouldLoadRowWithBlankCells() throws Exception {
        Path file = tempDir.resolve("teste-celulas-vazias.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file.toFile())) {

            var sheet = workbook.createSheet();

            createHeader(sheet.createRow(0));

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue(2025);
            data.createCell(1).setCellValue("Março");
            data.createCell(2).setCellValue("Blumenau");
            data.createCell(9).setCellValue(15);

            workbook.write(out);
        }

        ExcelLoader loader = new ExcelLoader();
        List<br.com.weltonsabino.etl.Row> rows = loader.load(file.toString());

        assertEquals(1, rows.size());

        br.com.weltonsabino.etl.Row row = rows.get(0);
        assertEquals(2025, row.anoAbertura());
        assertEquals("Março", row.mesAbertura());
        assertEquals("Blumenau", row.municipio());
        assertEquals("", row.naturezaJuridica());
        assertEquals("", row.regiao());
        assertEquals("", row.opcaoMei());
        assertEquals("", row.uf());
        assertEquals("", row.porte());
        assertEquals("", row.tipoSituacao());
        assertEquals(15, row.quantidadeEmpresas());
    }

    private void createHeader(Row header) {
        header.createCell(0).setCellValue("ano_abertura");
        header.createCell(1).setCellValue("mes_abertura");
        header.createCell(2).setCellValue("municipio");
        header.createCell(3).setCellValue("natureza_juridica");
        header.createCell(4).setCellValue("regiao");
        header.createCell(5).setCellValue("opcao_mei");
        header.createCell(6).setCellValue("uf");
        header.createCell(7).setCellValue("porte");
        header.createCell(8).setCellValue("tipo_situacao");
        header.createCell(9).setCellValue("quantidade_empresas");
    }
}