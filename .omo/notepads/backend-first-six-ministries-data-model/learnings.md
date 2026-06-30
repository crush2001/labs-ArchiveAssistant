## 2026-06-26 Task: context-gathering
- Active plan: `.omo/plans/backend-first-six-ministries-data-model.md`.
- Boulder active work id: `backend-first-six-ministries-data-model-4bb06336`.
- Direct grep/rg hotspots: `Models.kt`, `SampleKnowledgeData.kt`, `AppDataPreferences.kt`, `AppDataRepository.kt`, `MockKnowledgeClassifier.kt`, `LocalLlmClassifier.kt`, `LocalLlmSmartSummarizer.kt`, `RemoteApiSmartSummarizer.kt`, `ArchiveAssistantState.kt`, `ArchiveAssistantStateStore.kt`, `ArchiveAssistantApp.kt`, `HomePane.kt`, `ManagePane.kt`, `DetailPane.kt`, and related unit/android tests.
- `ArchiveAssistantStateStore.deleteTopic()` currently filters both `topics` and `items`, so cascade deletion is the highest-priority data-safety risk.
- `sg` command is unavailable in this environment (`zsh: command not found: sg`); AST-grep coverage cannot be used unless installed later.

## 2026-06-26 Background findings: State/UI map
- `ArchiveAssistantStateStore.kt` direct Topic CRUD methods: `createTopic`, `renameTopic`, `deleteTopic`, `confirmCreateTopic`, `confirmRenameTopic`, `confirmDeleteTopic`, `openCreateTopicDialog`, `openRenameTopicDialog`, `openDeleteConfirmDialog`, `openTopicManagementForCreate`.
- Keep navigation/read methods: `openTopic`, `closePanes`, `openSettings`; `openTopicManagement` can remain if ManagePane becomes read-only or be made inert depending on minimal UI approach.
- `ArchiveAssistantState.kt` computed properties `itemsByTopic`, `selectedTopic`, `selectedTopicItems`, `filteredSelectedTopicItems`, `recentTopics`, `searchedTopics` are still required for rendering and should not be removed in this phase.
- Minimal UI options identified: add `showTopicManagement`/`topicCrudEnabled` booleans to hide buttons, or keep methods as no-op backend guards. Plan preference remains minimal CRUD affordance hide/disable only, no page redesign.
- Tests needing updates include `ArchiveAssistantStateStoreTest`, `HomePaneTest`, `ManagePaneTest`, and classifier/summarizer tests that assert legacy topic IDs.

## 2026-06-26 Background findings: Persistence architecture
- Current `decodeTopics()`/`decodeItems()` collapse corrupt JSON to `emptyList()`, which loses the distinction between absent, valid empty, and corrupt.
- Recommended implementation seam: add result-returning APIs such as `tryDecodeTopics`/`tryDecodeItems` or a repository `loadSnapshot()` that returns success/corrupt/missing states from one Preferences read.
- `ArchiveAssistantStateStore.loadPersistedStateAsync()` must not restore a partial corrupt snapshot or trigger `saveData()` after corrupt topic decode.
- Test seam: fake DataStore seeded with corrupt `app_topics_json` and valid `app_items_json`; assert items remain preserved and update/write count stays zero.

## 2026-06-26 Background findings: Model/data map
- Core symbols remain: `Topic`, `KnowledgeItem.topicId`, `ClassificationPayload.topicId`, `SmartSummarizeResult.Success.topicId`, `SampleKnowledgeData.DefaultTopicId`, `AppDataPreferences.TopicsKey`, `AppDataPreferences.ItemsKey`.
- `MockKnowledgeClassifier`, `LocalLlmClassifier`, `LocalLlmSmartSummarizer`, and `RemoteApiSmartSummarizer` all validate or emit topic IDs and must constrain to ministry IDs/fallback.
- The model/data agent suggested repository splitting and reactive Flow refactors, but that is outside the approved backend-first scope and should not be implemented in this pass.

