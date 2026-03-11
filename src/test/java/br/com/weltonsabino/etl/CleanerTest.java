package br.com.weltonsabino.etl;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanerTest {

    @Test
    void shouldKeepOnlyValidRows() {
        List<Row> rawRows = List.of(
                new Row(2025, " Janeiro ", " Florianópolis ", " LTDA ", " Sul ", " Sim ", " sc ", " Microempresa ", " Ativa ", 10),
                new Row(2025, "Janeiro", "   ", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 5),
                new Row(2025, "Janeiro", "Blumenau", "LTDA", "Sul", "Sim", "PR", "Microempresa", "Ativa", 5),
                new Row(2025, "Janeiro", "Joinville", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 0)
        );

        List<Row> result = Cleaner.clean(rawRows);

        assertEquals(1, result.size());

        Row row = result.get(0);
        assertEquals(2025, row.anoAbertura());
        assertEquals("Janeiro", row.mesAbertura());
        assertEquals("Florianópolis", row.municipio());
        assertEquals("LTDA", row.naturezaJuridica());
        assertEquals("Sul", row.regiao());
        assertEquals("Sim", row.opcaoMei());
        assertEquals("SC", row.uf());
        assertEquals("Microempresa", row.porte());
        assertEquals("Ativa", row.tipoSituacao());
        assertEquals(10, row.quantidadeEmpresas());
    }

    @Test
    void shouldIgnoreNullRows() {
        List<Row> rawRows = Arrays.asList(
                null,
                new Row(2025, "Janeiro", "Florianópolis", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 3)
        );

        List<Row> result = Cleaner.clean(rawRows);

        assertEquals(1, result.size());
        assertEquals("Florianópolis", result.get(0).municipio());
    }

    @Test
    void shouldRemoveRowsWithNonPositiveQuantity() {
        List<Row> result = Cleaner.clean(List.of(
                new Row(2025, "Janeiro", "Florianópolis", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", -1),
                new Row(2025, "Janeiro", "Joinville", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 0),
                new Row(2025, "Janeiro", "Blumenau", "LTDA", "Sul", "Sim", "SC", "Microempresa", "Ativa", 1)
        ));

        assertEquals(1, result.size());
        assertEquals("Blumenau", result.get(0).municipio());
    }
}