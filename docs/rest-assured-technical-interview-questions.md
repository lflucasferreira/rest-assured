# Rest Assured — Perguntas Técnicas para Entrevistas

> Banco de perguntas para entrevistas com recrutadores técnicos, QA leads, SDETs e engenheiros de software.  
> Cobertura baseada no conteúdo dos slides (`docs/slides/index.html`), na suíte **rest-assured** (Spring Petclinic) e em tópicos frequentes em empresas brasileiras e internacionais.  
> **Legenda:** `[SLIDE]` = abordado na apresentação · `[PROJETO]` = presente na suíte Petclinic · `[EXTRA]` = comum em entrevistas, fora dos slides/projeto.

---

## Índice

1. [Conceitos Fundamentais](#1-conceitos-fundamentais)
2. [Rest Assured — DSL e API](#2-rest-assured--dsl-e-api)
3. [JUnit 5 e Organização](#3-junit-5-e-organização)
4. [Camada de Clients e Arquitetura](#4-camada-de-clients-e-arquitetura)
5. [Validação de Contrato e Schema](#5-validação-de-contrato-e-schema)
6. [Dados de Teste e Factories](#6-dados-de-teste-e-factories)
7. [Cenários Negativos e Parametrização](#7-cenários-negativos-e-parametrização)
8. [WireMock e Serviços Externos](#8-wiremock-e-serviços-externos)
9. [Captura de Rede e Observabilidade](#9-captura-de-rede-e-observabilidade)
10. [Allure e Relatórios](#10-allure-e-relatórios)
11. [CI/CD e Docker](#11-cicd-e-docker)
12. [Comparações e Migração](#12-comparações-e-migração)
13. [Segurança e Boas Práticas](#13-segurança-e-boas-práticas)
14. [Cenários Comportamentais](#14-cenários-comportamentais)

---

## 1. Conceitos Fundamentais

| # | Pergunta | Tag |
|---|----------|-----|
| 1.1 | O que é Rest Assured e para que tipo de teste ele foi projetado? | `[SLIDE]` |
| 1.2 | Qual a diferença entre teste de API, teste de integração e teste E2E? | `[EXTRA]` |
| 1.3 | Por que testar APIs diretamente (sem UI) pode ser mais estável? | `[PROJETO]` |
| 1.4 | O que é contrato de API e como ele se relaciona com JSON Schema? | `[PROJETO]` |
| 1.5 | O que é RFC 7807 Problem Details e quando validá-lo? | `[PROJETO]` |

---

## 2. Rest Assured — DSL e API

| # | Pergunta | Tag |
|---|----------|-----|
| 2.1 | Explique a sintaxe `given().when().then()` do Rest Assured. | `[SLIDE]` |
| 2.2 | Qual a diferença entre `Response` e encadear asserções em `then()`? | `[SLIDE]` |
| 2.3 | Como extrair valores com JsonPath sem mapear para POJO? | `[PROJETO]` |
| 2.4 | Como validar tempo de resposta (SLA) com Rest Assured? | `[PROJETO]` |
| 2.5 | O que é `RequestSpecification` e por que reutilizá-la? | `[PROJETO]` |
| 2.6 | Como enviar headers customizados (ex.: `If-None-Match` para ETag)? | `[PROJETO]` |

---

## 3. JUnit 5 e Organização

| # | Pergunta | Tag |
|---|----------|-----|
| 3.1 | Diferença entre `@BeforeEach` e `@BeforeAll` em testes de API. | `[SLIDE]` |
| 3.2 | Como usar `@ParameterizedTest` com `@CsvSource`? | `[PROJETO]` |
| 3.3 | Para que serve `@DisplayName` e as anotações Allure `@Epic`/`@Feature`/`@Story`? | `[PROJETO]` |
| 3.4 | Como garantir independência entre testes que criam dados? | `[PROJETO]` |
| 3.5 | Quando usar `Assumptions.assumeTrue()` (ex.: ETag ausente)? | `[PROJETO]` |

---

## 4. Camada de Clients e Arquitetura

| # | Pergunta | Tag |
|---|----------|-----|
| 4.1 | Por que encapsular chamadas HTTP em classes Client? | `[SLIDE]` `[PROJETO]` |
| 4.2 | O que `BaseTest` deve centralizar? | `[PROJETO]` |
| 4.3 | Como organizar pacotes `clients`, `models`, `tests`, `utils`? | `[PROJETO]` |
| 4.4 | Quando usar POJO vs `Map` vs JsonPath? | `[PROJETO]` |
| 4.5 | Como o projeto trata múltiplos ambientes (`dev`, `docker`)? | `[PROJETO]` |

---

## 5. Validação de Contrato e Schema

| # | Pergunta | Tag |
|---|----------|-----|
| 5.1 | Como validar JSON Schema com Rest Assured neste projeto? | `[PROJETO]` |
| 5.2 | Diferença entre validar schema do primeiro item de array vs objeto único. | `[PROJETO]` |
| 5.3 | Como validar headers HTTP além do corpo JSON? | `[PROJETO]` |
| 5.4 | O que testar em respostas 204 No Content? | `[PROJETO]` |
| 5.5 | Como tratar corpo vazio em erros 404? | `[PROJETO]` |

---

## 6. Dados de Teste e Factories

| # | Pergunta | Tag |
|---|----------|-----|
| 6.1 | Por que usar JavaFaker em vez de dados fixos? | `[SLIDE]` |
| 6.2 | Como implementar cleanup (`@AfterEach`) de recursos criados? | `[PROJETO]` |
| 6.3 | Como modelar payloads inválidos para testes negativos? | `[PROJETO]` |

---

## 7. Cenários Negativos e Parametrização

| # | Pergunta | Tag |
|---|----------|-----|
| 7.1 | Como parametrizar IDs inválidos (404) sem duplicar testes? | `[PROJETO]` |
| 7.2 | Como validar erro 400 com ProblemDetail? | `[PROJETO]` |
| 7.3 | Qual a diferença entre `ErrorResponseValidator` e `ResponseValidator`? | `[PROJETO]` |

---

## 8. WireMock e Serviços Externos

| # | Pergunta | Tag |
|---|----------|-----|
| 8.1 | Para que serve WireMock em testes de API? | `[SLIDE]` |
| 8.2 | Diferença entre stub, mock e spy no contexto WireMock. | `[EXTRA]` |
| 8.3 | Como verificar payload JSON exato com `equalToJson`? | `[PROJETO]` |
| 8.4 | Como simular falha downstream (503) sem afetar a API principal? | `[PROJETO]` |

---

## 9. Captura de Rede e Observabilidade

| # | Pergunta | Tag |
|---|----------|-----|
| 9.1 | Como um `Filter` do Rest Assured captura request/response? | `[PROJETO]` |
| 9.2 | Como anexar troca HTTP ao relatório Allure? | `[PROJETO]` |
| 9.3 | Como validar sequência de chamadas em fluxos multi-etapa? | `[PROJETO]` |

---

## 10. Allure e Relatórios

| # | Pergunta | Tag |
|---|----------|-----|
| 10.1 | Como gerar e servir relatório Allure localmente? | `[SLIDE]` |
| 10.2 | Como o CI publica Allure no GitHub Pages? | `[SLIDE]` |
| 10.3 | Qual valor de `@Epic`/`@Feature`/`@Story` no dia a dia? | `[PROJETO]` |

---

## 11. CI/CD e Docker

| # | Pergunta | Tag |
|---|----------|-----|
| 11.1 | Como o GitHub Actions sobe a API Petclinic como service container? | `[SLIDE]` |
| 11.2 | Por que fazer health check antes de `mvn test`? | `[PROJETO]` |
| 11.3 | Diferença entre `docker compose up` e Maven local. | `[PROJETO]` |
| 11.4 | Como passar `api.base.uri` via `-D` no Maven? | `[PROJETO]` |

---

## 12. Comparações e Migração

| # | Pergunta | Tag |
|---|----------|-----|
| 12.1 | Rest Assured vs Postman/Newman — prós e contras. | `[EXTRA]` |
| 12.2 | Rest Assured vs Playwright `request` fixture. | `[EXTRA]` |
| 12.3 | Rest Assured vs Karate DSL. | `[EXTRA]` |
| 12.4 | Quando adicionar testes de API vs E2E na pirâmide? | `[EXTRA]` |

---

## 13. Segurança e Boas Práticas

| # | Pergunta | Tag |
|---|----------|-----|
| 13.1 | Como evitar commitar credenciais em `config/*.properties`? | `[EXTRA]` |
| 13.2 | Por que não usar dados de produção em testes automatizados? | `[EXTRA]` |
| 13.3 | Como sanitizar logs e anexos Allure? | `[EXTRA]` |

---

## 14. Cenários Comportamentais

| # | Pergunta | Tag |
|---|----------|-----|
| 14.1 | Descreva como você estruturaria testes de API para um novo microserviço. | `[EXTRA]` |
| 14.2 | Um teste flaky retorna 500 intermitente — como investigar? | `[EXTRA]` |
| 14.3 | A API mudou o contrato de erro — como atualizar a suíte com mínimo impacto? | `[PROJETO]` |
| 14.4 | Como você priorizaria cobertura entre happy path, negativo e fluxos? | `[EXTRA]` |

---

## Respostas sugeridas (amostra)

<details>
<summary>2.1 — Sintaxe given/when/then</summary>

- **given:** prepara request (headers, body, auth, spec base)
- **when:** executa o verbo HTTP (GET, POST, …)
- **then:** asserções na response (status, body, time, headers)

No projeto, clients encapsulam given/when; testes usam validators e Hamcrest no "then".
</details>

<details>
<summary>4.1 — Por que Client layer?</summary>

Centraliza paths, headers e serialização; facilita manutenção quando a API muda; evita duplicação de Rest Assured em cada teste; permite anexar filtros (network capture) uma vez no `ApiClient`.
</details>

<details>
<summary>8.1 — WireMock</summary>

Simula serviços externos (webhooks, filas, parceiros) sem dependência real. Permite stub de respostas, verify de payload outbound e injeção de falhas para testar resiliência do código cliente.
</details>
