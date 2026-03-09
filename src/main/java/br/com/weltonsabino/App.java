package br.com.weltonsabino;

import br.com.weltonsabino.analysis.Analyzer;
import br.com.weltonsabino.db.DuckDb;
import br.com.weltonsabino.etl.ExcelLoader;
import br.com.weltonsabino.etl.Cleaner;
import br.com.weltonsabino.etl.Exporter;
import br.com.weltonsabino.etl.Row;

import java.nio.file.Path;
import java.util.List;

public class App {

    public static void main(String[] args) {
        try {

            Path rawExcel = Path.of("data/raw/empresas_abertas_sc_2025.xlsx");
            Path cleanedCsv = Path.of("data/processed/empresas_abertas_sc_2025_clean.csv");
            Path duckDbFile = Path.of("data/processed/empresas_abertas_sc_2025.duckdb");

            // 1) Load Excel
            ExcelLoader loader = new ExcelLoader();
            List<Row> rawRows = loader.load(rawExcel.toString());

            // 2) Clean
            List<Row> cleanRows = Cleaner.clean(rawRows);

            // 3) Export cleaned CSV
            Exporter.writeCsv(cleanRows, cleanedCsv);

            // 4) Load into DuckDB + analyze
            try (DuckDb db = DuckDb.open(duckDbFile)) {
                db.createSchema();
                db.loadRows(cleanRows);

                Analyzer analyzer = new Analyzer(db);
                analyzer.generateSummary(Path.of("reports/summary.md"));
                analyzer.generateCharts(Path.of("reports/figures"));
            }

            System.out.println("✅ Pipeline finalizado com sucesso!");
            System.out.println("➡ CSV tratado: " + cleanedCsv);
            System.out.println("➡ DuckDB:      " + duckDbFile);
            System.out.println("➡ Relatório:   reports/summary.md");
            System.out.println("➡ Gráficos:    reports/figures/*.png");

        } catch (Exception e) {
            System.err.println("❌ Erro no pipeline: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}