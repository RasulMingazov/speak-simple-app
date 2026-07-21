# AGENTS.md

Guidance for coding agents working in this repository.

## Project

SpeakSimple is a Kotlin Multiplatform mobile app.

Current stack:

- Kotlin Multiplatform
- Compose Multiplatform
- Decompose
- MVVM/MVI-like presentation style
- Gradle convention plugins from `convention-plugins/project`

Modules:

- `app`: Android application wrapper.
- `shared`: shared app entry point.
- `feature-root`: bootstrap and authentication gate.
- `feature-main`: authenticated app shell and product-feature routing.
- `feature-auth`: authentication domain, data layer, platform integrations, and login UI.
- `feature-chat`: first product feature.
- `core-common`: shared utilities, coroutine dispatchers, `BaseModel`.
- `core-test`: reusable multiplatform test infrastructure; depend on it only from test source sets.
- `core-design`: app theme, typography, colors.

## Working Rules

- Keep changes small and local to the requested feature.
- Do not refactor unrelated files.
- Do not revert user changes unless explicitly asked.
- Prefer existing project patterns over introducing new abstractions.
- Use `rg` / `rg --files` for search.
- Use Compose resources for user-visible strings. Do not hardcode UI strings in composables.
- Default every declaration to `internal` or `private`. Keep it public only when another Gradle module needs it or when it is part of an unavoidable public signature.
- Before exposing a data implementation to a host module, add a narrow feature-owned factory or bridge entry point.
- Run relevant Gradle checks before finishing.

## Architecture

### Components

Decompose components are the presentation boundary.

Pattern:

- `FeatureComponent.kt`: public component contract.
- `DefaultFeatureComponent.kt`: implementation and retained model creation.
- `FeatureContent.kt`: composable UI for that component.
- `FeatureUiStateMapper.kt`: interface plus default implementation mapping `DataState` to `UiState`.

Only the component contract, its required presentation models, the public content composable, and a feature entry point should normally cross the Gradle-module boundary. Keep default components, models, mappers, and their factories `internal`.

Use Decompose navigation models such as `ChildStack` for mutually exclusive screens. Do not eagerly create every route child and switch composables with a manual enum: inactive children must not initialize their models, subscriptions, or data loading.

Keep navigation ownership hierarchical:

- `feature-root` decides only between bootstrap, unauthenticated, and authenticated destinations.
- `feature-main` owns navigation inside the authenticated product and composes product features such as `feature-chat`.
- A product feature owns its own nested navigation and children.

Adding another authenticated feature should normally change `feature-main`, not `feature-root`. The root module must not depend directly on individual product features.

For child components, keep the component contract, default implementation, model, and content close to each other in the package that owns the child.

Current chat split:

- `presentation/ChatComponent.kt`
- `presentation/DefaultChatComponent.kt`
- `presentation/ChatContent.kt`
- `presentation/input/*`
- `presentation/messages/*`

The root chat component should mainly compose child components. Avoid putting child internals into the root component.

### Models

Presentation state holders are named `Model`, not `ViewModel`.

Use `BaseModel` from `core-common` when a model needs:

- a retained lifetime through Decompose `InstanceKeeper`;
- a coroutine scope;
- `mapState` helper for `StateFlow`.

Use `modelScope`, not `viewModelScope`.

Keep `DataState` simple. It should hold facts, not too much derived UI logic. Put UI derivation into a mapper when it improves testability.

- Nest `DataState` inside its owning model.
- Mutate it only with `dataState.update { it.copy(...) }`; do not assign `dataState.value`.
- Read `dataState.value` only when an operation needs a synchronous snapshot.
- Set busy flags synchronously before launching a coroutine when they prevent duplicate actions.
- Suffix use-case dependencies with `UseCase`.
- Define UI-state mappers as interfaces and provide `Default...UiStateMapper` object implementations.
- Select display resources and derive presentation-only flags in the mapper, not in a composable.

### State and Events

Prefer this shape:

- `DataState`: internal model state.
- `UiState`: public state exposed by the component.
- `Event`: input from UI into component/model.
- `News` or `Effect`: one-shot UI actions such as scrolling.

Use `StateFlow` for observable UI state.

Use `Channel(...).receiveAsFlow()` or equivalent one-shot flow for effects/news.

Do not expose domain entities through component events or UI state when a presentation primitive or focused UI model expresses the same fact. For example, expose a string identifier or `isMessageLimitReached` instead of leaking a domain value object or sealed result.

### Decompose Retention

- `InstanceKeeper`: retains live objects across configuration changes.
- `StateKeeper`: use only for small serializable state that should survive process death.
- Do not store large data or message history in `StateKeeper`.
- Message history should come from repository/database when real persistence exists.

## Feature Chat

Current chat behavior:

- Messages are observed through `ObserveChatUseCase`.
- Initial loading goes through `GetChatUseCase`.
- Sending goes through `SendChatMessageUseCase`.
- The fake repository simulates backend delay.

Feedback is represented as a chat message role when it needs special visual treatment. Keep it as simple text unless the product explicitly needs structured feedback again.

Do not make `ChatInputComponent` depend on `ChatMessagesComponent` internals. Cross-child coordination should happen through shared domain/repository state or through a small explicit parent contract only when necessary.

## Domain and Data

Feature domain packages must contain only:

```text
domain/
├── entity/
├── repository/
└── usecase/
```

