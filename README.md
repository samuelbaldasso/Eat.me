# Eat.me

Clone conceitual e produção-grade do ecossistema de delivery (estilo iFood) para Android nativo em Kotlin e Jetpack Compose, concebido com arquitetura de alta escala, modularização e princípios rigorosos de Clean Architecture e UDF/MVI.

---

## 🚀 Visão Geral e Stack Técnica

| Camada / Função | Tecnologia |
|---|---|
| **Linguagem & Concorrência** | Kotlin 2.x (K2), Coroutines, Flow, `kotlinx.serialization` |
| **Plataforma & SDK** | Android nativo · `minSdk 26` · `targetSdk 35` · `compileSdk 35` |
| **UI & Design System** | Jetpack Compose, Material 3, Type-safe Compose Navigation |
| **Arquitetura** | Clean Architecture + MVI (UDF), Multi-módulo por camada e feature |
| **Injeção de Dependências** | Dagger Hilt |
| **Rede & Comunicação** | Retrofit + OkHttp com interceptors, SSE / WebSockets para tracking |
| **Persistência Local** | Room (offline-first, cache, sacola, pedidos), Jetpack DataStore |
| **Testes de Qualidade** | JUnit 5, MockK, Turbine, Truth, Compose UI Tests |

---

## 🏛️ Estrutura Arquitetural

```
presentation  →  domain  ←  data
(UI, ViewModel,   (entities, use cases,   (repositories impl,
 UiState/Intent/   repository interfaces,  remote/local sources,
 Effect)           regras de negócio)      DTOs, mappers)
```

- **`:core:domain-shared`**: Núcleo em Kotlin JVM puro (sem acoplamento com Android SDK ou Room/Retrofit), contendo entidades centrais, value class monetária `Money` (cálculos seguros em centavos inteiros via `Long`, arredondamento half-up e formatação BRL pt-BR) e tratamento de erros padronizado (`AppResult`, `AppError`).
- **`:app`**: Módulo de composição principal integrando injeção de dependências e navegação raiz.

---

## 🧪 Estratégia de Testes

- **100% de cobertura no domínio financeiro e use cases críticos.**
- Cálculos monetários com testes parametrizados cobrindo percentuais, descontos, cupons e arredondamento half-up.
- Testes de ViewModel com Turbine e MockK simulando ciclos de vida e fluxos de estado MVI.

---

## 🛠️ Como Executar

```bash
# Executar todos os testes unitários
./gradlew test

# Gerar APK de debug
./gradlew assembleDebug
```
