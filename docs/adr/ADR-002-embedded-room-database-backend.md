# ADR-002: Backend de Marketplace e Persistência Integrado via Room SQLite

## Status
**Aceito** (Implementado no módulo `:core:database`)

---

## Contexto
O aplicativo Eat.io necessitava de um ambiente completo e fiel de marketplace para demonstrar operações complexas de e-commerce delivery (alimentação de catálogo, montagem de cardápio com opções obrigatórias e opcionais, gerenciamento reativo de carrinho e ciclo de vida de pedidos em tempo real).

As opções tradicionais de projeto envolviam:
1. **Mock HTTP Server / MockK em memória:** Volátil, perde o estado a cada reinício do app, inviabilizando testes manuais prolongados de histórico de pedidos e sacola.
2. **Servidor Backend Externo Independente (Node.js / Ktor / Spring):** Adiciona dependência de rede, latência de servidores gratuitos e risco de indisponibilidade durante avaliações técnicas de recrutadores.

---

## Decisão
Adotamos o **Android Room (SQLite)** como o **backend local integrado** e Single Source of Truth (SSOT) do aplicativo.

### Pilares da Arquitetura:
1. **Relacionamentos Relacionais Reais (1:N):**
   - `Restaurant` → `MenuSection` → `Dish` → `OptionGroup` → `Option`.
   - `CartItem` → `CartItemOption`.
   - `Order` → `OrderItem` → `OrderItemOption`.
   - Chaves estrangeiras com `CASCADE DELETE` garantem a integridade referencial ao esvaziar a sacola ou atualizar restaurantes.
2. **Reatividade Nascida no Banco (`Flow`):**
   - Todas as consultas de sacola (`CartDao.observeCart()`) e pedidos (`OrderDao.observeActiveOrders()`) emitem Kotlin `Flow`, notificando imediatamente ViewModels e a UI Compose sobre inserções, alterações de quantidade e exclusões.
3. **Database Seeder Idempotente:**
   - O `DatabaseSeeder` popula realisticamente os restaurantes, seções e pratos no primeiro carregamento do app via Dagger Hilt de forma atômica e assíncrona, sem bloquear a thread principal.
4. **Transacionalidade Atômica:**
   - Operações críticas como o *Checkout* (transferir itens da sacola para pedido definitivo e esvaziar a sacola) executam dentro de `@Transaction` protegida contra falhas intermediárias.

---

## Consequências

### Positivas
- **Offline-First Absoluto:** O aplicativo funciona com fluidez máxima sem conectividade externa.
- **Persistência Confiável:** Pedidos realizados continuam acessíveis no histórico e no rastreamento após fechar o aplicativo.
- **Preparado para Cache-First:** A estrutura de repositórios no padrão `RoomRestaurantRepository` permite acoplar facilmente um cliente de rede remoto (`Retrofit`/`Ktor`) sem modificar a camada de domínio.
