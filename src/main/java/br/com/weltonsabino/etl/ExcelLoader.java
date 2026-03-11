package br.com.weltonsabino.etl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

            skipHeader(iterator);

            while (iterator.hasNext()) {
                org.apache.poi.ss.usermodel.Row excelRow = iterator.next();
                Row row = mapRow(excelRow);

                if (row != null) {
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    private void skipHeader(Iterator<org.apache.poi.ss.usermodel.Row> iterator) {
        if (iterator.hasNext()) {
            iterator.next();
        }
    }

    private Row mapRow(org.apache.poi.ss.usermodel.Row excelRow) {
        String anoTexto = getCellValueAsString(excelRow, 0);
        if (anoTexto.isBlank() || "Totais".equalsIgnoreCase(anoTexto)) {
            return null;
        }

        String mesAbertura = getCellValueAsString(excelRow, 1);
        String municipio = getCellValueAsString(excelRow, 2);
        String naturezaJuridica = getCellValueAsString(excelRow, 3);
        String regiao = getCellValueAsString(excelRow, 4);
        String opcaoMei = getCellValueAsString(excelRow, 5);
        String uf = getCellValueAsString(excelRow, 6);
        String porte = getCellValueAsString(excelRow, 7);
        String tipoSituacao = getCellValueAsString(excelRow, 8);
        String quantidadeTexto = getCellValueAsString(excelRow, 9);

        int anoAbertura = parseInteger(anoTexto);
        int quantidadeEmpresas = parseInteger(quantidadeTexto);

        return new Row(
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
        );
    }

    private String getCellValueAsString(org.apache.poi.ss.usermodel.Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
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
            case FORMULA -> cell.getCellFormula().trim();
            case BLANK -> "";
            default -> "";
        };
    }

    private int parseInteger(String value) {
        return Integer.parseInt(value.trim());
    }
}