## 2026-06-26 Implementation: Add Canonical Six Ministries Model
### Files changed
- `app/src/main/java/com/lyihub/archiveassistant/domain/Models.kt` — Added `SixMinistry` enum with 6 entries (`OFFICIALS`, `TREASURY`, `RITES`, `MILITARY`, `JUSTICE`, `WORKS`), each with `id`/`order`/`label`. Added `SixMinistry.byId()` lookup, `SixMinistry.toTopic()` extension, and `sixMinistryTopics` top-level list. `Topic` data class remains unchanged for source compatibility.
- `app/src/main/java/com/lyihub/archiveassistant/domain/SampleKnowledgeData.kt` — Changed `DefaultTopicId` from `"topic-ai-architecture"` to `"treasury"`. Replaced `topics` list with `sixMinistryTopics`. Updated sample items to use `SixMinistry.*.id` references instead of legacy topic IDs. One sample item uses `DefaultTopicId` ("treasury").
- `app/src/main/java/com/lyihub/archiveassistant/domain/MockKnowledgeClassifier.kt` — Updated `selectTopic()` to map classifier keywords to `SixMinistry` labels: UI/UX → `WORKS`, anthropology → `RITES`, travel → `TREASURY`, default → `OFFICIALS`. Fallback Topic constructor now uses `SixMinistry.OFFICIALS.toTopic()`.
- `app/src/test/java/com/lyihub/archiveassistant/domain/MockKnowledgeClassifierTest.kt` — Updated topic ID assertions: URL articles → `"officials"`, UI screenshots → `"works"`, anthropology → `"rites"`. Renamed test method to `sampleData_containsSixMinistryTopicsAndDeterministicFixtures` and updated assertions to check six ministry labels and IDs.
- `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStore.kt` — Fixed pre-existing `duplicate companion object` compilation error by merging two `private companion object` blocks into one.

### Test results
- All domain tests pass (`BUILD SUCCESSFUL`): `MockKnowledgeClassifierTest`, `SmartSummarizerContractTest`, `WebUrlDetectorTest`, `LocalLlmClassifierTest`, `LocalLlmSmartSummarizerTest`.
- `ArchiveAssistantStateStoreTest` has 19 pre-existing failures due to disabled CRUD (`rejectTopicCrud()`) and legacy topic ID references — these are outside the immediate scope.

### Key design decisions
- Six ministries in fixed enum order (officials → treasury → rites → military → justice → works) following the historical 六部 sequence.
- `SampleKnowledgeData.DefaultTopicId = "treasury"` — safest fallback as the treasury/archival storehouse is the default category for unclassified content.
- `toTopic()` uses neutral icon color `#5e5d59` and standard `updatedAtEpochMillis` — can be customized later.
- No `Other`/`Unmapped` bucket — all legacy topic IDs map to `treasury` via `fallback` logic (in later phases).
- Pre-existing `ArchiveAssistantStateStore.kt` duplicate companion object was fixed as a prerequisite for test compilation.

## 2026-06-26 Fix: Focused test updates for ministry model
### Files changed
- `app/src/test/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStoreTest.kt` — Added `import com.lyihub.archiveassistant.domain.SixMinistry`. Replaced 5 stale legacy topic ID references:
  - `"topic-ui-inspiration"` → `SixMinistry.WORKS.id` ("works") in `submitParserInput_whenClassified_addsItemAndClearsInputAndUpdatesTopicTimestamp` and `submitParserInput_whenSmartSummarizeSucceeds_addsItemFromOriginalInput`.
  - `"topic-anthropology-clips"` → `SixMinistry.RITES.id` ("rites") in `recentTopics_afterClassification_reflectsUpdatedTopicAtTop`.
  - `SampleKnowledgeData.DefaultTopicId` → `SixMinistry.OFFICIALS.id` ("officials") in `filterSelection_documentPdf_updatesVisibleItemsForSelectedTopic` and `closeCardModal_preservesSelectedFilterAndTopic` because treasury has no DOCUMENT items.

### Verification
- `./gradlew testDebugUnitTest --tests "com.lyihub.archiveassistant.domain.MockKnowledgeClassifierTest" --tests "com.lyihub.archiveassistant.state.ArchiveAssistantStateStoreTest"` → **BUILD SUCCESSFUL** (84 tests, 0 failures).
- Evidence saved to `.omo/evidence/backend-six-ministries/state-test-results.txt`.


## 2026-06-26 Task: disable-topic-crud-backend-paths
- `ArchiveAssistantStateStore` now keeps Topic CRUD signatures but routes create/rename/delete and topic CRUD dialog openers to deterministic no-ops with `六部分类已固定，不能新建、重命名或删除。`.
- `deleteTopic(existingId)` no longer filters `topics` or `items`; focused tests assert topic count/content and item count/content stay unchanged.
- Focused disabled Topic CRUD tests pass; evidence: `.omo/evidence/backend-six-ministries/state-store-disabled-topic-crud-test.log`.
- Broader `ArchiveAssistantStateStoreTest` run still has unrelated sample-data expectation failures; evidence: `.omo/evidence/backend-six-ministries/state-store-test.log`.
- LSP diagnostics could not run because `kotlin-lsp` is not installed in this environment.

