# Backend-First Six Ministries Data Model

## TL;DR
> **Summary**: First convert ArchiveAssistant’s backend taxonomy from user-managed Topics toward fixed Six Ministries, while keeping frontend pages visually/layout-wise unchanged. Topic create/rename/delete becomes disabled and non-destructive; legacy topic persistence remains readable until migration is proven.
> **Deliverables**:
> - Canonical immutable Six Ministries model with stable IDs
> - Disabled/non-destructive Topic CRUD backend rules
> - Legacy topic JSON compatibility and safe topicId resolution
> - Classifier/summarizer routing constrained to ministry IDs
> - Minimal UI callback/button handling only where required to avoid broken actions
> - Updated unit/UI tests proving no data loss and no frontend page redesign
> **Effort**: Medium
> **Parallel**: YES - 3 waves
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 5 → Task 8

## Context
### Original Request Revision
用户调整方向：先改后端的数据模型和处理规则；Topic 的创建、重命名、删除能力不保留，或前端取消按钮；前端页面先不变。

### Revised Decisions
- This plan supersedes the previous frontend/visual-heavy imperial redesign plan.
- Six Ministries become canonical immutable backend taxonomy.
- Frontend page layout, visual theme, 三省壳层, 宫廷视觉风格, 字体纹样, and page redesign are explicitly deferred.
- Topic CRUD is not preserved as a user capability.
- For safety, this plan uses “Ministry canonical, Topic compatibility”: keep legacy `Topic`/`topicId` data readable while routing new processing to ministry IDs.
- Do not hard-delete legacy topic data in the first backend phase.

### Research Summary
- `Topic` is central in `Models.kt`, `ArchiveAssistantState.kt`, `ArchiveAssistantStateStore.kt`, `AppDataPreferences.kt`, `AppDataRepository.kt`, classifiers, summarizers, and UI tests.
- `KnowledgeItem.topicId`, `ClassificationPayload.topicId`, and `SmartSummarizeResult.Success.topicId` can remain string IDs and transition to ministry IDs.
- `deleteTopic()` currently cascades deletion of all items with matching `topicId`; this is the highest data-loss risk and must be guarded first.
- UI CRUD controls exist in `HomePane.kt`, `ManagePane.kt`, and `ArchiveAssistantApp.kt`, but page structure can remain unchanged.
- Existing persistence uses JSON/preferences; failed decode returns empty lists, so compatibility and backup tests are mandatory.

### Metis/Oracle Guidance Incorporated
- Disable/guard Topic CRUD before classifier/summarizer contract changes.
- Preserve readability of legacy `app_topics_json` and `app_items_json` until migration is verified.
- Centralize compatibility in state/domain layers instead of scattering UI conditionals.
- Do not change frontend page layout in this phase.
- Add tests for legacy JSON, unknown topic IDs, disabled CRUD, and no cascade deletion.

## Work Objectives
### Core Objective
Make Six Ministries the backend source of truth for classification/processing while making user-managed Topic CRUD impossible and non-destructive, without redesigning frontend pages.

### Deliverables
- Canonical `Ministry` model/constants with six fixed IDs and display labels.
- Compatibility resolver that maps legacy topic IDs and unknown IDs to valid ministry IDs without deleting data.
- Disabled Topic CRUD behavior in `ArchiveAssistantStateStore` with deterministic no-op/error state.
- Persistence compatibility: old topics/items can be read; items remain preserved; topic writes are either retained only for backward compatibility or frozen per task decision below.
- Classifier/summarizer outputs constrained to ministry IDs.
- Tests updated to remove expectations that Topic can be user-created/renamed/deleted.
- Minimal UI changes only to hide/disable CRUD buttons if necessary; no page redesign.

