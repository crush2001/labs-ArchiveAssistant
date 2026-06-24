package com.lyihub.archiveassistant.domain

/**
 * Request for smart summarization.
 * Carries only original user/clipboard input — never model output.
 *
 * @property rawText  Original user or clipboard input (never from model output).
 * @property sourceUrl  Optional source URL for clipboard/shared content.
 * @property sourceTitle  Optional source title for contextual reference.
 */
data class SmartSummarizeRequest(
    val rawText: String,
    val sourceUrl: String? = null,
    val sourceTitle: String? = null,
)

/**
 * Result from smart summarization.
 */
sealed interface SmartSummarizeResult {
    /**
     * Successful summarization with fields parsed from AI JSON output.
     *
     * Required AI JSON fields: [topicId], [contentType], [tag], [title], [summary], [documentFormat].
     * Optional AI JSON field: [sourceUrl] (empty string maps to null).
     *
     * @param sourceUrl  Source URL from AI; an empty string is normalized to null.
     */
    data class Success(
        val topicId: String,
        val contentType: ContentType,
        val tag: String,
        val title: String,
        val summary: String,
        val documentFormat: DocumentFormat,
        val sourceUrl: String? = null,
    ) : SmartSummarizeResult {
        /**
         * Converts this result to a [ClassificationPayload].
         *
         * @param rawText  The original raw text from [SmartSummarizeRequest.rawText].
         *                 This is set as [ClassificationPayload.rawInput] to ensure
         *                 rawInput always comes from user input, never from model output.
         */
        fun toClassificationPayload(rawText: String): ClassificationPayload = ClassificationPayload(
            topicId = topicId,
            contentType = contentType,
            tag = tag,
            title = title,
            summary = summary,
            rawInput = rawText,
            documentFormat = documentFormat,
        )

        companion object {
            /**
             * Creates a [Success] from AI JSON output.
             * Normalizes [sourceUrl]: blank or empty string is converted to null.
             */
            fun fromAiJson(
                topicId: String,
                contentType: ContentType,
                tag: String,
                title: String,
                summary: String,
                documentFormat: DocumentFormat,
                sourceUrl: String? = null,
            ): Success = Success(
                topicId = topicId,
                contentType = contentType,
                tag = tag,
                title = title,
                summary = summary,
                documentFormat = documentFormat,
                sourceUrl = if (sourceUrl.isNullOrBlank()) null else sourceUrl,
            )
        }
    }

    /**
     * Failed summarization with a user-visible message and no partial payload.
     */
    data class Failure(val message: String) : SmartSummarizeResult
}

/**
 * Contract for smart summarization.
 *
 * Implementations produce a structured [SmartSummarizeResult] from user input
 * without persisting new models or modifying [KnowledgeItem] fields.
 */
interface SmartSummarizer {
    suspend fun summarize(
        request: SmartSummarizeRequest,
        topics: List<Topic>,
        existingItems: List<KnowledgeItem> = emptyList(),
    ): SmartSummarizeResult
}