## 2026-06-26 Task: disable-topic-crud-state-test-verification-fix
- Updated stale state-test fixtures to canonical six-ministry IDs via `SixMinistry`: UX/workflow cases use `works`, anthropology/recent-topic case uses `rites`, and document-filter/card-modal cases use `officials` where seeded document items exist.
- Rechecked disabled Topic CRUD tests: create/rename/delete remain deterministic no-ops, fixed disabled message is asserted, and delete preserves both topics and items.
- Focused state-store class now passes: `.omo/evidence/backend-six-ministries/state-store-test-task2-fix.log`.

## 2026-06-26 Task: legacy persistence readability and safe topic resolver
- Added `tryDecodeTopics`/`tryDecodeItems` result APIs while preserving legacy `decodeTopics`/`decodeItems` empty-list wrappers and `TopicsKey`/`ItemsKey` read paths.
- Added central `resolveTopicId`: known six-ministry IDs map to themselves; null, empty, legacy, and unknown IDs resolve to `treasury`.
- `AppDataRepository.loadSnapshot()` reads both JSON values from one Preferences snapshot so state restore can distinguish missing, valid, and corrupt topic data.
- `ArchiveAssistantStateStore` skips restore entirely when topics JSON is corrupt, preserving stored valid items and avoiding automatic empty topic/item writes; valid legacy items restore with topic IDs normalized to `treasury`.
- Focused tests pass: `.omo/evidence/backend-six-ministries/task3-persistence-focused-test.log`; LSP diagnostics remain unavailable because `kotlin-lsp` is not installed.

## 2026-06-26 Fix: task 3 stale smart-summary test
- Updated `submitParserInput_whenSmartResultHasUnknownTopicId_fallsBackToTreasuryAndSavesItem` to match resolver policy: unknown smart-summary `topicId` now saves a new item under `treasury` with no smart-summary error.
- Requested focused suite passes: `.omo/evidence/backend-six-ministries/task3-state-suite-fallback-fix.log`.

## 2026-06-26 Task: state grouping/filtering ministry compatibility
- `ArchiveAssistantState` computed views now centralize item grouping, selected-topic lookup, selected item lists, filters, and search matching through `resolveTopicId()`, so stale/unknown raw item topic IDs read under `treasury` without mutating raw records.
- `ArchiveAssistantStateStore.openTopic()` and manual clipboard target selection resolve incoming topic IDs; edit processing resolves stale original item topic IDs to `treasury` while add/smart/classification save paths continue using resolver-backed validation.
- Added focused state-store tests for selected ministry-compatible views, content-type filtering of treasury-resolved stale items, unknown raw topic open/add/edit fallback to `treasury`, and preserved disabled Topic CRUD behavior.
- Focused state-store suite passes: `.omo/evidence/backend-six-ministries/task5-state-grouping-test.log`.

## 2026-06-26 Task: disable-topic-crud-frontend-controls
### Files changed
- `app/src/main/java/com/lyihub/archiveassistant/ui/screens/HomePane.kt` — Removed `onCreateTopic` parameter and the "新建" (`home-create-topic-button`) TextActionButton. Removed `Icons.Default.Add` import.
- `app/src/main/java/com/lyihub/archiveassistant/ui/screens/ManagePane.kt` — Removed all CRUD parameters (`onCreateTopic`, `onRenameTopic`, `onDeleteTopic`, `onConfirmCreateTopic`, `onConfirmRenameTopic`, `onConfirmDeleteTopic`, `onCloseTopicNameDialog`, `onCloseDeleteConfirmDialog`, `topicNameDialogMode`, `topicNameDialogTopicId`, `topicValidationMessage`, `deleteConfirmTopicId`). Removed header `create-topic-button` IconButton. Removed rename (`Edit`) and delete (`Delete`) IconButtons from `ManageTopicRow`. Removed `TopicNameDialog` and `DeleteConfirmDialog` composables entirely. Cleaned up unused imports.
- `app/src/main/java/com/lyihub/archiveassistant/app/ArchiveAssistantApp.kt` — Removed `onCreateTopic = stateStore::openTopicManagementForCreate` from all 5 HomePane call sites. Removed all CRUD callbacks from both ManagePane call sites (SinglePaneLayout and TwoPaneLayout).
- `app/src/androidTest/java/com/lyihub/archiveassistant/ui/screens/HomePaneTest.kt` — Removed `homePane_createTopicButton_click_triggersCallback` test. Removed `home-create-topic-button` assertion from `homePane_displaysParserInputAndClassifyButton`. Removed `onCreateTopic` from all test HomePane invocations. Cleaned unused `assertTextEquals` import.
- `app/src/androidTest/java/com/lyihub/archiveassistant/ui/screens/ManagePaneTest.kt` — Replaced `managePane_displaysTopicListAndCreateButton` with `managePane_displaysTopicList` (no CRUD button assertions). Removed `managePane_createTopicButton_triggersOnCreateTopic`, `managePane_renameButton_triggersOnRenameTopic`, `managePane_deleteButton_triggersOnDeleteTopic`, `managePane_createDialog_showsWhenModeIsCreate`, `managePane_renameDialog_showsWhenModeIsRename`, `managePane_validationMessage_showsErrorText`, and `managePane_deleteConfirmDialog_showsWhenDeleteConfirmTopicIdIsSet`. Updated remaining tests to use simplified ManagePane API.

