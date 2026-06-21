# Youtlix — struktura testów

Katalog `test/` opisuje konwencje. Kod testów znajduje się w standardowym drzewie Maven:

```
src/test/java/com/project/youtlix/
├── testsupport/          # współdzielone: tagi, adnotacje meta, buildery, fixture'y
├── unit/                 # testy jednostkowe (bez Springa, bez I/O)
├── integration/          # testy integracyjne (Spring context, adaptery)
├── e2e/                  # testy end-to-end (HTTP, pełna aplikacja)
└── architecture/         # ArchUnit + reguły strukturalne
```

## Piramida testów

| Warstwa | Pakiet | Adnotacja | Runner |
|---------|--------|-----------|--------|
| Jednostkowe | `unit.<moduł>` | `@UnitTest` | `.\test-suite unit` |
| Integracyjne | `integration.<moduł>` | `@IntegrationTest` | `.\test-suite integration` |
| Architektura | `architecture` | `@ArchitectureTest` | `.\test-suite architecture` |
| E2E | `e2e.scenario` | `@E2ETest` | `.\test-suite e2e` |

Domyślnie `mvn test` uruchamia wszystko **oprócz** testów oznaczonych tagiem `e2e`.

## Mapowanie na wzorzec DeskFlow (rozdział 4)

| DeskFlow | Youtlix | Pakiet / runner |
|----------|---------|-----------------|
| 4.4 Model domenowy | `RatingUnitTest`, `WatchlistUnitTest`, `SeriesUnitTest`, `ContentDomainUnitTest`, `PlaybackDomainUnitTest`… | `.\test-suite unit` |
| 4.5 Usługi dziedzinowe | `RatingServiceUnitTest`, `RecommendationEngineUnitTest`, `PlaybackServiceUnitTest`, `ContentSearchServiceUnitTest`, `ContentFactoryUnitTest` | `.\test-suite unit` |
| 4.6 Usługi aplikacyjne | `*UseCaseUnitTest`, `*CrudUnitTest`, `*MissingDataUnitTest` | `.\test-suite unit` |
| 4.7 Adaptery wyjściowe (JDBC/Supabase) | `SupabaseContentRepositoryIntegrationTest`, `SupabaseRatingRepositoryIntegrationTest`, `SupabaseWatchlistRepositoryIntegrationTest` | `.\test-suite integration` |
| 4.8 Adaptery REST | `AuthenticationWebMvcIntegrationTest`, testy kontrolerów w `unit/*/infrastructure` | `integration` + `unit` |
| 4.9 Testy systemowe (pełna ścieżka) | `AdminContentLibraryCycleE2ETest` | `.\test-suite e2e` |
| 4.10 Zdarzenia domenowe | `InMemoryDomainEventBusUnitTest`, `ContentRemovedEventHandlerUnitTest`, eventy w testach domeny | `unit` + `integration` |
| 4.11 Architektura | `CleanArchitectureTest`, `DomainIsolationTest`, `ArchitectureRulesTest` | `.\test-suite architecture` |

### Konfiguracja `env.properties` (gitignored)

Wymagane dla `integration` i `e2e`:

```properties
DB_DATABASE=...
SUPABASE_URL=...
SUPABASE_ANON_KEY=...
TEST_ADMIN_EMAIL=admin@example.com
TEST_ADMIN_PASSWORD=...
```

`TEST_ADMIN_*` — konto z rolą `LIBRARY_ADMIN` do testów HTTP i E2E. Bez tych pól testy integracyjne auth/E2E zostaną pominięte (`TestAborted`).

## Wspólny runner — log + raport HTML

Jedna komenda dla każdej warstwy piramidy:

```powershell
.\test-suite unit
.\test-suite integration
.\test-suite architecture
.\test-suite e2e
```

Skróty (aliasy):

```powershell
.\test-unit              # to samo co .\test-suite unit
.\open-test-report unit  # ostatni raport HTML dla danej warstwy
.\open-unit-report       # alias dla unit
```

Opcje:

```powershell
.\test-suite unit -Open           # po teście otwiera raport w przeglądarce
.\test-suite integration -Open
```

Po każdym uruchomieniu zapisuje w `test/logs/`:

| Plik | Opis |
|------|------|
| `{suite}-YYYY-MM-DD_HH-mm-ss.log` | pełny output Maven |
| `{suite}-YYYY-MM-DD_HH-mm-ss.html` | raport HTML (moduły, statusy, błędy) |
| `latest-{suite}.log` / `.html` | ostatnie uruchomienie danej warstwy |

Dla `unit` dodatkowo utrzymywane są `latest.log` / `latest.html` (kompatybilność wsteczna).

Maven bez skryptu:

```powershell
.\mvnw.cmd test -Punit
.\mvnw.cmd test -Pintegration
.\mvnw.cmd test -Parchitecture
.\mvnw.cmd verify -Pe2e-tests
```

## Moduły (bounded contexts)

W `unit/` i `integration/` trzymaj podfoldery zgodne z produkcją:

- `authentication`
- `contentlibrary`
- `recommendation`
- `videoplayback`
- `common`

Przykład: test use case'u biblioteki treści →  
`unit/contentlibrary/application/ContentLibraryUseCaseTest.java`

## Profile Spring

| Profil | Plik | Zastosowanie |
|--------|------|--------------|
| `integration` | `src/test/resources/application-integration.properties` | adaptery, repozytoria, kontekst Spring |
| `e2e` | `src/test/resources/application-e2e.properties` | scenariusze HTTP |

Oba profile opcjonalnie importują `env.properties` (lokalna baza Supabase).

## Klasy bazowe

- `integration.support.IntegrationTestSupport` — kontekst Spring, profil `integration`
- `e2e.support.E2ETestSupport` — `RestTestClient`, port losowy, context path `/youtlix/app`

## ArchUnit

- `architecture.ArchitectureRulesTest` — punkt wejścia ArchUnit
- `architecture.rules.ProductionCodeRules` — współdzielone reguły

Istniejący `CleanArchitectureTest` (sprawdzanie plików) zostanie przeniesiony / zastąpiony w kolejnym kroku.

## Migracja istniejących testów (plan)

| Obecna lokalizacja | Docelowa |
|--------------------|----------|
| `*/domain/**`, `*/application/**` (bez Springa) | `unit/<moduł>/...` + `@UnitTest` |
| `system/ContentLibrarySystemTest` | `integration/<moduł>/...` lub `unit/` (in-memory) |
| `architecture/CleanArchitectureTest` | `architecture/rules/` (ArchUnit) |
| — | `e2e/scenario/*E2ETest.java` |

## Konwencje nazewnictwa

- Jednostkowe: `*Test`
- Integracyjne: `*IT` lub `*IntegrationTest`
- E2E: `*E2ETest` lub `*E2EIT` (wymagane przez Failsafe)

## Następne kroki (do ustalenia)

1. Migracja istniejących testów do nowych pakietów
2. Testcontainers / lokalna baza testowa dla `integration` i `e2e`
3. Rozbudowa reguł ArchUnit (warstwy, bounded contexts)
4. CI: osobne joby per profil Maven
