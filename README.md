# Análise de Abertura de Empresas em Santa Catarina (jan-nov 2025)

Projeto desenvolvido como parte do **desafio prático da trilha IA para DEVs – Programa SCTEC**.

O objetivo deste projeto é construir um pipeline de tratamento e análise de dados utilizando informações públicas sobre abertura de empresas em Santa Catarina no ano de 2025 (jan-nov).

A solução realiza ingestão de dados em formato Excel, limpeza e padronização das informações, armazenamento em banco analítico e geração de análises e visualizações.

---

# Objetivo do projeto

O projeto busca analisar o cenário de empreendedorismo em Santa Catarina a partir da abertura de empresas em 2025 (jan-nov).

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

---

# Fonte dos dados

Os dados utilizados neste projeto foram obtidos a partir do painel público do Governo Federal: **Mapa de Empresas**.

https://www.gov.br/empresas-e-negocios/pt-br/mapa-de-empresas/painel-mapa-de-empresas

Filtros utilizados na extração dos dados:

- UF: Santa Catarina (SC)
- Período: Janeiro a Novembro de 2025

Obs: A base pública utilizada não contempla dados de dezembro de 2025.

Os dados foram exportados em formato Excel e utilizados como base para o pipeline de análise.

---

# Tecnologias utilizadas

- Java 17
- Maven
- Apache POI (leitura de arquivos Excel)
- DuckDB (banco analítico embarcado)
- JFreeChart (visualização de dados)
- JUnit 5 (testes automatizados)
- Git / GitHub (versionamento)

---

# Estrutura do projeto

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

src/test/java/br/com/weltonsabino
 ├── db
 │   └── DuckDbTest.java
 └── etl
     ├── CleanerTest.java
     ├── ExporterTest.java
     └── ExcelLoaderTest.java

App.java
```

---

# Pipeline de dados

O pipeline implementado neste projeto segue as seguintes etapas:

**1. Ingestão de dados**

Leitura da planilha Excel exportada do painel Mapa de Empresas utilizando Apache POI.

**2. Limpeza e tratamento**

Durante o processo de limpeza são aplicadas as seguintes regras:
- remoção da linha de totais da planilha
- remoção de registros inválidos
- validação da UF (SC)
- padronização de campos de texto
- validação de quantidade de empresas

**3. Exportação de dataset tratado**

Após o tratamento, o dataset é exportado para:

```text
data/processed/empresas_abertas_sc_2025_clean.csv
```

Esse arquivo representa o dataset preparado para análise.

**4. Persistência analítica**

Os dados tratados são carregados em um banco analítico local utilizando **DuckDB**, permitindo consultas SQL rápidas.

**5. Análise e visualização**

Após o carregamento dos dados no DuckDB, o projeto executa consultas analíticas para gerar indicadores sobre a abertura de empresas em Santa Catarina no ano de 2025 (jan-nov).

Como resultado, o pipeline produz:

- Relatório em Markdown
- Gráficos analíticos em PNG

---

# Resultados gerados

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

- Top 10 municípios com mais aberturas de empresas
- Aberturas de empresas por mês
- Distribuição por opção MEI
- Distribuição por porte empresarial
- Principais naturezas jurídicas

---

# Arquitetura do pipeline

```text
Excel (dados brutos)
   ↓
ExcelLoader
   ↓
Cleaner
   ↓
Exporter (CSV tratado)
   ↓
DuckDb (banco analítico)
   ↓
Analyzer
   ↓
Relatórios + Gráficos
```

---

# Como executar o projeto

**1. Clone o repositório:**

```bash
git clone https://github.com/weltonsabino/ia-dados-abertura-empresas-sc
```

**2. Entre na pasta do projeto:**

```bash
cd ia-dados-abertura-empresas-sc
```

**3. Execute o pipeline rodando a classe:**

```bash
App.java
```
Após a execução, os resultados estarão nas pastas:

```text
reports
```

---

# Executar via Maven

Para compilar o projeto:

```bash
mvn clean package
```

Para executar os testes automatizados:

```bash
mvn test
```

---

# Testes automatizados

O projeto possui testes unitários para as principais etapas do pipeline ETL.

Componentes cobertos pelos testes:

- ExcelLoader
- Cleaner
- Exporter
- DuckDb

Os testes utilizam:

- JUnit 5
- geração de arquivos temporários
- criação de planilhas Excel em memória

Todos os testes estão localizados em:

```text
src/test/java
```

Para executar os testes:

```bash
mvn test
```

Saída esperada:

```bash
BUILD SUCCESS
```

---

# Vídeo de apresentação

O vídeo pitch do projeto será disponibilizado aqui após a gravação.

---

# Autor

Welton Sabino

GitHub: https://github.com/weltonsabino/ia-para-devs-abertura-empresas-sc
