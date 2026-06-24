package com.lyihub.archiveassistant.domain

data class ItemSnippet(
    val itemId: String,
    val title: String,
    val snippet: String,
)

data class MaterialContext(
    val topicOptions: List<Topic>,
    val selectedSnippets: List<ItemSnippet>,
)

object MaterialContextSelector {

    private const val MAX_SNIPPETS = 8
    private const val MAX_SNIPPET_LENGTH = 500

    fun select(
        rawInput: String,
        topicOptions: List<Topic>,
        allItems: List<KnowledgeItem>,
    ): MaterialContext {
        val normalizedInput = rawInput.lowercase()
        val topicTitles = topicOptions.associate { it.id to it.title }

        val (related, fallback) = allItems.partition { item ->
            val topicTitle = topicTitles[item.topicId]?.lowercase() ?: ""
            val fields = listOf(
                topicTitle,
                item.title.lowercase(),
                item.tag.lowercase(),
                item.summary.lowercase(),
                item.fullText.lowercase(),
            ).filter { it.isNotBlank() }
            fields.any { field -> normalizedInput in field || field in normalizedInput }
        }

        val sortedRelated = related.sortedByDescending { it.createdAtEpochMillis }
        val sortedFallback = fallback.sortedByDescending { it.createdAtEpochMillis }

        val selected = (sortedRelated + sortedFallback)
            .distinctBy { it.id }
            .take(MAX_SNIPPETS)

        return MaterialContext(
            topicOptions = topicOptions,
            selectedSnippets = selected.map { it.toSnippet() },
        )
    }

    private fun KnowledgeItem.toSnippet(): ItemSnippet {
        val parts = listOfNotNull(
            summary.takeIf { it.isNotBlank() },
            fullText.takeIf { it.isNotBlank() },
        )
        val text = if (parts.isEmpty()) title else parts.joinToString(" | ")
        return ItemSnippet(
            itemId = id,
            title = title,
            snippet = text.take(MAX_SNIPPET_LENGTH),
        )
    }
}