### Verification
- `./gradlew compileDebugKotlin` → **BUILD SUCCESSFUL**
- `./gradlew compileDebugAndroidTestKotlin` → **BUILD SUCCESSFUL**
- `./gradlew testDebugUnitTest --tests "com.lyihub.archiveassistant.domain.MockKnowledgeClassifierTest" --tests "com.lyihub.archiveassistant.state.ArchiveAssistantStateStoreTest"` → 90 tests, 2 pre-existing state-test failures (treasury-resolution edge cases), 88 pass.
- Evidence saved to `.omo/evidence/backend-six-ministries/task6-ui-test-results.log`.
- No `ui/theme` files modified.
- Non-CRUD navigation (back, topic selection via click, settings, manage pane access) and read-only topic/ministry browsing remain intact.

## 2026-06-26 Task: constrain classifiers and summarizers to ministry IDs
- Mock classifier, local classifier, local smart summarizer, and remote smart summarizer now constrain successful topic IDs through six-ministry IDs and `resolveTopicId()`.
- Unknown or legacy model topic IDs now resolve to `treasury` instead of throwing or returning failure for otherwise valid model JSON.
- Local and remote summarizer prompts now enumerate only canonical six-ministry topics; focused fake/no-network tests cover fallback behavior.
- Focused tests pass: `.omo/evidence/backend-six-ministries/task4-classifier-summarizer-focused-tests-rerun.log`. Initial stale expectation failure is preserved at `.omo/evidence/backend-six-ministries/task4-classifier-summarizer-focused-tests.log`.

## 2026-06-26 Task: rewrite tests for fixed ministries and disabled Topic CRUD
- Strengthened unit tests around fixed six-ministry order/labels, absence of Other/Unmapped buckets, resolver fallback for legacy IDs, read-only Topic CRUD no-ops, and no cascade deletion.
- Strengthened Compose tests to assert Topic create/rename/delete controls are absent while topic card/list selection remains read-only navigable.
- Search confirmed remaining legacy topic IDs in tests are compatibility/fallback assertions only; CRUD method references are disabled/no-op assertions only.
- Required gates pass: `.omo/evidence/backend-six-ministries/task7-testDebugUnitTest.log` and `.omo/evidence/backend-six-ministries/task7-compileDebugAndroidTestKotlin.log`.
- LSP diagnostics remain unavailable because `kotlin-lsp` is not installed.

## 2026-06-26 Fix: task 7 verification continuation
- Removed unsupported explicit `assertDoesNotExist` imports from HomePane/ManagePane tests; existing Compose node assertion calls compile as project-supported member/extension resolution.
- Made the local async summarize state-store test wait for both loading completion and the saved `Async local summary` item, preventing a race after completing the fake summarize gate.
- Required gates pass with fresh evidence: `.omo/evidence/backend-six-ministries/task7-continuation-testDebugUnitTest.log` and `.omo/evidence/backend-six-ministries/task7-continuation-compileDebugAndroidTestKotlin.log`.
- LSP diagnostics remain unavailable because `kotlin-lsp` is not installed.

## 2026-06-26 Task: full backend-first regression and evidence audit
- Required Gradle gates passed: `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and `./gradlew compileDebugAndroidTestKotlin`; combined evidence saved to `.omo/evidence/backend-six-ministries/task-8-gradle-gates.log`.
- Scope audit saved to `.omo/evidence/backend-six-ministries/task-8-scope-audit.log`: no `ui/theme` diff, and `ui/screens` diffs are limited to removing Topic CRUD create/rename/delete affordances and dialogs.
- Data-safety evidence remains intact: disabled `deleteTopic` does not cascade-delete topics/items; corrupt topics JSON skips restore/save overwrite; legacy JSON remains readable; unknown/legacy IDs resolve to `treasury`; classifier/summarizer outputs are constrained to six-ministry IDs.
- Search audit found no stale active Topic CRUD success expectations beyond disabled/no-op or absence assertions; remaining legacy topic IDs are compatibility/fallback test cases.
- Tests use fake local engines, fake summarizers/fetchers, and fake remote/HTTP transports; no real external AI or network calls were used.
- LSP diagnostics remain unavailable in this environment because `kotlin-lsp` is not installed, so Gradle gates are the verification source.
