# ADR-004: SplashScreen API Nativa (Android 12+) e Suporte a Edge-to-Edge

## Status
**Aceito** (Implementado no módulo `:app`)

---

## Contexto
A experiência de inicialização (*cold start*) de um aplicativo é o primeiro ponto de contato com o usuário.
Abordagens legadas com uma `SplashActivity` dedicada que executa um `Handler.postDelayed(..., 2000)`:
1. Retardam artificialmente o tempo de renderização do app.
2. Causam efeito visual indesejado de "tela branca / preta" antes do carregamento da Activity.
3. Não atendem às diretrizes modernas introduzidas pelo Google a partir do Android 12 (API 31+).

Paralelamente, o Android 15 (Target SDK 35) impõe a execução completa em **Edge-to-Edge**, exigindo que as barras de sistema (status bar e navigation bar) sejam transparentes e que o conteúdo gerencie explicitamente seus insets.

---

## Decisão
1. **Biblioteca Oficial `androidx.core:core-splashscreen`:**
   Adotamos a biblioteca oficial de retrocompatibilidade da SplashScreen API do Android 12+.
   - Tema `Theme.App.Starting` com `windowSplashScreenBackground` na cor primária Roxa (`#7C3AED`) e ícone vetorial da marca (`ic_splash_logo`).
   - Invocação imediata de `installSplashScreen()` em `MainActivity.onCreate()` antes de `super.onCreate()`.
   - Transição suave customizada de saída via `setOnExitAnimationListener` com interpolação `AnticipateInterpolator` e fade out.
2. **Edge-to-Edge com WindowInsets:**
   - Invocação de `enableEdgeToEdge()` com barras transparentes.
   - Componentes visuais utilizam `WindowInsets` de forma defensiva (`statusBarsPadding`, `navigationBarsPadding`, `imePadding`) para que o conteúdo não seja sobreposto pela barra de navegação ou recorte de câmera.

---

## Consequências

### Positivas
- **Início Instantâneo:** Zero tempo ocioso forçado no cold start.
- **Conformidade com o Android 15:** O app atende aos requisitos mais rígidos do Google Play para APIs modernas.
- **Transição Fluida:** Eliminação de piscadas visuais (*flicker*) entre a splash screen e o primeiro frame Compose.
