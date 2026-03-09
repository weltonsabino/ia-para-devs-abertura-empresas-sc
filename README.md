# Análise de Abertura de Empresas em Santa Catarina (2025)

Projeto desenvolvido como parte do **desafio prático da trilha IA para DEVs – Programa SCTEC**.

O objetivo deste projeto é construir um pipeline de tratamento e análise de dados utilizando informações públicas sobre abertura de empresas em Santa Catarina no ano de 2025.

A solução realiza ingestão de dados em formato Excel, limpeza e padronização das informações, armazenamento em banco analítico e geração de análises e visualizações.

## Objetivo do projeto

O projeto busca analisar o cenário de empreendedorismo em Santa Catarina a partir da abertura de empresas em 2025.

A solução implementa um pipeline de dados que realiza:

- ingestão de dados públicos
- tratamento e limpeza das informações
- organização dos dados em estrutura analítica
- geração de relatórios e visualizações

Entre as análises realizadas estão:

- municípios com maior número de aberturas de empresas
- distribuição das aberturas ao longo dos meses
- participação de empresas optantes por MEI
- distribuição por porte empresarial
- principais naturezas jurídicas

## Fonte dos dados

Os dados utilizados neste projeto foram obtidos a partir do painel público do Governo Federal: **Mapa de Empresas**.

https://www.gov.br/empresas-e-negocios/pt-br/mapa-de-empresas/painel-mapa-de-empresas

Filtros utilizados na extração dos dados:

- UF: Santa Catarina (SC)
- Período: todos os meses de 2025

Os dados foram exportados em formato Excel e utilizados como base para o pipeline de análise.

## Tecnologias utilizadas

- Java 17
- Maven
- Apache POI (leitura de arquivos Excel)
- DuckDB (banco analítico embarcado)
- JFreeChart (gráficos com rótulos de valores)
- XChart (geração de gráficos)
- Git / GitHub (versionamento)

## Estrutura do projeto

```text
data
├── raw
│   └── empresas_abertas_sc_2025.xlsx
├── processed
│   ├── empresas_abertas_sc_2025_clean.csv
│   └── empresas_abertas_sc_2025.duckdb

reports
├── figures
│   └── gráficos gerados pela análise
└── summary.md

src/main/java/br/com/weltonsabino
├── etl
│   ├── ExcelLoader.java
│   ├── Cleaner.java
│   ├── Exporter.java
│   └── Row.java
├── db
│   └── DuckDb.java
└── analysis
    └── Analyzer.java

App.java
```

## Pipeline de dados

O pipeline implementado neste projeto segue as seguintes etapas:

### 1. Ingestão de dados

Leitura da planilha Excel exportada do painel Mapa de Empresas utilizando Apache POI.

### 2. Limpeza e tratamento

Durante o processo de limpeza são aplicadas as seguintes regras:

- remoção da linha de totais da planilha
- remoção de registros inválidos
- validação da UF (SC)
- padronização de campos de texto
- validação de quantidade de empresas

### 3. Exportação de dataset tratado

Após o tratamento, o dataset é exportado para:

```text
data/processed/empresas_abertas_sc_2025_clean.csv
```

Esse arquivo representa o dataset preparado para análise.

### 4. Persistência analítica

Os dados tratados são carregados em um banco analítico local utilizando **DuckDB**, permitindo consultas SQL rápidas.

### 5. Análise e visualização

Após o carregamento dos dados no DuckDB, o projeto executa consultas analíticas para gerar indicadores sobre a abertura de empresas em Santa Catarina no ano de 2025.

Como resultado, o pipeline produz dois tipos de artefatos analíticos:

- **Relatório em Markdown**, contendo um resumo das principais métricas e resultados da análise
- **Gráficos analíticos em formato PNG**, que facilitam a visualização e interpretação dos dados

Os gráficos são gerados utilizando bibliotecas Java de visualização de dados:

- **JFreeChart** – utilizado para gráficos de barras com rótulos de valores
- **XChart** – utilizado para alguns gráficos analíticos auxiliares

## Resultados gerados

Após a execução do pipeline são produzidos os seguintes artefatos:

```text
data/
└── processed/
    ├── empresas_abertas_sc_2025_clean.csv
    └── empresas_abertas_sc_2025.duckdb
reports/
├── summary.md
└── figures/
    └── *.png
```

Entre as análises produzidas estão:

- Top municípios com mais aberturas de empresas
- Aberturas de empresas por mês
- Distribuição por opção MEI
- Distribuição por porte empresarial
- Principais naturezas jurídicas

## Como executar o projeto

1. Clone o repositório.

```bash
git clone https://github.com/weltonsabino/ia-para-devs-abertura-empresas-sc
```

2. Abra o projeto em uma IDE Java, como Eclipse.
3. Execute a classe principal `App.java`.
4. Após a execução, os resultados estarão nas pastas `data/processed` e `reports`.

## Vídeo de apresentação

O vídeo pitch do projeto será disponibilizado aqui após a gravação.

## Autor

Welton Sabino

GitHub: https://github.com/weltonsabino/ia-para-devs-abertura-empresas-sc
