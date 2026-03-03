package br.com.weltonsabino.etl;

import java.time.YearMonth;

public record Row(
        String municipio,
        String segmento,
        YearMonth dataAbertura,
        int qtdAberturas
) {}