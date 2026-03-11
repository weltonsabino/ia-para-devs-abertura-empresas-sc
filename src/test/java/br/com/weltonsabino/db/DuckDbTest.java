package br.com.weltonsabino.db;

import br.com.weltonsabino.etl.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuckDbTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateSchemaAndLoadRows() throws Exception {
        Path dbFile = tempDir.resolve("teste.duckdb");

        try (DuckDb db = DuckDb.open(dbFile)) {
            db.createSchema();

            db.loadRows(List.of(
                    new Row(2025, "Janeiro", "Florianópolis", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 10),
                    new Row(2025, "Fevereiro", "Joinville", "MEI", "Norte", "Não", "SC", "Microempresa", "Ativa", 20)
            ));

            try (Statement stmt = db.connection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empresas_abertas_sc")) {

                rs.next();
                assertEquals(2, rs.getInt(1));
            }
        }
    }

    @Test
    void shouldReplaceExistingRowsWhenLoadingAgain() throws Exception {
        Path dbFile = tempDir.resolve("teste.duckdb");

        try (DuckDb db = DuckDb.open(dbFile)) {
            db.createSchema();

            db.loadRows(List.of(
                    new Row(2025, "Janeiro", "Florianópolis", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 10)
            ));

            db.loadRows(List.of(
                    new Row(2025, "Fevereiro", "Joinville", "MEI", "Norte", "Não", "SC", "Microempresa", "Ativa", 20)
            ));

            try (Statement stmt = db.connection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empresas_abertas_sc")) {

                rs.next();
                assertEquals(1, rs.getInt(1));
            }
        }
    }
}