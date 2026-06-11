# Knowledge Curation App Implementation Plan

## TL;DR
> **Summary**: Convert the high-fidelity prototype at `/Users/farest/Downloads/knowledge-curation-app-11.html` into a native Jetpack Compose Android app named `聚合拾遗`, optimized for Android foldable phones with simple posture-aware master/detail adaptation, and create a root `AI-Design/` documentation folder containing the overall implementation plan plus module design documents.
> **Deliverables**:
> - Root `AI-Design/` folder with overall plan and module design docs.
> - Native Compose app shell matching the prototype's master/detail/settings/manage structure, including foldable compact/unfolded behavior.
> - Local domain/data layer for topics, knowledge items, AI settings, and deterministic mock classification.
> - Compose UI for parser input, topic list, detail feed, card modal, topic management, and AI settings.
> - Unit tests, Compose UI tests, and QA evidence artifacts.
> **Effort**: Large
> **Parallel**: YES - 4 waves
> **Critical Path**: Task 1 -> Task 2 -> Task 3 -> Tasks 4-8 -> Task 9 -> Task 10 -> Final Verification

## Context
### Original Request
The user provided `/Users/farest/Downloads/knowledge-curation-app-11.html` as a high-fidelity frontend prototype for a knowledge-management app and requested functional-point analysis, an overall implementation plan, clear module division, and a root `AI-Design` folder where future implementation plans and module design documents should live.

### Interview Summary
No additional interview was required. The prototype and repo state answer the implementation-shaping questions. Default decisions are applied for unspecified product details: native Android Compose, local-first storage, deterministic mock AI classification for the first pass, and no real cloud API calls or secret transmission.

### Metis Review (gaps addressed)
Metis tool execution was unavailable, so equivalent manual review was applied. Addressed gaps:
- Guard against implementing a static mock by requiring domain models, state flows, persistence, and testable actions.
- Guard against secret leakage by persisting AI settings locally and masking API keys in UI; no network calls in this plan.
- Guard against vague design-doc work by naming exact `AI-Design/` files and required sections.
- Guard against prototype drift by requiring specific UI states and content labels from the HTML prototype.
- Guard against unverified UI by requiring Compose test tags, UI tests, and evidence files per task.
- Guard against weak foldable adaptation by requiring vivo foldable design guideline review, resizeable-window support, no compatibility black bars, no simple whole-screen scaling, and expanded-state layouts that show more useful content.

## Work Objectives
### Core Objective
Build the first native Android implementation of `聚合拾遗`, a large-model-assisted knowledge auto-distribution app, using the provided HTML prototype as the source of truth for feature scope and visual behavior, and treating Android foldable-phone adaptation as a first-class product requirement.

### Deliverables
- `AI-Design/00-overall-implementation-plan.md`
- `AI-Design/01-product-functional-analysis.md`
- `AI-Design/02-information-architecture.md`
- `AI-Design/03-data-and-state-model.md`
- `AI-Design/04-ui-module-design.md`
- `AI-Design/05-ai-engine-settings-design.md`
- `AI-Design/06-verification-and-qa-plan.md`
- Compose source modules under `app/src/main/java/com/lyihub/archiveassistant/`.
- Unit and instrumentation/UI tests under existing `app/src/test/` and `app/src/androidTest/` trees.

### Definition of Done (verifiable conditions with commands)
- `./gradlew testDebugUnitTest` passes.
- `./gradlew connectedDebugAndroidTest` passes on an available emulator/device.
- `AI-Design/` exists and contains all seven required markdown documents.
- The app launches to the parser/home screen with title `聚合拾遗` and subtitle `基于大语言模型的知识自动分配器`.
- User can add/classify input into a topic, open topic detail, filter by content type, open card detail, create/rename/delete topics, and edit AI engine settings locally.

