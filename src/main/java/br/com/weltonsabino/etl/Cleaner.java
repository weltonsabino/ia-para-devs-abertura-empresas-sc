package br.com.weltonsabino.etl;

import java.util.ArrayList;
import java.util.List;

public class Cleaner {

    public static List<Row> clean(List<Row> rawRows) {
        List<Row> cleanRows = new ArrayList<>();

        for (Row row : rawRows) {
            if (row == null) {
                continue;
            }

            String municipio = normalize(row.municipio());
            String naturezaJuridica = normalize(row.naturezaJuridica());
            String regiao = normalize(row.regiao());
            String opcaoMei = normalize(row.opcaoMei());
            String uf = normalize(row.uf());
            String porte = normalize(row.porte());
            String tipoSituacao = normalize(row.tipoSituacao());
            String mesAbertura = normalize(row.mesAbertura());

            if (municipio.isBlank()) {
                continue;
            }

            if (!"SC".equalsIgnoreCase(uf)) {
                continue;
            }

            if (row.quantidadeEmpresas() <= 0) {
                continue;
            }

            cleanRows.add(new Row(
                    row.anoAbertura(),
                    mesAbertura,
                    municipio,
                    naturezaJuridica,
                    regiao,
                    opcaoMei,
                    uf.toUpperCase(),
                    porte,
                    tipoSituacao,
                    row.quantidadeEmpresas()
            ));
        }

        return cleanRows;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}