### Definition of Done
- `./gradlew testDebugUnitTest` passes.
- `./gradlew assembleDebug` passes.
- `./gradlew compileDebugAndroidTestKotlin` passes.
- Existing legacy topics/items JSON loads with zero item deletion.
- Calling create/rename/delete topic state-store methods cannot create, rename, or delete topics/items.
- `deleteTopic(existingId)` leaves item count unchanged.
- Every new classification/summarization result uses one of six ministry IDs.
- Frontend layout and theme files are not redesigned in this phase.

### Must Have
- Canonical ministry IDs/order/labels:
  - `officials`: `吏 · 名籍`
  - `treasury`: `户 · 府库`
  - `rites`: `礼 · 典章`
  - `military`: `兵 · 行令`
  - `justice`: `刑 · 稽核`
  - `works`: `工 · 营造`
- Fallback for unmapped/unknown legacy topic ID: resolve to `treasury` for processing, while preserving raw item data where currently persisted.
- Legacy topic-to-ministry mapping rule for this backend-first phase: only the six known ministry IDs map to themselves. Every legacy custom topic ID, stale topic ID, corrupt topic reference, empty topic ID, or unknown raw `topicId` resolves to `treasury` for grouping/processing. Do not attempt keyword remapping in this phase.
- Legacy load/save invariant: if `app_topics_json` is corrupt/partial but `app_items_json` is valid, loading must preserve all valid items and must not automatically persist empty topics or empty items back to DataStore. Any decode failure must be treated as read-time compatibility fallback, not as permission to overwrite stored JSON.
- Disabled CRUD policy: `createTopic`, `renameTopic`, and `deleteTopic` must be deterministic no-ops that set a user-visible validation/error message if one already exists in state; they must not throw and must not delete items.
- `selectedTopicId` may remain named as-is in this phase to avoid frontend churn, but its valid new values should be ministry IDs.
- Existing custom topics may remain readable from legacy JSON for compatibility, but no new custom topics can be created.

