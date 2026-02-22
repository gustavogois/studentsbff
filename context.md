# StudentsBFF - Contexto do Produto

Documento consolidado a partir da entrevista de levantamento de requisitos com o pai/stakeholder principal.

---

## 1. Perfil da Estudante

| Aspecto | Resposta |
|---------|----------|
| **Nivel escolar** | Ensino Fundamental II (6o ao 9o ano, 11-14 anos) |
| **Materias** | Todas do curriculo (Portugues, Matematica, Ciencias, Historia, Geografia, Ingles, etc.) |
| **Maior dificuldade** | Foco em humanas (Portugues, Historia, Geografia) |
| **Dispositivos** | Celular e computador |

---

## 2. Dores e Problemas Identificados

A estudante enfrenta tres dificuldades principais (compreensao NAO foi selecionada como problema):

- **Organizacao** — Dificuldade em saber o que estudar, quando e como se planejar
- **Motivacao** — Falta de engajamento, dificuldade em manter constancia
- **Revisao** — Nao consegue revisar de forma eficiente o que ja estudou

---

## 3. Rotina de Estudo Atual

- Ela **estuda sozinha**, mas os **pais tambem ajudam** no processo
- Nao ha uma rotina rigidamente estruturada
- A participacao dos pais e ativa, mas sem ferramentas para dar visibilidade

---

## 4. Ferramentas Usadas Atualmente

A estudante ja usa tres ferramentas:

| Ferramenta | Uso | Limitacao |
|-----------|-----|-----------|
| **ChatGPT** | Tirar duvidas, explicacoes | Generico, nao e focado no contexto escolar dela |
| **Edraw** | Criar mapas mentais | Ferramenta isolada, sem integracao |
| **Quizlet** | Flashcards para revisao | Limitado na inteligencia de revisao |

**Problemas das ferramentas atuais** (todas selecionadas):
- Funcionam bem separadas, mas falta **integracao** entre elas
- ChatGPT e **generico** demais (nao conhece o contexto escolar)
- Quizlet e **limitado** (falta inteligencia na repeticao espacada)
- Falta **continuidade** — ela usa cada ferramenta isoladamente, sem um fluxo de estudo conectado

---

## 5. Papel dos Pais no Produto

O pai selecionou **todos os anteriores**, desejando participacao completa:

- **Dashboard de pais** — Painel para visualizar progresso, notas e dedicacao
- **Notificacoes** — Alertas sobre o estudo (estudou ou nao, resultados)
- **Colaborativo** — Poder ajudar diretamente: adicionar tarefas, conteudos, metas

---

## 6. Plataforma e Acesso

O pai confirmou que o produto precisa funcionar em **todas** as opcoes:

- **App mobile** — Para usar no celular a qualquer hora
- **Desktop/Web** — Para sessoes de estudo no computador
- **Assistente IA** — Consulta a qualquer momento

**Solucao escolhida:** React (web) + PWA para mobile (sem app nativo separado)

---

## 7. Funcionalidade Mais Importante (MVP)

**Plano de estudos** — Organizar automaticamente o que estudar por dia/semana com base no calendario escolar

---

## 8. Fontes de Dados do Plano de Estudos

Quatro fontes identificadas:

1. **Cadastro manual** — Ela ou os pais cadastram provas, materias e conteudos manualmente
2. **Foto do caderno/agenda** — Tirar foto da agenda escolar e o app extrai informacoes via OCR + IA
3. **Integracao com escola** — Conectar com o sistema/portal da escola para puxar calendario e notas (futuro)
4. **Gmail escolar** — A estudante recebe muitas informacoes da escola pelo Gmail escolar (avisos, tarefas, datas de provas). A app deve acessar esses e-mails para extrair e organizar automaticamente as informacoes relevantes. Viavel via Gmail API, aproveitando o OAuth Google ja planejado para autenticacao (basta adicionar o escopo de leitura do Gmail)

---

## 9. Stack Tecnologica

Definida com base na familiaridade do pai/desenvolvedor:

| Camada | Tecnologia | Motivo |
|--------|-----------|--------|
| **Frontend** | React + TypeScript + PWA | Familiaridade com React |
| **Backend** | Java 21 + Spring Boot 3 | Familiaridade com Java/Spring |
| **Banco de dados** | PostgreSQL | Robusto, otimo com Spring Boot/JPA |
| **IA (inicial)** | OpenAI API (GPT-4o) | Pai tem creditos de OpenAI API para aproveitar |
| **IA (futuro)** | Abstracao LLMProvider | Preparar para Anthropic Claude e outros provedores |
| **Hosting** | Railway (tudo) | Frontend + Backend + PostgreSQL, tudo no Railway |

**Nota sobre IA:** O pai antes usava ChatGPT e tem creditos na OpenAI API. Atualmente usa Claude MAX com Opus, mas nao tem creditos para a Claude API. Portanto, comecar com OpenAI API mas arquitetar com interface `LLMProvider` para trocar de provedor no futuro.

---

## 10. Funcionalidades Extras Desejadas

Alem do MVP, o pai selecionou todas as opcoes extras:

- **Gamificacao** — Pontos, conquistas, streaks para motivar a constancia no estudo
- **Modo offline** — Poder estudar mesmo sem internet (importante para PWA)
- **Multi-usuario** — Outros filhos ou alunos poderem usar com perfis separados

---

## Resumo das Decisoes-Chave

1. **Problema central:** Ferramentas fragmentadas, sem integracao e sem contexto escolar personalizado
2. **MVP:** Plano de estudos inteligente com calendario, provas e tarefas diarias
3. **Diferencial:** Tudo integrado — plano + IA + revisao + mapas mentais + dashboard parental
4. **Estrategia de IA:** Comecar com OpenAI (creditos existentes), abstrair para trocar depois
5. **Deploy:** Railway para simplificar (frontend + backend + DB num so lugar)
6. **Mobile:** PWA em vez de app nativo — simplifica desenvolvimento
7. **Pais:** Participacao ativa total (dashboard + notificacoes + acoes diretas)
