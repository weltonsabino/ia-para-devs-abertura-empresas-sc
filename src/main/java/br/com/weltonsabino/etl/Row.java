package br.com.weltonsabino.etl;

/**
 * Representa uma linha do dataset de empresas abertas em SC.
 *
 * Este record é utilizado em todo o pipeline de ETL:
 * ExcelLoader -> Cleaner -> DuckDb -> Analyzer.
 */
public record Row(
        int anoAbertura,
        String mesAbertura,
        String municipio,
        String naturezaJuridica,
        String regiao,
        String opcaoMei,
        String uf,
        String porte,
        String tipoSituacao,
        int quantidadeEmpresas
) {
}