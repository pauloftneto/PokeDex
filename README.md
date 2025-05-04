# PokeDex

Aplicativo de listagem e visualização de detalhes de Pokémons, construído com Clean Architecture e Jetpack Compose.

---

## 🔍 Visão Geral

Este projeto é uma Pokedex que permite:

1. **Listagem de Pokémon**

   * Busca em tempo real por nome ou número.
   * Rolagem infinita (pagination).
   * Indicador de carregamento e tratamento de erros.

2. **Detalhes do Pokémon**

   * Exibe imagem, tipos, altura, peso e estatísticas.
   * Histórico local do último Pokémon acessado.

3. **Offline-First**

   * Dados armazenados localmente com Room.
   * Exibição imediata de cache em reconexões.

O código está organizado em módulos seguindo princípios da Clean Architecture:

```
app/                # Módulo Android principal (UI em Compose)
core-analytics/     # Módulo de Analytics de produto e engenharia
core-domain/        # Modelos de domínio e casos de uso (UseCases)
core-data/          # Implementação de repositório, fontes remota (API) e local (Room)
core-ui/            # Componentes de UI genéricos em Compose
feature-pokedex/    # Feature de listagem de Pokémons
feature-pokedex-details/ # Feature de detalhes de Pokémon
```

---

## 🚀 Tecnologias

* **Linguagem**: Kotlin
* **UI**: Jetpack Compose
* **Arquitetura**: Clean Architecture (domínio, dados, apresentação)
* **Rede**: Retrofit + Kotlinx Serialization
* **Persistência**: Room
* **Concorrência**: Coroutines + Flow
* **Testes**: JUnit, MockK, Turbine (Fluxos)
* **Timber**: Logging erros
* **FakeAnalytics**: Módulo de analytics simulado

---

### 📷 Screenshots

<table>
  <tr>
    <td align="center"><img src="https://github.com/user-attachments/assets/3881a6e0-7487-4470-a719-e60e849b562d" width="50%"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/703d19d5-645b-4f28-983d-ad0e25f3de12" width="50%"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/a75073f9-95b5-41f2-8364-79d7f695f796" width="50%"/></td>
  </tr>
</table>

### 📥 Instalação

1. Clone o repositório:

```
git clone [https://github.com/pauloftneto/PokeDex.git](https://github.com/pauloftneto/PokeDex.git)

```

2. Abra no Android Studio e sincronize gradle.
3. Execute o app em um emulador ou dispositivo físico.

## 🧪 Testes

* **core-domain**: Testes de casos de uso;
* **core-data**: Testes de repositório;
* **feature-**\*: Testes de ViewModels usando Turbine para validar estados de Loading/Success/Error.

```
./gradlew testDebugUnitTest

```

---

## 🔄 Funcionalidades Futuras

* Integração com logs e métricas reais (Firebase Crashlytics);
* Analytics de usuários (Firebase Analytics);
* Adicionar **paginação** com Paging 3;
* Testes de UI com Compose Testing;
* Melhoria de performance e cache de imagens;
* CI/CD (GitHub Actions) para lint, testes e build automatizado;
* Offline-first avançado e sincronização de cache.

---

Feito com ❤️ por Paulo Neto
