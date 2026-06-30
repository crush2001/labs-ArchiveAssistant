## 2026-06-26 Task: context-gathering
- Need implementation agents to avoid frontend visual redesign; UI scope is limited to disabled/hidden Topic CRUD affordances.
- Need persistence agents to prove corrupt `app_topics_json` does not trigger automatic save of empty topics/items.

## 2026-06-26 Background findings
- Risk: Adding a broad `topicCrudEnabled` feature flag through UI layers may create more frontend churn than needed; prefer backend guards first, then minimal button hiding if tests/UI require it.
- Risk: Repository splitting / reactive Flow refactor is scope creep for this phase.
- Risk: If `decodeTopics()` remains `emptyList()` only, implementation may falsely treat corrupt JSON as valid empty state.
