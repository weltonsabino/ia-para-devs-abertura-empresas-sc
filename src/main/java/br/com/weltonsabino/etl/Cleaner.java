package br.com.weltonsabino.etl;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class Cleaner {

    private static final Set<String> SEGMENTOS_VALIDOS = Set.of(
            "Tecnologia", "Comércio", "Industria", "Indústria", "Serviços", "Servicos", "Agronegócio", "Agronegocio"
    );

    public static List<Row> clean(List<Row> raw) {
        if (raw == null) return List.of();

        return raw.stream()
                .map(Cleaner::normalize)
                .filter(Objects::nonNull)
                .filter(r -> r.qtdAberturas() >= 0)
                .collect(Collectors.toList());
    }

    private static Row normalize(Row r) {
        if (r == null) return null;

        String municipio = safe(r.municipio());
        String segmento = safe(r.segmento());

        if (municipio.isBlank() || segmento.isBlank() || r.dataAbertura() == null) {
            return null; // remove registros inválidos
        }

        municipio = titleCase(removerAcentos(municipio.trim()));
        segmento = normalizarSegmento(segmento);

        // se segmento não bater com lista, mantém mas padroniza (você pode preferir filtrar)
        return new Row(municipio, segmento, r.dataAbertura(), r.qtdAberturas());
    }

    private static String normalizarSegmento(String s) {
        String x = titleCase(removerAcentos(s.trim()));

        // mapeamentos comuns
        if (x.equalsIgnoreCase("Industria")) return "Indústria";
        if (x.equalsIgnoreCase("Servicos")) return "Serviços";
        if (x.equalsIgnoreCase("Agronegocio")) return "Agronegócio";

        // se vier algo fora, mantém em formato apresentável
        if (!SEGMENTOS_VALIDOS.contains(x)) {
            return x;
        }
        // se veio sem acento, aplica acento nos casos
        if (x.equals("Industria")) return "Indústria";
        if (x.equals("Servicos")) return "Serviços";
        if (x.equals("Agronegocio")) return "Agronegócio";
        return x;
    }

    private static String removerAcentos(String input) {
        String norm = Normalizer.normalize(input, Normalizer.Form.NFD);
        return norm.replaceAll("\\p{M}", "");
    }

    private static String titleCase(String input) {
        if (input.isBlank()) return input;
        String[] parts = input.toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isBlank()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}