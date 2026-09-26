# ADR-003: Adoção do Padrão UDF (Unidirectional Data Flow) e MVI na Camada de Apresentação

## Status
**Aceito** (Implementado no módulo `:app`)

---

## Contexto
Telas modernas com alto dinamismo (como a sacola de compras com cupom interativo, a bottom sheet de customização de pratos e o rastreamento em tempo real) acumulam diversos estados parciais e assíncronos.

No padrão MVVM tradicional com múltiplos `MutableStateFlow` ou `LiveData` expostos separadamente por ViewModel:
- Estados podem dessincronizar (ex.: `isLoading` ser verdadeiro enquanto `errorMessage` não é limpo, ou valores parciais de preço não refletirem a quantidade atual).
- Rastrear a origem exata de uma mutação se torna complexo em bases de código em crescimento.

---

## Decisão
Padronizamos a camada de apresentação em torno de **Unidirectional Data Flow (UDF)** implementado via **MVI (Model-View-Intent)**:

### 1. Estado Único e Imutável (`UiState`)
Cada tela possui uma única data class imutável que descreve 100% do estado visual da tela a cada frame:
```kotlin
data class RestaurantDetailUiState(
    val restaurant: Restaurant? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedSectionIndex: Int = 0,
    val customizationState: DishCustomizationState? = null,
    val cart: Cart = Cart.EMPTY
)
```

### 2. Intenções Explícitas do Usuário (`UiIntent`)
Qualquer interação do usuário (clique em prato, alteração de quantidade, seleção de opção ou envio de cupom) é encapsulada em uma `sealed interface Intent` processada por um ponto único de entrada:
```kotlin
fun handleIntent(intent: RestaurantDetailIntent)
```

### 3. Efeitos Colaterais Pontuais (`UiEffect`)
Eventos que não devem ser re-emitidos em caso de recriação de configuração (ex.: exibição de Snackbars, mensagens de erro temporárias ou navegação) trafegam em um canal dedicado (`SharedFlow<UiEffect>`).

---

## Consequências

### Positivas
- **Previsibilidade Absoluta:** O estado da UI é sempre uma função pura do estado anterior e da intenção processada.
- **Testabilidade Facilitada:** Permite testar o comportamento da ViewModel de ponta a ponta com a biblioteca **Turbine**, validando as emissões do StateFlow de forma síncrona e declarativa.
- **Redução Drástica de Race Conditions:** Mutadores concorrentes atualizam o estado via `_uiState.update { ... }`.
