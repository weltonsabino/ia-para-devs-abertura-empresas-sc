package br.com.weltonsabino.analysis;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

import br.com.weltonsabino.db.DuckDb;

public class Analyzer {

    private final DuckDb db;

    public Analyzer(DuckDb db) {
        this.db = db;
    }

    public void generateSummary(Path outputPath) throws IOException, SQLException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        Connection connection = db.connection();
        try (Statement stmt = connection.createStatement()) {
            int totalLinhas = getInt(stmt, "SELECT COUNT(*) FROM empresas_abertas_sc");
            int totalEmpresas = getInt(stmt, "SELECT SUM(quantidade_empresas) FROM empresas_abertas_sc");
            int totalMunicipios = getInt(stmt, "SELECT COUNT(DISTINCT municipio) FROM empresas_abertas_sc");

            List<String> topMunicipios = getTopMunicipios(stmt);
            List<String> topNaturezas = getTopNaturezas(stmt);

            StringBuilder sb = new StringBuilder();
            sb.append("# Resumo da análise de abertura de empresas em SC (2025)\n\n");
            sb.append("## Visão geral\n\n");
            sb.append("- Total de linhas analisadas: ").append(totalLinhas).append("\n");
            sb.append("- Total de empresas abertas: ").append(totalEmpresas).append("\n");
            sb.append("- Total de municípios com registros: ").append(totalMunicipios).append("\n\n");

            sb.append("## Top 5 municípios com mais aberturas\n\n");
            for (String linha : topMunicipios) {
                sb.append("- ").append(linha).append("\n");
            }

            sb.append("\n## Top 5 naturezas jurídicas\n\n");
            for (String linha : topNaturezas) {
                sb.append("- ").append(linha).append("\n");
            }

            sb.append("\n## Observações\n\n");
            sb.append("- Os dados foram exportados do painel Mapa de Empresas, com filtro para Santa Catarina e meses de 2025.\n");
            sb.append("- A linha de totais foi removida no processo de ingestão.\n");
            sb.append("- O dataset tratado mantém apenas registros válidos com UF = SC e quantidade de empresas maior que zero.\n");

            Files.writeString(outputPath, sb.toString());
        }
    }

    public void generateCharts(Path outputDir) throws IOException, SQLException {
        Files.createDirectories(outputDir);

        generateTopMunicipiosChart(outputDir.resolve("top10_municipios"));
        generateAberturasPorMesChart(outputDir.resolve("aberturas_por_mes"));
        generateMeiChart(outputDir.resolve("mei_vs_nao_mei"));
        generatePorteChart(outputDir.resolve("porte_empresas"));
        generateNaturezaChart(outputDir.resolve("natureza_juridica"));
    }

    private void generateTopMunicipiosChart(Path outputFile) throws SQLException, IOException {
        String sql = """
                SELECT municipio, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY municipio
                ORDER BY total DESC
                LIMIT 10
                """;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Connection connection = db.connection();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String municipio = rs.getString("municipio");
                if (municipio != null) {
                    municipio = municipio.replace(" - SC", "").trim();
                }
                int total = rs.getInt("total");
                dataset.addValue(total, "Empresas abertas", municipio);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 10 municípios de SC com mais empresas abertas em 2025",
                "Município",
                "Quantidade de empresas",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);

        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        renderer.setBarPainter(new StandardBarPainter());
        chart.setBackgroundPaint(Color.white);
        plot.setBackgroundPaint(Color.white);
        
        Path file = Path.of(outputFile.toString() + ".png");
        ChartUtils.saveChartAsPNG(file.toFile(), chart, 1600, 900);
    }

    private void generateAberturasPorMesChart(Path outputFile) throws SQLException, IOException {
        String sql = """
                SELECT mes_abertura, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY mes_abertura
                ORDER BY
                    CASE mes_abertura
                        WHEN 'Janeiro' THEN 1
                        WHEN 'Fevereiro' THEN 2
                        WHEN 'Março' THEN 3
                        WHEN 'Abril' THEN 4
                        WHEN 'Maio' THEN 5
                        WHEN 'Junho' THEN 6
                        WHEN 'Julho' THEN 7
                        WHEN 'Agosto' THEN 8
                        WHEN 'Setembro' THEN 9
                        WHEN 'Outubro' THEN 10
                        WHEN 'Novembro' THEN 11
                        WHEN 'Dezembro' THEN 12
                    END
                """;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Connection connection = db.connection();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String mes = rs.getString("mes_abertura");
                int total = rs.getInt("total");
                dataset.addValue(total, "Empresas abertas", mes);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Total de empresas abertas por mês em SC em 2025 (jan–nov)",
                "Mês",
                "Quantidade de empresas",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setBarPainter(new StandardBarPainter());

        chart.setBackgroundPaint(Color.white);
        plot.setBackgroundPaint(Color.white);

        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        Path file = Path.of(outputFile.toString() + ".png");
        ChartUtils.saveChartAsPNG(file.toFile(), chart, 1600, 900);
    }

    private void generateMeiChart(Path outputFile) throws SQLException, IOException {
        String sql = """
                SELECT opcao_mei, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY opcao_mei
                ORDER BY total DESC
                """;

        generateCategoryChart(
                sql,
                "Distribuição por opção MEI",
                "Opção MEI",
                "Quantidade de empresas",
                outputFile
        );
    }

    private void generatePorteChart(Path outputFile) throws SQLException, IOException {
        String sql = """
                SELECT porte, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY porte
                ORDER BY total DESC
                """;

        generateCategoryChart(
                sql,
                "Distribuição por porte",
                "Porte",
                "Quantidade de empresas",
                outputFile
        );
    }

    private void generateNaturezaChart(Path outputFile) throws SQLException, IOException {
        String sql = """
                SELECT natureza_juridica, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY natureza_juridica
                ORDER BY total DESC
                LIMIT 10
                """;

        generateCategoryChart(
                sql,
                "Top naturezas jurídicas",
                "Natureza jurídica",
                "Quantidade de empresas",
                outputFile
        );
    }

    private void generateCategoryChart(String sql, String title, String xAxisTitle, String yAxisTitle, Path outputFile)
            throws SQLException, IOException {

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        Connection connection = db.connection();
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                labels.add(rs.getString(1));
                values.add(rs.getInt(2));
            }
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(700)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        chart.addSeries(title, labels, values);

        BitmapEncoder.saveBitmap(chart, outputFile.toString(), BitmapEncoder.BitmapFormat.PNG);
    }

    private int getInt(Statement stmt, String sql) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    private List<String> getTopMunicipios(Statement stmt) throws SQLException {
        String sql = """
                SELECT municipio, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY municipio
                ORDER BY total DESC
                LIMIT 5
                """;

        List<String> resultado = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(rs.getString("municipio") + ": " + rs.getInt("total"));
            }
        }

        return resultado;
    }

    private List<String> getTopNaturezas(Statement stmt) throws SQLException {
        String sql = """
                SELECT natureza_juridica, SUM(quantidade_empresas) AS total
                FROM empresas_abertas_sc
                GROUP BY natureza_juridica
                ORDER BY total DESC
                LIMIT 5
                """;

        List<String> resultado = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(rs.getString("natureza_juridica") + ": " + rs.getInt("total"));
            }
        }

        return resultado;
    }
}