### Must NOT Have
- No frontend page redesign, no new imperial theme, no 三省 visual shell in this phase.
- No deletion of legacy topics/items during migration.
- No removal of legacy persistence read paths in the first backend phase.
- No cascade deletion from disabled topic deletion.
- No new “Other/Unmapped” ministry.
- No real external AI/network calls in tests.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: tests-after using existing JUnit4 and Compose UI tests.
- Evidence path: `.omo/evidence/backend-six-ministries/task-{N}-{slug}.log`.
- Required commands: `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, `./gradlew compileDebugAndroidTestKotlin`.
- Frontend-unchanged verification: `git diff -- app/src/main/java/com/lyihub/archiveassistant/ui/theme app/src/main/java/com/lyihub/archiveassistant/ui/screens` must show only minimal CRUD button disable/hide changes, with no layout/theme redesign.

## Execution Strategy
### Parallel Execution Waves
Wave 1: Tasks 1-3 — canonical model, CRUD guards, persistence compatibility.
Wave 2: Tasks 4-6 — classifier/summarizer constraints, state grouping/filtering, minimal UI callback cleanup.
Wave 3: Tasks 7-8 — test rewrite and full regression/evidence.

### Dependency Matrix
- Task 1 blocks Tasks 3, 4, 5, 7.
- Task 2 blocks Tasks 5, 6, 7.
- Task 3 blocks Tasks 5, 7.
- Tasks 1-6 block Task 7.
- Task 7 blocks Task 8 and Final Verification.

### Agent Dispatch Summary
- Wave 1: 3 tasks → deep for compatibility/persistence, quick for model.
- Wave 2: 3 tasks → deep for contracts/state, quick for minimal UI.
- Wave 3: 2 tasks → deep/unspecified-high for tests and QA.

## TODOs

- [x] 1. Add canonical Six Ministries model while retaining Topic compatibility

  **What to do**: Add `Ministry` constants/model in `app/src/main/java/com/lyihub/archiveassistant/domain/Models.kt` or a sibling domain file. Use the six stable IDs and display labels from Must Have. Keep `Topic`, `KnowledgeItem.topicId`, `ClassificationPayload.topicId`, and `SmartSummarizeResult.Success.topicId` readable for compatibility. Update sample data so canonical selectable categories are six ministries, not arbitrary sample topics.
  **Must NOT do**: Do not remove `Topic` type yet if doing so causes broad frontend churn. Do not introduce an `other` ministry. Do not rename frontend panes.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: focused domain constants/model change.
  - Skills: [] - No external skill needed.
  - Omitted: `frontend-ui-ux` - No visual work in this phase.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: 3,4,5,7 | Blocked By: none

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/domain/Models.kt` - `Topic`, `KnowledgeItem.topicId`, classification payloads.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/domain/SampleKnowledgeData.kt` - existing sample topics/items.
  - Test: `app/src/test/java/com/lyihub/archiveassistant/domain/MockKnowledgeClassifierTest.kt` - topic fixture expectations.

  **Acceptance Criteria**:
  - [ ] Exactly six canonical ministries exist in fixed order.
  - [ ] Existing code can still compile with `Topic` compatibility.
  - [ ] Sample topics/categories are six ministries or derived from the six ministry constants.
  - [ ] No ministry CRUD API is introduced.

  **QA Scenarios**:
  ```
  Scenario: Canonical ministries are fixed
    Tool: Bash
    Steps: Run ./gradlew testDebugUnitTest with test asserting IDs/order/labels.
    Expected: Six IDs exactly: officials, treasury, rites, military, justice, works.
    Evidence: .omo/evidence/backend-six-ministries/task-1-ministry-model.log

  Scenario: Topic compatibility still compiles
    Tool: Bash
    Steps: Run ./gradlew assembleDebug.
    Expected: Build passes without broad frontend refactor.
    Evidence: .omo/evidence/backend-six-ministries/task-1-compat-build.log
  ```

  **Commit**: NO | Message: `feat(domain): add fixed ministry taxonomy` | Files: domain files, tests

- [x] 2. Disable Topic CRUD backend paths and remove cascade deletion risk

  **What to do**: In `ArchiveAssistantStateStore.kt`, make `createTopic`, `renameTopic`, `deleteTopic`, and confirm/open wrappers deterministic disabled operations. Preferred policy: no-op plus set existing validation/error message such as `六部分类已固定，不能新建、重命名或删除。`. Ensure `deleteTopic()` never deletes items. Remove or freeze `nextTopicIndex` usage. Keep methods only if UI/tests still call them, to avoid frontend churn.
  **Must NOT do**: Do not throw runtime exceptions from UI callbacks. Do not delete items. Do not remove methods if it forces page rewiring beyond minimal scope.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: high data-loss risk in state store.
  - Skills: [] - No external skill needed.
  - Omitted: `frontend-ui-ux` - Backend/state behavior only.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 5,6,7 | Blocked By: none

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStore.kt` - CRUD methods and cascade delete.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantState.kt` - dialog/error state fields.
  - Test: `app/src/test/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStoreTest.kt` - current CRUD tests to rewrite.

  **Acceptance Criteria**:
  - [ ] Calling `createTopic` does not change topic count.
  - [ ] Calling `renameTopic` does not change topic title.
  - [ ] Calling `deleteTopic` does not change topic count or item count.
  - [ ] Disabled operation produces deterministic state feedback and no crash.

  **QA Scenarios**:
  ```
  Scenario: Delete topic is non-destructive
    Tool: Bash
    Steps: Run unit test calling deleteTopic(existingId) with items attached.
    Expected: Item count and topic count unchanged; disabled feedback set.
    Evidence: .omo/evidence/backend-six-ministries/task-2-delete-no-cascade.log

  Scenario: Create and rename are disabled
    Tool: Bash
    Steps: Run unit test calling createTopic and renameTopic.
    Expected: No new topic and no title change; no exception.
    Evidence: .omo/evidence/backend-six-ministries/task-2-crud-disabled.log
  ```

  **Commit**: NO | Message: `fix(state): disable topic crud safely` | Files: state store/state tests

- [x] 3. Preserve legacy persistence readability and add safe topicId resolver

  **What to do**: Keep `AppDataPreferences.decodeTopics()`/legacy topic read paths working for existing `app_topics_json`. Add a central resolver that maps any incoming `topicId` to a valid ministry ID for processing: if already one of the six ministry IDs, use it; otherwise fallback to `treasury`. Preserve raw item records and avoid destructive rewrites until tests pass. If `app_topics_json` is corrupt/partial but `app_items_json` is valid, loading must preserve all valid items and must not automatically persist empty topics or empty items back to DataStore. Writes may continue writing legacy-compatible topics if current repository requires it, but new canonical data should be ministry based.
  **Must NOT do**: Do not delete `TopicsKey` read path in this phase. Do not let failed topic decode erase items. Do not auto-save empty decoded state after partial legacy decode failure. Do not create an `unmapped` bucket. Do not implement keyword remapping in this phase.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: persistence/migration compatibility.
  - Skills: [] - No external skill needed.
  - Omitted: `frontend-ui-ux` - Data compatibility only.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: 5,7 | Blocked By: 1

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/data/AppDataPreferences.kt` - JSON encode/decode.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/data/AppDataRepository.kt` - load/save all data.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStore.kt` - `loadPersistedStateAsync`, `persistData`.

  **Acceptance Criteria**:
  - [ ] Legacy topics JSON still decodes.
  - [ ] Legacy items JSON still decodes and item count is preserved.
  - [ ] Unknown/stale item `topicId` resolves to `treasury` for grouping/processing.
  - [ ] Corrupt `app_topics_json` plus valid `app_items_json` preserves items and does not write empty topics/items back to storage.
  - [ ] No automatic destructive rewrite occurs on failed topic decode.

  **QA Scenarios**:
  ```
  Scenario: Legacy JSON loads without item loss
    Tool: Bash
    Steps: Run unit test loading legacy app_topics_json and app_items_json fixtures.
    Expected: All items load; resolved grouping uses ministry IDs.
    Evidence: .omo/evidence/backend-six-ministries/task-3-legacy-load.log

  Scenario: Stale topicId falls back safely
    Tool: Bash
    Steps: Run unit test with item.topicId = "deleted-custom-topic" and no matching legacy topic.
    Expected: Resolver returns treasury; item remains present.
    Evidence: .omo/evidence/backend-six-ministries/task-3-stale-fallback.log

  Scenario: Corrupt topics JSON does not overwrite valid items
    Tool: Bash
    Steps: Run persistence unit test with corrupt app_topics_json and valid app_items_json, then trigger load path and inspect saved values/write calls.
    Expected: Items remain loaded; no automatic save writes empty topics/items over existing JSON.
    Evidence: .omo/evidence/backend-six-ministries/task-3-corrupt-topics-no-overwrite.log
  ```

  **Commit**: NO | Message: `fix(data): preserve legacy topic compatibility` | Files: data/state/domain tests

