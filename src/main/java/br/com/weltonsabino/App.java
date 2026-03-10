package br.com.weltonsabino;

import br.com.weltonsabino.analysis.Analyzer;
import br.com.weltonsabino.db.DuckDb;
import br.com.weltonsabino.etl.Cleaner;
import br.com.weltonsabino.etl.ExcelLoader;
import br.com.weltonsabino.etl.Exporter;
import br.com.weltonsabino.etl.Row;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    private static final Path RAW_EXCEL = Path.of("data/raw/empresas_abertas_sc_2025.xlsx");
    private static final Path CLEANED_CSV = Path.of("data/processed/empresas_abertas_sc_2025_clean.csv");
    private static final Path DUCKDB_FILE = Path.of("data/processed/empresas_abertas_sc_2025.duckdb");
    private static final Path SUMMARY_FILE = Path.of("reports/summary.md");
    private static final Path FIGURES_DIR = Path.of("reports/figures");

    public static void main(String[] args) {
        try {
            validateInputFile(RAW_EXCEL);
            createDirectories(CLEANED_CSV, FIGURES_DIR);

            logger.info("Iniciando pipeline de análise de abertura de empresas em SC");
            logger.info("Arquivo de entrada: {}", RAW_EXCEL);

            ExcelLoader loader = new ExcelLoader();
            List<Row> rawRows = loader.load(RAW_EXCEL.toString());
            logger.info("Linhas carregadas da planilha: {}", rawRows.size());

            List<Row> cleanRows = Cleaner.clean(rawRows);
            logger.info("Linhas após limpeza: {}", cleanRows.size());

            Exporter.writeCsv(cleanRows, CLEANED_CSV);
            logger.info("CSV tratado gerado em: {}", CLEANED_CSV);

            try (DuckDb db = DuckDb.open(DUCKDB_FILE)) {
                db.createSchema();
                db.loadRows(cleanRows);
                logger.info("Base carregada no DuckDB: {}", DUCKDB_FILE);

                Analyzer analyzer = new Analyzer(db);
                analyzer.generateSummary(SUMMARY_FILE);
                analyzer.generateCharts(FIGURES_DIR);
            }

            logger.info("Pipeline finalizado com sucesso");
            logger.info("Relatório gerado em: {}", SUMMARY_FILE);
            logger.info("Gráficos gerados em: {}", FIGURES_DIR);

        } catch (Exception e) {
            logger.error("Erro no pipeline", e);
            System.exit(1);
        }
    }

    private static void validateInputFile(Path rawExcel) {
        if (!Files.exists(rawExcel)) {
            throw new IllegalStateException("Arquivo de entrada não encontrado: " + rawExcel);
        }
    }

    private static void createDirectories(Path cleanedCsv, Path figuresDir) throws IOException {
        Files.createDirectories(cleanedCsv.getParent());
        Files.createDirectories(figuresDir);
    }
}