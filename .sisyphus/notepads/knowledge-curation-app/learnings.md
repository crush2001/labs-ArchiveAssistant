
## Task 5 verification fixes - 2026-06-10
- Parameterized `friendlyTime(epochMillis, nowMillis = System.currentTimeMillis())` to make unit tests deterministic; `FriendlyTimeTest` now uses a fixed `nowMillis` constant.
- Updated `HomePaneTest.homePane_validationMessage_showsErrorText` to assert the visible validation text `请输入要归档的内容` via `onNodeWithText`.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.

## Task 6 - Topic Management Pane - 2026-06-10
- Added `AppPane.MANAGE` to separate manage pane routing from home (`AppPane.TOPICS`). `openTopicManagement()` now routes to `MANAGE` instead of `TOPICS`.
- Added minimal dialog state to `ArchiveAssistantState`: `topicNameDialogMode` (CREATE/RENAME), `topicNameDialogTopicId`, `deleteConfirmTopicId`.
- Added store methods: `openCreateTopicDialog`, `openRenameTopicDialog`, `closeTopicNameDialog`, `confirmCreateTopic`, `confirmRenameTopic`, `openDeleteConfirmDialog`, `closeDeleteConfirmDialog`, `confirmDeleteTopic`.
- `confirmCreateTopic` and `confirmRenameTopic` close the dialog on success by checking `topicValidationMessage == null` after calling the underlying CRUD action.
- `ManagePane` now shows: topic list with icon/title/meta, per-topic rename/delete actions, create action in header, topic-name dialog, delete confirmation dialog.
- Topic icons rendered as colored circle with first character fallback since no icon mapping exists yet.
- Added 7 unit tests for dialog state behavior and 8 Compose UI tests for tags/interactions.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.

## Task 7 - Detail Feed, Content Filters, and Card Modal - 2026-06-10
- `DetailPane.kt` now shows a filtered tab row with `detail-tabs` test tag, including only `全部`, `网页文章`, `图像截屏`, `文档/PDF` (excluding `文本片段` from the required top tabs).
- Empty state "该分类下暂无资料" renders when filtered list is empty, preventing stale cards.
- `KnowledgeItemRow` renders content-type-specific secondary text: `sourceUrl` for web articles, placeholder label for screenshots, metadata label for PDFs, and summary for plain text.
- Each card has a stable test tag `knowledge-card-{id}`.
- Added `CardModal` composable using Compose `Dialog`; tagged `card-modal` with close button `card-modal-close`, title, content type label, summary, and full text.
- Wired `CardModal` in `ArchiveAssistantApp.kt` at the top-level `Box` so it overlays both single-pane and two-pane layouts when `modalItem != null`.
- Existing state store `openCardModal`/`closeCardModal` correctly preserve filter state; `closeCardModal` returns to `DETAIL` (or `TOPICS` if no topic selected) without clearing `activeDetailFilter`.

## Task 9 - AI Settings DataStore Persistence - 2026-06-10
- Added AndroidX DataStore Preferences through the version catalog (`androidx-datastore-preferences`, version `1.1.7`) and app module dependency.
- AI engine settings are persisted via typed preference keys for `engineType`, `baseUrl`, `modelName`, `apiKeyAlias`, and `localEndpoint`; raw API key input remains UI-local in `SettingsPane`.
- `MainActivity` creates an `AiEngineSettingsRepository` from a private `preferencesDataStore`; `ArchiveAssistantApp` loads settings with a `LaunchedEffect` and saves through the existing settings-change callback.
- Knowledge topics/items remain seeded in-memory data for this pass; docs explicitly defer knowledge content persistence until real import/storage requirements are defined.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.
- Added 2 unit tests: `filterSelection_documentPdf_updatesVisibleItemsForSelectedTopic` and `closeCardModal_preservesSelectedFilterAndTopic`.
- Added `DetailPaneTest.kt` with 5 Compose UI tests covering tab display, filter switching, empty state, card click callback, and modal close behavior.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.

## Task 8 - AI Engine Settings Pane - 2026-06-10
- Replaced placeholder `SettingsPane` with full AI engine settings UI using Material3 `ExposedDropdownMenuBox` and `OutlinedTextField`.
- Group label `AI 推理引擎配置` rendered with `MaterialTheme.typography.titleMedium`.
- Engine type selector tagged `engine-type-selector` supports `API` (CLOUD_API) and `本地模型` (LOCAL_MODEL) options.
- Cloud mode shows `cloud-base-url-input`, `api-key-input` (masked via `PasswordVisualTransformation`), and `cloud-model-input`.
- Local mode shows `local-endpoint-input` and `local-model-input`, plus helper text about device hardware limitations.
- Raw API key stored only in local Compose state (`remember { mutableStateOf("") }`), never committed to `AiEngineSettings` or logged.
- Settings updates wired to `ArchiveAssistantState.aiSettings` via `ArchiveAssistantStateStore.updateAiSettings` in both `SinglePaneLayout` and `TwoPaneLayout`.
- Added 6 Compose UI tests in `SettingsPaneTest.kt` covering cloud/local field visibility, engine type switching, API key masking, and field change callbacks.
- Added focused unit test `updateAiSettings_switchesEngineTypeAndUpdatesFields` in `ArchiveAssistantStateStoreTest.kt`.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.
- No network clients added; no real AI calls; no secrets printed in logs or test output.

## Task 10 - QA Consolidation - 2026-06-10
- Actual stable Compose tags are dash-style, not the stale dot-style names formerly documented in `AI-Design/04-ui-module-design.md`.
- Required primary tags now documented: `parser-input`, `classify-button`, `recent-topic-list`, `topic-card-{id}`, `manage-button`, `create-topic-button`, `rename-topic-button-{id}`, `delete-topic-button-{id}`, `detail-tabs`, `card-modal`, `settings-trigger`, and `engine-type-selector`.
- `HomePane` previously still used old dot-style tags for home topic actions; Task 10 changed the manage action to `manage-button` and added a UI test assertion.
- Source search found no network clients in `app/src/main`; secret-related matches are limited to settings aliases/preferences and masking tests.
- Markdown LSP is not configured and Kotlin LSP is configured but `kotlin-lsp` is not installed, so Gradle compilation remains the verification fallback.

## F2 Fix - Home create topic button wiring - 2026-06-10
- Added `onCreateTopic: () -> Unit` parameter to `HomePane` and wired it to the `home-create-topic-button` `ActionButton`, replacing the dead `onClick = { }`.
- Added `ArchiveAssistantStateStore.openTopicManagementForCreate()` which navigates to `AppPane.MANAGE` and opens `TopicNameDialogMode.CREATE` via `openCreateTopicDialog()`.
- Wired all five `HomePane` call sites in `ArchiveAssistantApp.kt` (single-pane: TOPICS, DETAIL fallback, CLASSIFICATION_REVIEW, CARD_DETAIL fallback; two-pane layout) to `stateStore::openTopicManagementForCreate`.
- Updated `HomePaneTest.kt` to assert `home-create-topic-button` is displayed in the display test, and added `homePane_createTopicButton_click_triggersCallback` to verify the callback is invoked on click.
- Added `openTopicManagementForCreate_navigatesToManageAndOpensCreateDialog` unit test in `ArchiveAssistantStateStoreTest.kt`.
- `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin` all pass.
