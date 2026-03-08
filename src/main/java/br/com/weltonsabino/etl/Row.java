package br.com.weltonsabino.etl;

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