### Must Have
- Use Jetpack Compose and Material3 already configured in `app/build.gradle.kts`.
- Keep package namespace `com.lyihub.archiveassistant`.
- Replace starter `Greeting` UI in `MainActivity.kt` with app entry point.
- Preserve prototype labels in Chinese unless a specific design doc says otherwise.
- Provide deterministic seeded data and deterministic mock classifier so tests do not depend on a network.
- Add `Modifier.testTag(...)` for primary interactive UI surfaces.
- Support simple Android foldable adaptation: folded/compact width uses single-pane navigation; unfolded/expanded width uses master/detail side-by-side; hinge/fold bounds must not be covered by primary content when posture information is available.
- Follow vivo foldable design guidance from `https://dev.vivo.com.cn/documentCenter/doc/597`: executor must verify the official page during implementation, document the applied rules in `AI-Design/04-ui-module-design.md`, keep the app size-adjustable, avoid black bars/compatibility mode during fold/unfold transitions, avoid simple proportional zoom/stretch, and use expanded-state space to show additional content via split/extended layout.

### Must NOT Have
- Must not call real AI APIs or send user content over network in this implementation pass.
- Must not hardcode real API keys or secrets.
- Must not introduce backend, account login, cloud sync, OCR, full PDF parsing, or app release automation.
- Must not implement the prototype as a WebView wrapper.
- Must not leave UI as static screenshots or static-only mocks.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: tests-after using existing JUnit, Android instrumentation, and Compose UI test dependencies.
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.sisyphus/evidence/task-{N}-{slug}.{ext}`.
- Required commands: `./gradlew testDebugUnitTest`, `./gradlew connectedDebugAndroidTest` where an emulator/device is available.

## Execution Strategy
### Parallel Execution Waves
> Target: 5-8 tasks per wave. This project has fewer tasks because the current app is a starter shell and dependencies are sequential.

Wave 1: Task 1 documentation foundation; Task 2 domain model and deterministic sample data.
Wave 2: Task 3 app architecture/state wiring; Task 4 design system and vivo foldable responsive shell.
Wave 3: Tasks 5-8 feature UI modules in parallel after shared state is available.
Wave 4: Task 9 persistence/settings hardening; Task 10 tests and evidence consolidation.

### Dependency Matrix (full, all tasks)
- Task 1: no dependencies; blocks final documentation acceptance.
- Task 2: no dependencies; blocks Tasks 3, 5, 6, 7, 8, 9, 10.
- Task 3: depends on Task 2; blocks Tasks 4-10.
- Task 4: depends on Task 3; blocks visual/foldable acceptance for Tasks 5-8.
- Task 5: depends on Tasks 2-4; blocks parser workflow tests.
- Task 6: depends on Tasks 2-4; blocks topic management tests.
- Task 7: depends on Tasks 2-4; blocks reading/detail tests.
- Task 8: depends on Tasks 2-4; blocks settings tests.
- Task 9: depends on Tasks 2, 3, 8; blocks persistence acceptance.
- Task 10: depends on Tasks 1-9; blocks final verification.

### Agent Dispatch Summary
- Wave 1 -> 2 tasks -> writing, quick
- Wave 2 -> 2 tasks -> quick, visual-engineering
- Wave 3 -> 4 tasks -> visual-engineering, quick
- Wave 4 -> 2 tasks -> quick, unspecified-high

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Create `AI-Design/` Documentation Set

  **What to do**: Create root `AI-Design/` and add the seven required markdown files listed in Deliverables. Each file must include source-of-truth references to `/Users/farest/Downloads/knowledge-curation-app-11.html`, current Android repo paths, scope, module responsibilities, and acceptance checks. `00-overall-implementation-plan.md` must summarize feature modules and execution order. `01-product-functional-analysis.md` must list prototype functions: parser input, AI classify, recent topics, detail tabs, card modal, topic manage, settings cloud/local toggle, and foldable master/detail adaptation. `02-information-architecture.md` must define home/detail/settings/manage navigation states for folded compact, half-open/tabletop if posture is exposed, and unfolded expanded layouts. `03-data-and-state-model.md` must define Topic, KnowledgeItem, ContentType, AiEngineSettings, AppPane, and layout mode state. `04-ui-module-design.md` must define Compose components, foldable breakpoints/posture rules, hinge avoidance behavior, vivo guideline decisions from `https://dev.vivo.com.cn/documentCenter/doc/597`, and test tags. `05-ai-engine-settings-design.md` must define local-only settings and masking rules. `06-verification-and-qa-plan.md` must define Gradle commands, vivo foldable layout QA cases, and evidence paths.
  **Must NOT do**: Do not edit app code in this task. Do not create docs outside `AI-Design/`.

  **Recommended Agent Profile**:
  - Category: `writing` - Reason: design documentation and functional analysis.
  - Skills: [] - no special skill required.
  - Omitted: `frontend-ui-ux` - implementation design is documented but no UI code is changed here.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: final documentation acceptance | Blocked By: none

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:230` - app body state starts at `app-state-home`.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:251` - parser input section.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:285` - detail pane.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:308` - settings pane.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:376` - manage pane.
  - External: `https://dev.vivo.com.cn/documentCenter/doc/597` - vivo foldable design guideline; executor must open and record applied rules because automated fetch may return only the platform shell.
  - Repo: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/settings.gradle.kts:22` - project name is `ArchiveAssistant`.

  **Acceptance Criteria**:
  - [ ] `test -d AI-Design` succeeds.
  - [ ] `ls AI-Design` shows all seven required files.
  - [ ] Each design doc contains at least one reference to a prototype feature and one implementation acceptance check.

  **QA Scenarios**:
  ```
  Scenario: Documentation set exists
    Tool: Bash
    Steps: Run `ls AI-Design` and verify the seven exact markdown filenames.
    Expected: All seven filenames are present with non-zero file size.
    Evidence: .sisyphus/evidence/task-1-ai-design-docs.txt

  Scenario: Missing scope guard fails review
    Tool: Bash
    Steps: Search `AI-Design/*.md` for `Must NOT` and `no real AI API calls`.
    Expected: Guardrails are present in at least `00-overall-implementation-plan.md` and `05-ai-engine-settings-design.md`.
    Evidence: .sisyphus/evidence/task-1-ai-design-docs-error.txt
  ```

  **Commit**: YES | Message: `docs(design): add AI design implementation plan` | Files: `AI-Design/*.md`

- [x] 2. Add Domain Models, Sample Data, and Mock Classifier

  **What to do**: Create a domain package under `app/src/main/java/com/lyihub/archiveassistant/domain/`. Define immutable Kotlin data classes/enums: `Topic`, `KnowledgeItem`, `ContentType`, `AiEngineType`, `AiEngineSettings`, `AppPane`, and `CardRenderPayload` if needed. Add deterministic sample data matching prototype categories and content types. Add a pure Kotlin `MockKnowledgeClassifier` that accepts raw input text and returns a target topic plus generated `KnowledgeItem`; classification must be deterministic using simple keyword rules and fallback to a default topic.
  **Must NOT do**: Do not add network libraries. Do not use randomness or wall-clock time in tests except through injectable clock/default timestamp.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: focused Kotlin model and pure logic implementation.
  - Skills: [] - no special skill required.
  - Omitted: `frontend-ui-ux` - no UI work.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: Tasks 3, 5, 6, 7, 8, 9, 10 | Blocked By: none

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:295` - content tabs define content type labels.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:499` - friendly time behavior exists in prototype.
  - Build: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/build.gradle.kts:53` - JUnit dependency exists.
  - Test pattern: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/test/java/com/lyihub/archiveassistant/ExampleUnitTest.kt:13` - current host unit test structure.

  **Acceptance Criteria**:
  - [ ] Domain classes compile and are independent from Compose.
  - [ ] Unit tests verify keyword classification for web article, image/screenshot, document/PDF, and fallback text.
  - [ ] `./gradlew testDebugUnitTest` passes.

  **QA Scenarios**:
  ```
  Scenario: Classifier maps known content
    Tool: Bash
    Steps: Run `./gradlew testDebugUnitTest`.
    Expected: Tests for URL, image keyword, PDF keyword, and plain text fallback pass.
    Evidence: .sisyphus/evidence/task-2-domain-classifier.txt

  Scenario: Empty input is rejected
    Tool: Bash
    Steps: Run unit test for blank input.
    Expected: Classifier returns a validation error or null result defined in `03-data-and-state-model.md`; no crash.
    Evidence: .sisyphus/evidence/task-2-domain-classifier-error.txt
  ```

  **Commit**: YES | Message: `feat(domain): add knowledge models and mock classifier` | Files: `app/src/main/java/com/lyihub/archiveassistant/domain/**`, `app/src/test/**`

- [x] 3. Build App State Store and Entry Point

  **What to do**: Replace starter `Greeting` entry with `ArchiveAssistantApp`. Create `app/src/main/java/com/lyihub/archiveassistant/app/ArchiveAssistantApp.kt` and state holder package `state/`. Implement a single source of truth using Compose state: selected pane, selected topic id, topics list, items by topic, active detail filter, parser input, modal item, and AI settings. Wire actions: close panes, open topic, submit parser input, create topic, rename topic, delete topic, select filter, open/close card modal, update engine settings.
  **Must NOT do**: Do not use global mutable singleton state. Do not keep starter `Greeting` UI reachable.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: state wiring and entry-point replacement.
  - Skills: [] - no special skill required.
  - Omitted: `frontend-ui-ux` - UI polish comes in later tasks.

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: Tasks 4-10 | Blocked By: Task 2

  **References**:
  - Entry: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/main/java/com/lyihub/archiveassistant/MainActivity.kt:20` - current setContent entry.
  - Theme: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/main/java/com/lyihub/archiveassistant/ui/theme/Theme.kt:37` - existing theme wrapper.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:558` - pane state transitions.

  **Acceptance Criteria**:
  - [ ] App launches through `ArchiveAssistantApp` inside `ArchiveAssistantTheme`.
  - [ ] State actions are covered by unit tests where pure, or Compose UI tests where UI-bound.
  - [ ] `./gradlew testDebugUnitTest` passes.

  **QA Scenarios**:
  ```
  Scenario: App state opens and closes panes
    Tool: Bash
    Steps: Run state/action tests for open settings, open manage, open topic, close panes.
    Expected: Pane enum transitions match Home -> Settings -> Home -> Manage -> Detail -> Home.
    Evidence: .sisyphus/evidence/task-3-state-entry.txt

  Scenario: Deleting active topic clears detail state
    Tool: Bash
    Steps: Run unit test deleting selected topic.
    Expected: Selected topic id becomes null, pane returns Home, no stale feed is exposed.
    Evidence: .sisyphus/evidence/task-3-state-entry-error.txt
  ```

  **Commit**: YES | Message: `feat(app): wire archive assistant state` | Files: `MainActivity.kt`, `app/src/main/java/com/lyihub/archiveassistant/app/**`, `app/src/main/java/com/lyihub/archiveassistant/state/**`, tests

- [x] 4. Implement Design System and vivo Foldable Responsive Pane Shell

  **What to do**: Update Compose theme colors/typography to match prototype: parchment `#f5f4ed`, ivory `#faf9f5`, dark text `#141413`, muted `#5e5d59`, terracotta `#c96442`, coral `#d97757`, warm borders. Create UI primitives for header, icon button, primary button, tab row, pane container, empty state, and modal surface. Implement a vivo-guideline-aware foldable responsive shell: compact/folded width follows ordinary phone single-pane navigation; expanded/unfolded width shows master/detail side-by-side so the larger screen displays more useful information instead of simply scaling content; settings/manage appear as right-side overlays or full-height secondary panes depending on available width. Add AndroidX WindowManager or the current recommended Jetpack window layout dependency if needed to observe folding features; when a separating hinge/fold is reported, place the master pane on one side and detail/settings/manage on the other, with no primary controls underneath hinge bounds. Check `app/src/main/AndroidManifest.xml` and explicitly support resizable activity behavior so fold/unfold and resolution changes do not enter a black-bar compatibility mode. Add stable test tags: `home-pane`, `detail-pane`, `settings-pane`, `manage-pane`, `settings-trigger`, `layout-mode-compact`, `layout-mode-expanded`, `layout-mode-foldable`.
  **Must NOT do**: Do not use default purple palette. Do not create nested decorative cards or unrelated marketing hero content. Do not assume large width alone means hinge-safe layout when WindowManager reports a separating fold. Do not implement foldable support as whole-screen proportional zoom/stretch. Do not leave fold/unfold transitions in a compatibility mode with left/right or bottom black bars.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: Compose UI architecture and visual fidelity.
  - Skills: [`frontend-ui-ux`] - useful for design-quality guardrails.
  - Omitted: []

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: Tasks 5-8 | Blocked By: Task 3

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:12` - design tokens.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:44` - section explicitly labels responsive and foldable layout skeleton.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:47` - responsive layout skeleton.
  - External: `https://dev.vivo.com.cn/documentCenter/doc/597` - vivo foldable design guideline; verify directly during implementation and record applied rules in `AI-Design/04-ui-module-design.md`.
  - Current theme colors: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/main/java/com/lyihub/archiveassistant/ui/theme/Color.kt:5` - default purple values to replace.
  - Current theme wrapper: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/main/java/com/lyihub/archiveassistant/ui/theme/Theme.kt:53`.
  - Manifest: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/main/AndroidManifest.xml:14` - current `MainActivity` declaration; verify resizable/foldable behavior here.

  **Acceptance Criteria**:
  - [ ] App no longer uses default purple theme tokens.
  - [ ] Compact/folded, expanded/unfolded, and foldable-hinge-aware layouts expose correct pane and layout-mode test tags.
  - [ ] In expanded/unfolded mode, master and detail can be visible simultaneously after selecting a topic.
  - [ ] In compact/folded mode, selecting a topic navigates to a single visible detail pane with a back path.
  - [ ] If WindowManager reports a separating hinge/fold, primary content is not laid out underneath hinge bounds.
  - [ ] `AI-Design/04-ui-module-design.md` includes a `vivo foldable guideline decisions` section with the official URL and the rules applied.
  - [ ] App supports fold/unfold and resolution changes without compatibility black bars, and expanded mode shows more content rather than proportionally enlarged compact UI.
  - [ ] `./gradlew connectedDebugAndroidTest` passes when device/emulator is available.

  **QA Scenarios**:
  ```
  Scenario: Home shell renders in compact mode
    Tool: Bash
    Steps: Run Compose UI test with compact width that launches app and asserts `home-pane`, `layout-mode-compact`, title `聚合拾遗`, and `settings-trigger` exist.
    Expected: All nodes exist and are displayed; `detail-pane` is not simultaneously visible before topic selection.
    Evidence: .sisyphus/evidence/task-4-responsive-shell.txt

  Scenario: Foldable expanded mode keeps master and detail side by side
    Tool: Bash
    Steps: Run Compose UI test with expanded width or foldable test configuration, select a seeded topic.
    Expected: `home-pane`, `detail-pane`, and either `layout-mode-expanded` or `layout-mode-foldable` are visible; detail content does not overlap master content.
    Evidence: .sisyphus/evidence/task-4-foldable-expanded.txt

  Scenario: vivo foldable guideline checklist is documented
    Tool: Bash
    Steps: Verify `AI-Design/04-ui-module-design.md` contains `https://dev.vivo.com.cn/documentCenter/doc/597`, `可调整窗口`, `无黑边`, `非整体放大`, and `展开态展示更多内容` or exact equivalent wording from the official doc.
    Expected: Documentation records the vivo guideline rules and maps each rule to an implementation or test.
    Evidence: .sisyphus/evidence/task-4-vivo-guideline.txt

  Scenario: Settings overlay does not erase home state
    Tool: Bash
    Steps: Compose UI test enters parser text, opens settings, closes settings.
    Expected: Parser text remains unchanged after returning home.
    Evidence: .sisyphus/evidence/task-4-responsive-shell-error.txt
  ```

  **Commit**: YES | Message: `feat(ui): add foldable warm design shell` | Files: `ui/theme/**`, `ui/components/**`, `ui/screens/**`, android tests, build files if WindowManager dependency is added

- [x] 5. Implement Parser Home and AI Classification Flow

  **What to do**: Build home parser section with title/subtitle, multiline input placeholder `拖拽文件、输入链接、纯文本，或直接从剪切板粘贴...`, paste hint, and primary button labeled for AI classification. On submit, call `MockKnowledgeClassifier`, update topic list/feed, clear or retain input according to design doc: clear on success, retain on validation failure. Recent topics list shows top three by timestamp with topic icon, title, friendly time, and item count.
  **Must NOT do**: Do not implement actual file drag ingestion on Android beyond a documented placeholder action unless Android content picker is explicitly added later. Do not call network.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: user-facing Compose form and list behavior.
  - Skills: [`frontend-ui-ux`] - preserve prototype UX.
  - Omitted: []

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: Task 10 | Blocked By: Tasks 2, 3, 4

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:244` - home header text.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:251` - parser section.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:267` - recent topics header.
  - Classifier from Task 2.

  **Acceptance Criteria**:
  - [ ] Input accepts multiline text and URL strings.
  - [ ] Blank submit shows validation feedback and does not create item.
  - [ ] Successful submit creates/updates a topic and a feed item.
  - [ ] Recent topics list is sorted newest first and capped to three.

  **QA Scenarios**:
  ```
  Scenario: Text is classified into a topic
    Tool: Bash
    Steps: Compose UI test enters `这是一篇关于 AI 知识管理的网页文章 https://example.com`, taps AI classify button.
    Expected: Recent topics shows a topic with `1 项内容`, and parser input clears.
    Evidence: .sisyphus/evidence/task-5-parser-classify.txt

  Scenario: Blank input is rejected
    Tool: Bash
    Steps: Compose UI test taps AI classify button with empty input.
    Expected: No new topic appears; validation message is displayed.
    Evidence: .sisyphus/evidence/task-5-parser-classify-error.txt
  ```

  **Commit**: YES | Message: `feat(home): implement parser and recent topics` | Files: `ui/screens/home/**`, `state/**`, tests

- [x] 6. Implement Topic Management Pane

  **What to do**: Build manage pane matching prototype behavior: open from home button, list all topics with icon/title/meta, tap item opens detail, rename action opens name dialog, delete action requires confirmation, create action opens same name dialog in create mode. Ensure duplicate empty names are rejected; duplicate non-empty names get validation feedback instead of silent merge.
  **Must NOT do**: Do not delete topic content without confirmation. Do not allow blank topic names.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: management UI and dialogs.
  - Skills: [`frontend-ui-ux`] - interaction quality.
  - Omitted: []

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: Task 10 | Blocked By: Tasks 2, 3, 4

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:270` - create topic button.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:274` - manage entry.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:376` - manage pane.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:532` - manage list rendering.

  **Acceptance Criteria**:
  - [ ] Create, rename, delete topic actions are available from UI.
  - [ ] Delete confirmation protects accidental removal.
  - [ ] Empty and duplicate names show validation feedback.
  - [ ] Opening a managed topic navigates to detail pane.

  **QA Scenarios**:
  ```
  Scenario: Create and rename topic
    Tool: Bash
    Steps: Compose UI test opens manage pane, creates `阅读摘录`, renames it to `长期研究`.
    Expected: Manage list displays `长期研究` and no longer displays `阅读摘录`.
    Evidence: .sisyphus/evidence/task-6-topic-manage.txt

  Scenario: Delete requires confirmation
    Tool: Bash
    Steps: Compose UI test taps delete for a topic, dismisses confirmation, then confirms on second attempt.
    Expected: Topic remains after dismiss and disappears only after confirm.
    Evidence: .sisyphus/evidence/task-6-topic-manage-error.txt
  ```

  **Commit**: YES | Message: `feat(topics): add topic management pane` | Files: `ui/screens/manage/**`, `state/**`, tests

- [x] 7. Implement Detail Feed, Content Filters, and Card Modal

  **What to do**: Build detail pane with selected topic title, tabs `全部`, `网页文章`, `图像截屏`, `文档/PDF`, knowledge feed cards, empty states, and card detail modal. Cards must render different content styles by `ContentType`: text excerpt, URL/article metadata, screenshot/image placeholder with label, PDF/document metadata. Modal shows title/content and close action.
  **Must NOT do**: Do not fetch remote URL previews. Do not require actual image/PDF decoding in this pass.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: central reading UI and modal behavior.
  - Skills: [`frontend-ui-ux`] - visual fidelity and information hierarchy.
  - Omitted: []

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: Task 10 | Blocked By: Tasks 2, 3, 4

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:285` - detail pane.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:293` - detail tabs.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:302` - knowledge feed.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:220` - card modal styling/rendering.

  **Acceptance Criteria**:
  - [ ] Selecting a topic opens detail pane and displays its feed.
  - [ ] Tabs filter content by exact content type and `全部` resets filter.
  - [ ] Empty selected type shows an empty state, not stale data.
  - [ ] Tapping a card opens modal; closing modal returns to same topic/filter.

  **QA Scenarios**:
  ```
  Scenario: Filter feed by document type
    Tool: Bash
    Steps: Compose UI test opens a seeded topic, taps `文档/PDF`.
    Expected: Only document/PDF cards are displayed; web/image/text cards are hidden.
    Evidence: .sisyphus/evidence/task-7-detail-feed.txt

  Scenario: Modal preserves filter state
    Tool: Bash
    Steps: With `网页文章` filter active, open first card modal and close it.
    Expected: `网页文章` tab remains active and same filtered cards are visible.
    Evidence: .sisyphus/evidence/task-7-detail-feed-error.txt
  ```

  **Commit**: YES | Message: `feat(detail): add feed filters and card modal` | Files: `ui/screens/detail/**`, `ui/components/**`, tests

- [x] 8. Implement AI Engine Settings Pane

  **What to do**: Build settings pane with group label `AI 推理引擎配置`, engine type selector with cloud/local options, cloud fields for base URL, API key, model name, and local field for local service URL/model as defined in `05-ai-engine-settings-design.md`. Toggling engine type shows only relevant fields. API key display must be masked by default and never logged.
  **Must NOT do**: Do not validate by making network calls. Do not print settings values in logs or test output.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: settings form and conditional UI.
  - Skills: [`frontend-ui-ux`] - compact settings UX.
  - Omitted: []

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: Task 9, Task 10 | Blocked By: Tasks 2, 3, 4

  **References**:
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:233` - settings trigger.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:318` - AI settings group.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:330` - engine type select.
  - Prototype: `/Users/farest/Downloads/knowledge-curation-app-11.html:484` - cloud/local field toggle behavior.

  **Acceptance Criteria**:
  - [ ] Settings trigger opens settings pane.
  - [ ] Cloud/local selector changes visible fields.
  - [ ] API key value is masked in normal display.
  - [ ] Settings updates are reflected in app state.

  **QA Scenarios**:
  ```
  Scenario: Switch to local engine
    Tool: Bash
    Steps: Compose UI test opens settings and selects local engine.
    Expected: Cloud base URL/API key/model fields are hidden; local service fields are visible.
    Evidence: .sisyphus/evidence/task-8-ai-settings.txt

  Scenario: API key is masked
    Tool: Bash
    Steps: Compose UI test enters `sk-test-secret` and returns to settings display state.
    Expected: Raw string `sk-test-secret` is not visible in any displayed text node.
    Evidence: .sisyphus/evidence/task-8-ai-settings-error.txt
  ```

  **Commit**: YES | Message: `feat(settings): add AI engine configuration pane` | Files: `ui/screens/settings/**`, `state/**`, tests

- [x] 9. Add Local Persistence for App Data and Settings

  **What to do**: Add local persistence without introducing heavy architecture. Use Android DataStore Preferences for AI settings and either in-memory seeded knowledge data for this pass or JSON/DataStore serialization for topics/items if design doc chooses persistence for user-created items. Decision for this plan: persist AI settings; keep knowledge items in memory with seeded data because no storage dependency currently exists and real import/storage requirements are out of scope. Document this explicitly in `AI-Design/03-data-and-state-model.md` and `00-overall-implementation-plan.md`.
  **Must NOT do**: Do not add Room unless the scope changes. Do not persist raw API keys in plain logs; if persisted, keep local-only and masked in UI.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: dependency and settings persistence.
  - Skills: [] - no special skill required.
  - Omitted: `frontend-ui-ux` - UI already implemented.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: Task 10 | Blocked By: Tasks 2, 3, 8

  **References**:
  - Build dependencies: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/build.gradle.kts:44` - dependency block.
  - Settings model from Task 2.
  - Settings UI from Task 8.

  **Acceptance Criteria**:
  - [ ] DataStore Preferences dependency is added through version catalog or direct Gradle dependency following project style.
  - [ ] AI engine settings survive Activity recreation or app restart in instrumentation test where feasible.
  - [ ] Design docs explicitly state knowledge content persistence is deferred and why.
  - [ ] `./gradlew testDebugUnitTest` passes.

  **QA Scenarios**:
  ```
  Scenario: Settings persist locally
    Tool: Bash
    Steps: Instrumentation test updates engine type/model, recreates activity, reopens settings.
    Expected: Engine type/model values match saved values.
    Evidence: .sisyphus/evidence/task-9-settings-persistence.txt

  Scenario: No network persistence validation
    Tool: Bash
    Steps: Search implementation for HTTP client imports or network calls from settings save path.
    Expected: No network client use exists for settings validation.
    Evidence: .sisyphus/evidence/task-9-settings-persistence-error.txt
  ```

  **Commit**: YES | Message: `feat(data): persist AI engine settings locally` | Files: `app/build.gradle.kts`, `gradle/libs.versions.toml` if present, `data/**`, docs, tests

- [x] 10. Consolidate Tests, Accessibility Tags, and QA Evidence

  **What to do**: Add or tighten unit and Compose UI tests for all critical flows. Ensure every primary action has a stable test tag documented in `AI-Design/04-ui-module-design.md`: `parser-input`, `classify-button`, `recent-topic-list`, `topic-card-{id}`, `manage-button`, `create-topic-button`, `rename-topic-button`, `delete-topic-button`, `detail-tabs`, `card-modal`, `settings-trigger`, `engine-type-selector`. Run verification commands and save command outputs or summarized evidence under `.sisyphus/evidence/`.
  **Must NOT do**: Do not weaken tests to match broken behavior. Do not rely on timing sleeps where Compose test synchronization is available.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: cross-module QA consolidation.
  - Skills: [] - no special skill required.
  - Omitted: []

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: Final Verification | Blocked By: Tasks 1-9

  **References**:
  - Unit test pattern: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/test/java/com/lyihub/archiveassistant/ExampleUnitTest.kt:13`.
  - Instrumentation test pattern: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/src/androidTest/java/com/lyihub/archiveassistant/ExampleInstrumentedTest.kt:16`.
  - Compose test dependency: `/Users/farest/AndroidStudioProjects/ArchiveAssistant/app/build.gradle.kts:57`.

  **Acceptance Criteria**:
  - [ ] `./gradlew testDebugUnitTest` passes.
  - [ ] `./gradlew connectedDebugAndroidTest` passes or, if no device is available, failure is only environment-related and recorded with exact message.
  - [ ] `.sisyphus/evidence/` contains evidence files for Tasks 1-10.
  - [ ] `AI-Design/06-verification-and-qa-plan.md` lists exact commands and expected outputs.

  **QA Scenarios**:
  ```
  Scenario: Full local unit suite
    Tool: Bash
    Steps: Run `./gradlew testDebugUnitTest`.
    Expected: Build successful and all unit tests pass.
    Evidence: .sisyphus/evidence/task-10-test-consolidation.txt

  Scenario: UI suite unavailable environment is documented
    Tool: Bash
    Steps: Run `./gradlew connectedDebugAndroidTest`.
    Expected: Tests pass on device/emulator, or exact no-device environment error is captured without being misreported as product failure.
    Evidence: .sisyphus/evidence/task-10-test-consolidation-error.txt
  ```

  **Commit**: YES | Message: `test(app): cover knowledge curation flows` | Files: `app/src/test/**`, `app/src/androidTest/**`, `.sisyphus/evidence/**`, `AI-Design/06-verification-and-qa-plan.md`

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Code Quality Review — unspecified-high
- [x] F3. Real Manual QA — unspecified-high (+ Compose UI testing; Playwright not applicable to native Android)
- [x] F4. Scope Fidelity Check — deep

## Commit Strategy
- Commit documentation foundation first: `docs(design): add AI design implementation plan`.
- Commit domain/state/UI modules in task order with focused files only.
- Commit tests after related implementation tasks or in Task 10 consolidation.
- Do not commit secrets, local properties, emulator artifacts, or unrelated IDE files.

## Success Criteria
- The root `AI-Design/` folder exists and serves as the authoritative design/implementation reference.
- The app visibly matches the provided prototype's structure, labels, warm design language, foldable-oriented pane behavior, and core workflows.
- User workflows work end to end without network: classify input, browse topics, manage topics, read/filter cards, configure AI engine settings.
- All automated verification either passes or records environment-only blockers accurately.
