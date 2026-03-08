package br.com.weltonsabino.etl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelLoader {

    public List<Row> load(String filePath) throws IOException {
        List<Row> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<org.apache.poi.ss.usermodel.Row> iterator = sheet.iterator();

            if (iterator.hasNext()) {
                iterator.next(); // pula cabeçalho
            }

            while (iterator.hasNext()) {
                org.apache.poi.ss.usermodel.Row excelRow = iterator.next();

                String anoTexto = getCellValueAsString(excelRow.getCell(0));
                String mesAbertura = getCellValueAsString(excelRow.getCell(1));
                String municipio = getCellValueAsString(excelRow.getCell(2));
                String naturezaJuridica = getCellValueAsString(excelRow.getCell(3));
                String regiao = getCellValueAsString(excelRow.getCell(4));
                String opcaoMei = getCellValueAsString(excelRow.getCell(5));
                String uf = getCellValueAsString(excelRow.getCell(6));
                String porte = getCellValueAsString(excelRow.getCell(7));
                String tipoSituacao = getCellValueAsString(excelRow.getCell(8));
                String quantidadeTexto = getCellValueAsString(excelRow.getCell(9));

                if (anoTexto == null || anoTexto.isBlank()) {
                    continue;
                }

                if ("Totais".equalsIgnoreCase(anoTexto.trim())) {
                    continue;
                }

                int anoAbertura = Integer.parseInt(anoTexto.trim());
                int quantidadeEmpresas = Integer.parseInt(quantidadeTexto.trim());

                rows.add(new Row(
                        anoAbertura,
                        mesAbertura,
                        municipio,
                        naturezaJuridica,
                        regiao,
                        opcaoMei,
                        uf,
                        porte,
                        tipoSituacao,
                        quantidadeEmpresas
                ));
            }
        }

        return rows;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }
}