- [x] 4. Constrain classifiers and summarizers to ministry IDs

  **What to do**: Update `MockKnowledgeClassifier`, `LocalLlmClassifier`, `LocalLlmSmartSummarizer`, `RemoteApiSmartSummarizer` contracts/prompts/parsing as needed so accepted output IDs are only six ministry IDs. If external/local model returns legacy title/unknown topic, resolve through the central resolver/fallback. Keep method signatures if changing them causes unnecessary churn.
  **Must NOT do**: Do not call real external APIs in tests. Do not accept arbitrary user-created topic IDs as new output.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: multiple domain/data contracts.
  - Skills: [] - No external skill needed.
  - Omitted: `frontend-ui-ux` - No UI work.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 7 | Blocked By: 1

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/domain/MockKnowledgeClassifier.kt` - deterministic classifier.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/domain/LocalLlmClassifier.kt` - local classifier prompt/parser.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/domain/LocalLlmSmartSummarizer.kt` - local summarizer parser.
  - Test: `app/src/test/java/com/lyihub/archiveassistant/domain/LocalLlmClassifierTest.kt` - classifier behavior.

  **Acceptance Criteria**:
  - [ ] New classification payload topicId is always one of six ministry IDs.
  - [ ] Unknown/legacy classifier output resolves to `treasury` or a mapped ministry.
  - [ ] Existing no-network test isolation remains intact.
  - [ ] Smart summarizer success topicId is validated against ministries.

  **QA Scenarios**:
  ```
  Scenario: Mock classifier emits ministry IDs
    Tool: Bash
    Steps: Run MockKnowledgeClassifier tests across representative inputs.
    Expected: All payload topicIds are valid ministry IDs.
    Evidence: .omo/evidence/backend-six-ministries/task-4-mock-classifier.log

  Scenario: Unknown LLM topic output resolves safely
    Tool: Bash
    Steps: Run LocalLlmClassifier/SmartSummarizer parser tests with unknown topic output.
    Expected: Result resolves to treasury without exception.
    Evidence: .omo/evidence/backend-six-ministries/task-4-unknown-output.log
  ```

  **Commit**: NO | Message: `fix(domain): constrain ai routing to ministries` | Files: classifier/summarizer files and tests

- [x] 5. Update state grouping/filtering to treat selectedTopicId as ministry-compatible

  **What to do**: Adjust `ArchiveAssistantState` computed properties and state-store validation so `selectedTopicId`, `itemsByTopic`, `selectedTopic`, `selectedTopicItems`, `filteredSelectedTopicItems`, `recentTopics`, and `searchedTopics` work with ministry IDs and legacy-compatible topics. Keep names if renaming would force frontend churn. Add resolver usage wherever `topicId` validation currently checks `state.topics.none { it.id == topicId }`. Unknown raw IDs must consistently resolve to `treasury`; do not reject unknown IDs after resolver fallback.
  **Must NOT do**: Do not redesign pane routing. Do not rename public state fields unless all references are updated safely.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: core state semantics.
  - Skills: [] - No external skill needed.
  - Omitted: `frontend-ui-ux` - State logic only.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: 7 | Blocked By: 1,2,3

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantState.kt` - computed grouping/filtering.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStore.kt` - add/edit/classify validation.
  - Test: `app/src/test/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStoreTest.kt` - state behavior.

  **Acceptance Criteria**:
  - [ ] Selected ministry/topic displays valid items.
  - [ ] Add/edit item with unknown raw topicId resolves to `treasury` and preserves the item unless another existing validation error applies.
  - [ ] ContentType filtering remains unchanged.
  - [ ] Search behavior remains functional with ministry-backed categories.

  **QA Scenarios**:
  ```
  Scenario: selectedTopicId works with ministry ID
    Tool: Bash
    Steps: Run unit test setting selectedTopicId = works with works items.
    Expected: selectedTopicItems returns only works items.
    Evidence: .omo/evidence/backend-six-ministries/task-5-selected-ministry.log

  Scenario: ContentType filter unchanged
    Tool: Bash
    Steps: Run unit test selecting treasury and DOCUMENT filter.
    Expected: Only treasury document items appear.
    Evidence: .omo/evidence/backend-six-ministries/task-5-content-filter.log
  ```

  **Commit**: NO | Message: `fix(state): group items by ministry ids` | Files: state files and tests

- [x] 6. Apply minimal frontend callback/button handling only for disabled CRUD

  **What to do**: Keep frontend pages visually/layout-wise unchanged. If backend-disabled actions would leave broken buttons, hide or disable only Topic create/rename/delete controls: `HomePane` new topic button, `ManagePane` create/edit/delete buttons/dialog triggers, and `ArchiveAssistantApp` CRUD callback wiring. Keep ManagePane list/tap navigation if still useful. Do not change theme, typography, ministry visual style, or page hierarchy.
  **Must NOT do**: Do not redesign UI. Do not introduce imperial visuals. Do not remove non-CRUD navigation unless required by disabled CRUD safety.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: minimal UI cleanup.
  - Skills: [] - No visual skill needed because no redesign.
  - Omitted: `frontend-ui-ux` - Avoid visual expansion.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 7 | Blocked By: 2

  **References**:
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/ui/screens/HomePane.kt` - `home-create-topic-button`, `manage-button` area.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/ui/screens/ManagePane.kt` - create/edit/delete controls and dialogs.
  - Pattern: `app/src/main/java/com/lyihub/archiveassistant/app/ArchiveAssistantApp.kt` - CRUD callback wiring.
  - Test: `app/src/androidTest/java/com/lyihub/archiveassistant/ui/screens/ManagePaneTest.kt` - CRUD UI tests to rewrite/remove.

  **Acceptance Criteria**:
  - [ ] No visible enabled create/rename/delete Topic controls remain.
  - [ ] Existing panes still compile and navigate.
  - [ ] Theme files are unchanged.
  - [ ] UI diffs are limited to CRUD affordance removal/disable and callback cleanup.

  **QA Scenarios**:
  ```
  Scenario: Topic CRUD controls absent or disabled
    Tool: Bash
    Steps: Run Compose UI tests asserting create-topic-button, rename-topic-button-*, and delete-topic-button-* are absent or disabled per implementation.
    Expected: User cannot initiate Topic create/rename/delete.
    Evidence: .omo/evidence/backend-six-ministries/task-6-crud-controls-disabled.log

  Scenario: Frontend redesign is not introduced
    Tool: Bash
    Steps: Run git diff -- app/src/main/java/com/lyihub/archiveassistant/ui/theme app/src/main/java/com/lyihub/archiveassistant/ui/screens.
    Expected: No theme diff; screen diff limited to CRUD controls/callbacks/tests.
    Evidence: .omo/evidence/backend-six-ministries/task-6-ui-diff.log
  ```

  **Commit**: NO | Message: `fix(ui): disable topic crud affordances` | Files: minimal UI/app wiring and UI tests

- [x] 7. Rewrite tests for fixed ministries and disabled Topic CRUD

  **What to do**: Update affected unit/UI tests. Replace old Topic CRUD success tests with disabled/no-op tests. Add tests for six ministries, legacy JSON compatibility, resolver fallback, no cascade deletion, classifier/summarizer ministry outputs, state grouping, and absence/disabled state of CRUD controls. Remove only tests that exclusively assert removed Topic CRUD UI success.
  **Must NOT do**: Do not weaken unrelated tests. Do not delete broad test files wholesale if non-CRUD coverage can remain.

  **Recommended Agent Profile**:
  - Category: `deep` - Reason: broad test suite update.
  - Skills: [] - No external skill needed.
  - Omitted: `visual-qa` - No visual redesign in this phase.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 8 | Blocked By: 1-6

  **References**:
  - Test: `app/src/test/java/com/lyihub/archiveassistant/state/ArchiveAssistantStateStoreTest.kt` - CRUD/state tests.
  - Test: `app/src/androidTest/java/com/lyihub/archiveassistant/ui/screens/ManagePaneTest.kt` - CRUD UI tests.
  - Test: `app/src/androidTest/java/com/lyihub/archiveassistant/ui/screens/HomePaneTest.kt` - create/manage button tests.
  - Test: `app/src/test/java/com/lyihub/archiveassistant/domain/MockKnowledgeClassifierTest.kt` - fixture expectations.

  **Acceptance Criteria**:
  - [ ] Unit tests cover disabled create/rename/delete and no item deletion.
  - [ ] Unit tests cover legacy JSON compatibility and fallback resolver.
  - [ ] Domain tests cover ministry-only classifier/summarizer outputs.
  - [ ] Compose UI tests cover absence/disabled state of CRUD controls.
  - [ ] `./gradlew testDebugUnitTest` and `./gradlew compileDebugAndroidTestKotlin` pass.

  **QA Scenarios**:
  ```
  Scenario: Unit suite validates backend migration rules
    Tool: Bash
    Steps: Run ./gradlew testDebugUnitTest.
    Expected: All unit tests pass, including new ministry/CRUD-disabled tests.
    Evidence: .omo/evidence/backend-six-ministries/task-7-unit-suite.log

  Scenario: Android test sources compile after UI test updates
    Tool: Bash
    Steps: Run ./gradlew compileDebugAndroidTestKotlin.
    Expected: Instrumented test sources compile with updated selectors/expectations.
    Evidence: .omo/evidence/backend-six-ministries/task-7-android-test-compile.log
  ```

  **Commit**: NO | Message: `test: cover fixed ministries and disabled topic crud` | Files: test files

- [x] 8. Run full backend-first regression and evidence audit

  **What to do**: Run final commands and collect evidence. Verify no frontend redesign slipped in. Summarize data safety: no cascade deletion, legacy JSON readable, unknown IDs fallback, classifier emits ministries, CRUD controls disabled/hidden.
  **Must NOT do**: Do not claim visual redesign. Do not run formatters/codegen that rewrite unrelated files. Do not fix unrelated failures without documenting.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: broad verification and evidence.
  - Skills: [] - No external skill needed.
  - Omitted: `git-master` - No commit requested.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: Final Verification | Blocked By: 7

  **References**:
  - Build: `app/build.gradle.kts` - Gradle config.
  - Pattern: `AI-Design/06-verification-and-qa-plan.md` - existing commands.

  **Acceptance Criteria**:
  - [ ] `./gradlew testDebugUnitTest` passes.
  - [ ] `./gradlew assembleDebug` passes.
  - [ ] `./gradlew compileDebugAndroidTestKotlin` passes.
  - [ ] Evidence confirms no data-loss path from Topic deletion.
  - [ ] Evidence confirms frontend changes are minimal and not visual redesign.

  **QA Scenarios**:
  ```
  Scenario: Required Gradle gates pass
    Tool: Bash
    Steps: Run ./gradlew testDebugUnitTest && ./gradlew assembleDebug && ./gradlew compileDebugAndroidTestKotlin.
    Expected: All three commands exit 0.
    Evidence: .omo/evidence/backend-six-ministries/task-8-gradle-gates.log

  Scenario: Scope audit confirms backend-first only
    Tool: Bash
    Steps: Run git diff --stat and git diff -- app/src/main/java/com/lyihub/archiveassistant/ui/theme app/src/main/java/com/lyihub/archiveassistant/ui/screens.
    Expected: No theme redesign; UI screen changes limited to disabled/hidden Topic CRUD affordances.
    Evidence: .omo/evidence/backend-six-ministries/task-8-scope-audit.log
  ```

  **Commit**: NO | Message: `test: verify backend six ministries migration` | Files: evidence only

## Final Verification Wave
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing implementation.
- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Data Safety Review — deep
- [x] F3. Regression QA — unspecified-high
- [x] F4. Scope Fidelity Check — oracle

## Commit Strategy
- Do not commit unless explicitly requested.
- Suggested commit slices if requested later:
  - `feat(domain): add fixed ministry taxonomy`
  - `fix(state): disable topic crud safely`
  - `fix(data): preserve legacy topic compatibility`
  - `fix(domain): constrain ai routing to ministries`
  - `test: cover backend six ministries rules`

## Success Criteria
- Six Ministries are canonical backend taxonomy.
- Topic create/rename/delete cannot mutate state or delete data.
- Legacy persisted topics/items remain readable.
- New classifier/summarizer processing uses ministry IDs.
- Frontend pages remain visually/layout-wise unchanged except disabled/hidden Topic CRUD affordances.
- Required Gradle gates pass.
