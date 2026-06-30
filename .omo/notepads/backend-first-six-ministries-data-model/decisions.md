## 2026-06-26 Task: context-gathering
- Follow plan rule: only six known ministry IDs map to themselves; all legacy/unknown topic IDs fallback to `treasury` in this phase.
- Keep `Topic`/`selectedTopicId` names where needed to avoid frontend churn.

## 2026-06-26 Background synthesis
- Do not implement repository split, item/topic repository extraction, or reactive Flow observation in this pass.
- Prefer compatibility APIs (`tryDecode*` or `loadSnapshot`) over changing existing `decodeTopics()` wrappers to throw.
- Disabled Topic CRUD should be deterministic and non-destructive: no topic count change, no item count change, no runtime exception.
- Frontend controls may be hidden/disabled only for Topic CRUD; theme/layout should remain unchanged.