- Use singular package names. Do not add `domain.model`, `domain.models`, `domain.result`, or infrastructure packages.
- Put every independently named entity, value object, enum, command, result, or sealed root in its own file.
- Keep domain entities immutable and free from transport, persistence, UI, and platform dependencies.
- Put repository contracts in `domain.repository`; keep implementations in `data.repository`.
- Put each use-case interface and its internal `Default...UseCase` implementation in the same file.
- Keep use cases and repository contracts `internal` when they are consumed only inside their feature.
- Preserve coroutine cancellation at data boundaries and collapse failures that produce the same caller behavior.

For a real data layer, use this feature-local structure:

```text
data/
├── exception/
├── identity/
├── local/
│   └── entity/
├── mapper/
├── platform/
│   └── entity/
├── remote/
│   └── entity/
└── repository/
```

- Remote serialized types end in `Request` or `Response`; local serialized types end in `Db`.
- Remote and local data sources operate only on their own representations, never on domain entities.
- Keep all data/domain conversion in `data.mapper`, grouped by domain entity (`AuthSessionMapper.kt`, not one mapper per source representation).
- Keep data sources, mappers, adapters, provider tokens, platform models, and repository implementations `internal`.
- Expose platform setup through a narrow factory such as `createAndroidAuthContainer`, `createIosAuthContainer`, or `createChatComponentFactory`.

## DI

The project currently uses manual DI containers.

Examples:

- `feature-chat/di/DefaultChatContainer.kt`
- `feature-auth/di/DefaultAuthContainer.kt`
- `feature-root/di/DefaultRootContainer.kt`

Do not introduce a DI framework unless the user explicitly asks for it. Manual DI is preferred while the app is small because dependencies are visible and easy to change.

Application and shared host modules must not construct feature data sources, repositories, identity providers, or storage implementations directly. They should pass platform inputs into the feature's public factory and receive a narrow container or component factory.

Keep container lifetimes explicit:

- Application-scoped containers may own process-wide infrastructure such as authentication session storage and HTTP clients.
- An authenticated/user-scoped feature container must be created when its owning `MainComponent` is created and released when that authenticated branch is destroyed. Never retain user data repositories inside an application-scoped child factory across logout/login.
- A component factory may be application-scoped only when invoking it creates fresh user-scoped dependencies for the new component.

Expose composition through `create...ComponentFactory` functions and keep `Default...Container` implementations `internal`. Do not expose repositories or data/platform implementations merely to connect feature modules; expose focused domain use-case interfaces or a platform-specific container facade.

Platform factories own their adapters. For Android auth, the feature creates and hides its activity provider while the public Android auth container exposes only lifecycle attachment. On iOS, drive the root Decompose lifecycle from the owning `UIViewController` lifecycle and destroy it when Compose content is disposed.

## UI

Use Compose Multiplatform.

Guidelines:

- UI should not look strongly Android/Material-only; it should work visually for Android and iOS.
- Prefer simple, calm, product-specific UI over generic messenger clones.
- Keep dark and light theme both usable.
- Use `core-design` theme values instead of ad-hoc palettes where possible.
- Keep cards and bubbles stable in size and readable on small screens.
- Avoid nested cards.
- Keep strings in `composeResources`.
- Use the platform system splash for initial app bootstrap on Android. Keep the Compose bootstrap child as a static fallback and do not show an indeterminate progress indicator for initial session restoration.

Preview files currently live in Android source set when Android Studio preview is needed.

Formatting rules:

- Do not place blank lines between fields of a data class.
- Do not place blank lines between variants inside `Event`, `News`, enums, or sealed roots.
- Use trailing commas in multiline declarations and calls.
- Keep logical blank lines between class sections such as state, initialization, event handling, private operations, and nested types.

## Tests

Use focused tests for models and mappers.

Put reusable cross-feature fixtures and test infrastructure in `core-test` under the `org.speaksimpleapp.core.test` package. Add this module only to test source-set dependencies; do not place test helpers in `core-common/commonMain` or production feature source sets. Keep feature-specific fakes and builders in their owning feature tests so `core-test` never depends on feature modules.

Prefer testing:

- model event -> state/effect behavior;
- mapper `DataState -> UiState`;
- pagination state;
- send-message state transitions.
- duplicate-event protection when a busy flag guards an operation.
- each actionable branch of a sealed domain result.

Component tests are usually less valuable when the component only proxies to a model and wires child components.

## Verification

Before finishing code changes, run the relevant checks. For broad feature-chat changes, use:

```bash
./gradlew :feature-chat:testAndroidHostTest :feature-chat:compileKotlinIosSimulatorArm64 :feature-root:compileKotlinIosSimulatorArm64 :app:assembleDebug
```

For broad feature-auth changes, use:

```bash
./gradlew :feature-auth:testAndroidHostTest :feature-auth:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosSimulatorArm64 :app:assembleDebug
```

For root or authenticated navigation changes, use:

```bash
./gradlew :feature-root:testAndroidHostTest :feature-main:compileKotlinIosSimulatorArm64 :feature-root:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosSimulatorArm64 :app:assembleDebug
```

If only a narrow non-UI change was made, a smaller Gradle target is acceptable, but mention what was run.

## Git

- The worktree may contain user changes. Inspect `git status --short` before edits.
- Do not use destructive commands like `git reset --hard` or `git checkout --` unless explicitly asked.
- If committing, stage the exact intended state with `git add -A` only after checking status.
