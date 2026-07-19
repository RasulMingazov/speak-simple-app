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
- `feature-root`: root Decompose component and app-level routing.
- `feature-chat`: first product feature.
- `core-common`: shared utilities, coroutine dispatchers, `BaseModel`.
- `core-design`: app theme, typography, colors.

## Working Rules

- Keep changes small and local to the requested feature.
- Do not refactor unrelated files.
- Do not revert user changes unless explicitly asked.
- Prefer existing project patterns over introducing new abstractions.
- Use `rg` / `rg --files` for search.
- Use Compose resources for user-visible strings. Do not hardcode UI strings in composables.
- Run relevant Gradle checks before finishing.

## Architecture

### Components

Decompose components are the presentation boundary.

Pattern:

- `FeatureComponent.kt`: public component contract.
- `DefaultFeatureComponent.kt`: implementation and retained model creation.
- `FeatureContent.kt`: composable UI for that component.
- `FeatureStateMapper.kt`: maps `DataState` to `UiState` when useful.

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

### State and Events

Prefer this shape:

- `DataState`: internal model state.
- `UiState`: public state exposed by the component.
- `Event`: input from UI into component/model.
- `News` or `Effect`: one-shot UI actions such as scrolling.

Use `StateFlow` for observable UI state.

Use `Channel(...).receiveAsFlow()` or equivalent one-shot flow for effects/news.

### Decompose Retention

- `InstanceKeeper`: retains live objects across configuration changes.
- `StateKeeper`: use only for small serializable state that should survive process death.
- Do not store large data or message history in `StateKeeper`.
- Message history should come from repository/database when real persistence exists.

## Feature Chat

Current chat behavior:

- Messages are observed through `ObserveChatMessagesUseCase`.
- Message loading/pagination goes through `LoadChatMessagesUseCase`.
- Sending goes through `SendChatMessageUseCase`.
- The fake repository simulates backend delay.

Feedback is represented as a chat message role when it needs special visual treatment. Keep it as simple text unless the product explicitly needs structured feedback again.

Do not make `ChatInputComponent` depend on `ChatMessagesComponent` internals. Cross-child coordination should happen through shared domain/repository state or through a small explicit parent contract only when necessary.

## DI

The project currently uses manual DI containers.

Examples:

- `feature-chat/di/DefaultChatContainer.kt`
- `feature-root/di/DefaultRootContainer.kt`

Do not introduce a DI framework unless the user explicitly asks for it. Manual DI is preferred while the app is small because dependencies are visible and easy to change.

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

Preview files currently live in Android source set when Android Studio preview is needed.

## Tests

Use focused tests for models and mappers.

Prefer testing:

- model event -> state/effect behavior;
- mapper `DataState -> UiState`;
- pagination state;
- send-message state transitions.

Component tests are usually less valuable when the component only proxies to a model and wires child components.

## Verification

Before finishing code changes, run the relevant checks. For broad feature-chat changes, use:

```bash
./gradlew :feature-chat:testAndroidHostTest :feature-root:compileAndroidMain :app:assembleDebug :feature-chat:compileKotlinIosSimulatorArm64
```

If only a narrow non-UI change was made, a smaller Gradle target is acceptable, but mention what was run.

## Git

- The worktree may contain user changes. Inspect `git status --short` before edits.
- Do not use destructive commands like `git reset --hard` or `git checkout --` unless explicitly asked.
- If committing, stage the exact intended state with `git add -A` only after checking status.

