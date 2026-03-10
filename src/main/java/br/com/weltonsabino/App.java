package br.com.weltonsabino;

import br.com.weltonsabino.analysis.Analyzer;
import br.com.weltonsabino.db.DuckDb;
import br.com.weltonsabino.etl.Cleaner;
import br.com.weltonsabino.etl.ExcelLoader;
import br.com.weltonsabino.etl.Exporter;
import br.com.weltonsabino.etl.Row;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        try {
            Path rawExcel = Path.of("data/raw/empresas_abertas_sc_2025.xlsx");
            Path cleanedCsv = Path.of("data/processed/empresas_abertas_sc_2025_clean.csv");
            Path duckDbFile = Path.of("data/processed/empresas_abertas_sc_2025.duckdb");
            Path summaryFile = Path.of("reports/summary.md");
            Path figuresDir = Path.of("reports/figures");

            if (!Files.exists(rawExcel)) {
                throw new IllegalStateException("Arquivo de entrada não encontrado: " + rawExcel);
            }

            Files.createDirectories(cleanedCsv.getParent());
            Files.createDirectories(figuresDir);

            logger.info("Iniciando pipeline de análise de abertura de empresas em SC");
            logger.info("Arquivo de entrada: {}", rawExcel);

            ExcelLoader loader = new ExcelLoader();
            List<Row> rawRows = loader.load(rawExcel.toString());
            logger.info("Linhas carregadas da planilha: {}", rawRows.size());

            List<Row> cleanRows = Cleaner.clean(rawRows);
            logger.info("Linhas após limpeza: {}", cleanRows.size());

            Exporter.writeCsv(cleanRows, cleanedCsv);
            logger.info("CSV tratado gerado em: {}", cleanedCsv);

            try (DuckDb db = DuckDb.open(duckDbFile)) {
                db.createSchema();
                db.loadRows(cleanRows);
                logger.info("Base carregada no DuckDB: {}", duckDbFile);

                Analyzer analyzer = new Analyzer(db);
                analyzer.generateSummary(summaryFile);
                analyzer.generateCharts(figuresDir);
            }

            logger.info("Pipeline finalizado com sucesso");
            logger.info("Relatório gerado em: {}", summaryFile);
            logger.info("Gráficos gerados em: {}", figuresDir);

        } catch (Exception e) {
            logger.error("Erro no pipeline", e);
            System.exit(1);
        }
    }
}