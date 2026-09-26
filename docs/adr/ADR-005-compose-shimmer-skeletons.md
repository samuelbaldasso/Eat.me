# ADR-005: Feedback de Carregamento por Esqueletos Shimmer no Design System

## Status
**Aceito** (Implementado no módulo `:core:designsystem`)

---

## Contexto
O carregamento de dados em feeds e listas pode produzir uma sensação de lentidão quando apresentado apenas com indicadores circulares genéricos (`CircularProgressIndicator` centralizado). Além disso, quando o conteúdo final é carregado de forma repentina, ocorre um salto de layout (*layout shift* ou *jank* perceptível), prejudicando a experiência visual do usuário.

---

## Decisão
Criamos uma biblioteca de esqueletos de carregamento baseada em animação de gradiente contínuo (*Shimmer Effect*) dentro de `:core:designsystem`:

1. **Extensão `Modifier.shimmer()`:**
   - Utiliza `rememberInfiniteTransition` com `Brush.linearGradient` e interpolação `FastOutSlowInEasing`.
   - Modificador leve, sem dependência de bibliotecas de terceiros externas desnecessárias, operando com recomposição mínima.
2. **Esqueletos Estruturados Fiéis:**
   - `HomeScreenSkeleton`: Reflete com precisão as dimensões da barra de busca em pílula, carrossel de categorias, banner promocional e cards de restaurantes.
   - `RestaurantDetailSkeleton`: Reflete o cabeçalho, abas e itens do cardápio.
3. **Substituição dos Indicadores:**
   - Durante o estado `isLoading` do `HomeUiState` e `RestaurantDetailUiState`, os esqueletos são renderizados mantendo a geometria espacial da tela até os dados do Room estarem prontos.

---

## Consequências

### Positivas
- **Sensação de Agilidade (Perceived Performance):** O usuário percebe a estrutura da interface imediatamente.
- **Prevenção de Layout Shift:** Como o esqueleto replica a altura e largura dos cards reais, a transição para os dados finais ocorre sem solavancos.
- **Reutilização Modular:** Qualquer nova tela pode compor com `ShimmerPlaceholder` diretamente a partir do Design System.
