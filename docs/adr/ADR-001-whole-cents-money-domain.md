# ADR-001: Representação de Valores Monetários em Centavos Inteiros (`Long`) via Kotlin Value Class

## Status
**Aceito** (Implementado no módulo `:core:domain-shared`)

---

## Contexto
Aplicações de entrega e marketplace transacionam valores financeiros sensíveis (subtotais, taxas dinâmicas de entrega, descontos de cupons percentuais e absolutos, gorjetas e saldos de carteira). 

Historicamente, o uso inadequado de tipos numéricos em software financeiro resulta em dois cenários críticos:
1. **Tipos de ponto flutuante (`Double`, `Float`):** Sujeitos à especificação IEEE 754, acumulam erros de representação binária (ex: `0.1 + 0.2 = 0.30000000000000004`). Em somatórios de sacola e recálculo de cupons, isso causa discrepâncias de centavos na cobrança do usuário.
2. **`BigDecimal`:** Embora preciso, impõe uma sobrecarga de alocação de objetos no Garbage Collector (GC) e custo computacional em recomposições frequentes de UI no Jetpack Compose.

---

## Decisão
Implementamos a abstração financeira central como uma **inline value class** em Kotlin:

```kotlin
@JvmInline
value class Money(val cents: Long) : Comparable<Money>
```

### Regras Estabelecidas:
1. **Unidade Atômica:** Todos os cálculos, persistência em banco Room (SQLite `INTEGER`) e tráfego de domínio utilizam centavos inteiros (`Long`). Um item de R$ 22,00 é representado estritamente como `Money(2200L)`.
2. **Imutabilidade e Zero Overhead:** O compilador Kotlin elimina a alocação de objetos em tempo de execução, tratando `Money` como um primitivo JVM `long` sempre que não houver necessidade de boxing.
3. **Arredondamento Determinístico:** Operações de percentual e multiplicação aplicam explicitamente a regra bancária padrão `RoundingMode.HALF_UP` (`(cents * percent + 50) / 100`).
4. **Formatação Padronizada:** A formatação em moeda BRL (`R$ 22,00`) é centralizada e isolada no domínio através do método `formatBrl()` (com fallback sem quebra para locale pt-BR).

---

## Consequências

### Positivas
- **Impossibilidade de bugs de ponto flutuante:** 100% dos cálculos são exatos ao centavo.
- **Eficiência de Memória:** Sem sobrecarga na heap do Android, essencial para listas extensas de pedidos e produtos.
- **Tipagem Forte:** O compilador impede a soma acidental de quantidades escalares com valores monetários.

### Considerações
- Todas as camadas (DAOs, Mappers, ViewModels) devem converter entradas puras para `Money` no ponto de fronteira.
