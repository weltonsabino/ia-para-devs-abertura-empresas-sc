package br.com.weltonsabino.db;

import br.com.weltonsabino.etl.Row;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

public class DuckDb implements AutoCloseable {

    private final Connection conn;

    private DuckDb(Connection conn) {
        this.conn = conn;
    }

    public static DuckDb open(Path dbFile) throws Exception {
        Files.createDirectories(dbFile.getParent());
        String url = "jdbc:duckdb:" + dbFile.toAbsolutePath();
        Connection c = DriverManager.getConnection(url);
        return new DuckDb(c);
    }

    public void createSchema() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS aberturas_empresas_sc (
                    municipio TEXT,
                    segmento TEXT,
                    data_abertura DATE,
                    qtd_aberturas INTEGER
                );
            """);

            st.execute("DELETE FROM aberturas_empresas_sc;"); // recria carga (simples)
        }
    }

    public void loadRows(List<Row> rows) throws Exception {
        String sql = "INSERT INTO aberturas_empresas_sc (municipio, segmento, data_abertura, qtd_aberturas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Row r : rows) {
                ps.setString(1, r.municipio());
                ps.setString(2, r.segmento());
                // converter YearMonth para o 1º dia do mês
                ps.setDate(3, Date.valueOf(r.dataAbertura().atDay(1)));
                ps.setInt(4, r.qtdAberturas());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public ResultSet query(String sql) throws Exception {
        Statement st = conn.createStatement();
        return st.executeQuery(sql);
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
