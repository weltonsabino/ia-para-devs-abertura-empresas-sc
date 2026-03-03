package br.com.weltonsabino.analysis;

import br.com.weltonsabino.db.DuckDb;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

    private final DuckDb db;

    public Analyzer(DuckDb db) {
        this.db = db;
    }

    public void generateSummary(Path out) throws Exception {
        Files.createDirectories(out.getParent());

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("# Relatório - Abertura de Empresas em SC\n\n");
            w.write("Este relatório foi gerado automaticamente a partir do dataset tratado e carregado no DuckDB.\n\n");

            // Total
            int total = singleInt("SELECT SUM(qtd_aberturas) AS total FROM aberturas_empresas_sc");
            w.write("## Visão geral\n");
            w.write("- Total de aberturas no recorte do dataset: **" + total + "**\n\n");

            // Top municípios
            w.write("## Top 10 municípios por aberturas\n");
            try (ResultSet rs = db.query("""
                    SELECT municipio, SUM(qtd_aberturas) AS total
                    FROM aberturas_empresas_sc
                    GROUP BY municipio
                    ORDER BY total DESC
                    LIMIT 10
            """)) {
                while (rs.next()) {
                    w.write("- " + rs.getString("municipio") + ": **" + rs.getInt("total") + "**\n");
                }
            }
            w.write("\n");

            // Segmentos
            w.write("## Aberturas por segmento\n");
            try (ResultSet rs = db.query("""
                    SELECT segmento, SUM(qtd_aberturas) AS total
                    FROM aberturas_empresas_sc
                    GROUP BY segmento
                    ORDER BY total DESC
            """)) {
                while (rs.next()) {
                    w.write("- " + rs.getString("segmento") + ": **" + rs.getInt("total") + "**\n");
                }
            }
            w.write("\n");

            // Observações
            w.write("## Observações\n");
            w.write("- O objetivo é apoiar análise exploratória: ranking por município, distribuição por segmento e evolução temporal.\n");
            w.write("- Próximos passos possíveis: ampliar período, adicionar CNAE/porte e comparar tendências por região.\n");
        }
    }

    public void generateCharts(Path figuresDir) throws Exception {
        Files.createDirectories(figuresDir);

        chartSegmentos(figuresDir.resolve("segmentos.png"));
        chartTopMunicipios(figuresDir.resolve("top_municipios.png"));
        chartEvolucaoMensal(figuresDir.resolve("evolucao_mensal.png"));
    }

    private void chartSegmentos(Path out) throws Exception {
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        try (ResultSet rs = db.query("""
                SELECT segmento, SUM(qtd_aberturas) AS total
                FROM aberturas_empresas_sc
                GROUP BY segmento
                ORDER BY total DESC
        """)) {
            while (rs.next()) {
                labels.add(rs.getString("segmento"));
                values.add(rs.getInt("total"));
            }
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(900).height(500)
                .title("Aberturas por Segmento (SC)")
                .xAxisTitle("Segmento")
                .yAxisTitle("Aberturas")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        chart.addSeries("Aberturas", labels, values);
        BitmapEncoder.saveBitmap(chart, out.toString(), BitmapEncoder.BitmapFormat.PNG);
    }

    private void chartTopMunicipios(Path out) throws Exception {
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        try (ResultSet rs = db.query("""
                SELECT municipio, SUM(qtd_aberturas) AS total
                FROM aberturas_empresas_sc
                GROUP BY municipio
                ORDER BY total DESC
                LIMIT 10
        """)) {
            while (rs.next()) {
                labels.add(rs.getString("municipio"));
                values.add(rs.getInt("total"));
            }
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(1000).height(550)
                .title("Top 10 Municípios por Aberturas (SC)")
                .xAxisTitle("Município")
                .yAxisTitle("Aberturas")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisLabelRotation(45);

        chart.addSeries("Aberturas", labels, values);
        BitmapEncoder.saveBitmap(chart, out.toString(), BitmapEncoder.BitmapFormat.PNG);
    }

    private void chartEvolucaoMensal(Path out) throws Exception {
        List<String> meses = new ArrayList<>();
        List<Integer> total = new ArrayList<>();

        try (ResultSet rs = db.query("""
                SELECT strftime('%Y-%m', data_abertura) AS mes, SUM(qtd_aberturas) AS total
                FROM aberturas_empresas_sc
                GROUP BY mes
                ORDER BY mes
        """)) {
            while (rs.next()) {
                meses.add(rs.getString("mes"));
                total.add(rs.getInt("total"));
            }
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(900).height(500)
                .title("Evolução Mensal de Aberturas (SC)")
                .xAxisTitle("Mês")
                .yAxisTitle("Aberturas")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisLabelRotation(45);

        chart.addSeries("Aberturas", meses, total);
        BitmapEncoder.saveBitmap(chart, out.toString(), BitmapEncoder.BitmapFormat.PNG);
    }

    private int singleInt(String sql) throws Exception {
        try (ResultSet rs = db.query(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}