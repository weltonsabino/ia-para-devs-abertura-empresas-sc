package br.com.weltonsabino.db;

import br.com.weltonsabino.etl.Row;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DuckDb implements AutoCloseable {

    private static final String TABLE_NAME = "empresas_abertas_sc";

    private final Connection connection;

    private DuckDb(Connection connection) {
        this.connection = connection;
    }

    public static DuckDb open(Path dbFile) throws SQLException {
        String url = "jdbc:duckdb:" + dbFile.toAbsolutePath();
        Connection connection = DriverManager.getConnection(url);
        return new DuckDb(connection);
    }

    public void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    ano_abertura INTEGER,
                    mes_abertura VARCHAR,
                    municipio VARCHAR,
                    natureza_juridica VARCHAR,
                    regiao VARCHAR,
                    opcao_mei VARCHAR,
                    uf VARCHAR,
                    porte VARCHAR,
                    tipo_situacao VARCHAR,
                    quantidade_empresas INTEGER
                )
            """.formatted(TABLE_NAME));
        }
    }

    public void loadRows(List<Row> rows) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            deleteExistingRows();
            insertRows(rows);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void deleteExistingRows() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + TABLE_NAME);
        }
    }

    private void insertRows(List<Row> rows) throws SQLException {
        String sql = """
            INSERT INTO %s (
                ano_abertura,
                mes_abertura,
                municipio,
                natureza_juridica,
                regiao,
                opcao_mei,
                uf,
                porte,
                tipo_situacao,
                quantidade_empresas
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.formatted(TABLE_NAME);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Row row : rows) {
                ps.setInt(1, row.anoAbertura());
                ps.setString(2, row.mesAbertura());
                ps.setString(3, row.municipio());
                ps.setString(4, row.naturezaJuridica());
                ps.setString(5, row.regiao());
                ps.setString(6, row.opcaoMei());
                ps.setString(7, row.uf());
                ps.setString(8, row.porte());
                ps.setString(9, row.tipoSituacao());
                ps.setInt(10, row.quantidadeEmpresas());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public Connection connection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }
}