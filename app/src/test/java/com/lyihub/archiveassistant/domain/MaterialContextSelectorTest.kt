package com.lyihub.archiveassistant.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MaterialContextSelectorTest {

    private val topicAi = Topic(
        id = "topic-ai",
        title = "大模型架构研究",
        iconName = "folder-spark",
        iconColor = "#b85c38",
        updatedAtEpochMillis = 1_715_000_000_000,
    )
    private val topicUi = Topic(
        id = "topic-ui",
        title = "UX/UI 灵感板",
        iconName = "folder-spark",
        iconColor = "#3898ec",
        updatedAtEpochMillis = 1_714_000_000_000,
    )
    private val topicAnthropology = Topic(
        id = "topic-anthropology",
        title = "阅读剪报：人类学",
        iconName = "folder-bookmark",
        iconColor = "#5e5d59",
        updatedAtEpochMillis = 1_713_000_000_000,
    )
    private val allTopics = listOf(topicAi, topicUi, topicAnthropology)

    private val itemAiTitle = KnowledgeItem(
        id = "item-ai-title", topicId = "topic-ai",
        contentType = ContentType.WEB_ARTICLE, tag = "网页",
        title = "大模型架构研究的最新进展",
        summary = "架构扩展的最新研究摘要。",
        fullText = "Details about architecture scaling.",
        sourceUrl = null, createdAtEpochMillis = 1_715_100_000_000,
    )
    private val itemAiSummary = KnowledgeItem(
        id = "item-ai-summary", topicId = "topic-ai",
        contentType = ContentType.DOCUMENT, documentFormat = DocumentFormat.PDF,
        tag = "文档", title = "Attention 机制",
        summary = "大模型架构研究的核心注意力机制摘要。",
        fullText = "Attention 机制说明。",
        sourceUrl = null, createdAtEpochMillis = 1_715_086_400_000,
    )
    private val itemAiFullText = KnowledgeItem(
        id = "item-ai-fulltext", topicId = "topic-ai",
        contentType = ContentType.WEB_ARTICLE, tag = "网页",
        title = "Scaling Transformer 札记",
        summary = "关于大模型扩展规律的网页剪报。",
        fullText = "大模型架构研究的关键技术笔记和扩展规律",
        sourceUrl = "https://example.com/scaling",
        createdAtEpochMillis = 1_715_000_000_000,
    )
    private val itemAiTag = KnowledgeItem(
        id = "item-ai-tag", topicId = "topic-ai",
        contentType = ContentType.IMAGE_SCREENSHOT, tag = "大模型架构研究",
        title = "Transformer 全景图", summary = "编码器-解码器结构总览。",
        fullText = "", sourceUrl = null, createdAtEpochMillis = 1_715_200_000_000,
    )
    private val itemAiUnrelated = KnowledgeItem(
        id = "item-ai-unrelated", topicId = "topic-ai",
        contentType = ContentType.WEB_ARTICLE, tag = "网页",
        title = "LoRA 微调实践", summary = "低秩适配的微调方法。",
        fullText = "LoRA 在 Transformer 层上的应用实践笔记。",
        sourceUrl = null, createdAtEpochMillis = 1_715_300_000_000,
    )
    private val itemUiScreenshot = KnowledgeItem(
        id = "item-ui-screenshot", topicId = "topic-ui",
        contentType = ContentType.IMAGE_SCREENSHOT, tag = "图像",
        title = "设置页信息层级截图",
        summary = "卡片弹窗和详情筛选的界面参考。",
        fullText = "", sourceUrl = null, createdAtEpochMillis = 1_714_086_400_000,
    )
    private val itemAnthropologyNote = KnowledgeItem(
        id = "item-anthropology-note", topicId = "topic-anthropology",
        contentType = ContentType.DOCUMENT, documentFormat = DocumentFormat.PDF,
        tag = "文档", title = "田野笔记片段",
        summary = "阅读剪报中的短文本摘录。",
        fullText = "仪式、交换与地方知识之间的关系。",
        sourceUrl = null, createdAtEpochMillis = 1_713_086_400_000,
    )

    @Test
    fun `all topic options included in output`() {
        val context = MaterialContextSelector.select("anything", allTopics, emptyList())
        assertEquals(allTopics, context.topicOptions)
    }

    @Test
    fun `raw input matching item title selects it as related`() {
        val items = listOf(itemAiTitle, itemUiScreenshot)
        val context = MaterialContextSelector.select("大模型架构研究的最新进展", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-title", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `raw input matching item summary selects it as related`() {
        val items = listOf(itemAiSummary, itemUiScreenshot)
        val context = MaterialContextSelector.select("大模型架构研究的核心注意力机制摘要", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-summary", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `raw input matching item tag selects it as related`() {
        val items = listOf(itemAiTag, itemUiScreenshot)
        val context = MaterialContextSelector.select("大模型架构研究", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-tag", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `raw input matching item fullText selects it as related`() {
        val items = listOf(itemAiFullText, itemUiScreenshot)
        val context = MaterialContextSelector.select("大模型架构研究的关键技术笔记", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-fulltext", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `raw input matching topic title selects items from that topic`() {
        val items = listOf(itemAiSummary, itemAnthropologyNote)
        val context = MaterialContextSelector.select("大模型架构研究", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-summary", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `item field substring in raw input also matches bidirectionally`() {
        val items = listOf(itemAiTitle, itemUiScreenshot)
        val context = MaterialContextSelector.select("关于大模型架构研究的最新进展文档", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-title", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `related items from multiple topics when raw input matches both`() {
        val items = listOf(itemAiSummary, itemUiScreenshot, itemAnthropologyNote)
        val context = MaterialContextSelector.select("人类学", allTopics, items)
        assertEquals(3, context.selectedSnippets.size)
        assertEquals("item-anthropology-note", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `matching is case insensitive`() {
        val item = KnowledgeItem(
            id = "item-ci", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "research",
            title = "ai research paper", summary = "About AI research.",
            fullText = "", sourceUrl = null, createdAtEpochMillis = 2L,
        )
        val context = MaterialContextSelector.select("AI RESEARCH", allTopics, listOf(item))
        assertEquals(1, context.selectedSnippets.size)
        assertEquals("item-ci", context.selectedSnippets[0].itemId)
    }

    @Test
    fun `related items rank before unrelated fallback items`() {
        val items = listOf(itemAiUnrelated, itemAiSummary)
        val context = MaterialContextSelector.select("注意力机制", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertEquals("item-ai-summary", context.selectedSnippets[0].itemId)
        assertEquals("item-ai-unrelated", context.selectedSnippets[1].itemId)
    }

    @Test
    fun `fallback includes items from all topics`() {
        val items = listOf(itemUiScreenshot, itemAnthropologyNote)
        val context = MaterialContextSelector.select("xyzzy_nonexistent", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
    }

    @Test
    fun `fallback items sorted by recency descending`() {
        val old = KnowledgeItem(
            id = "old", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "其他",
            title = "Old", summary = "Old", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 100L,
        )
        val newer = KnowledgeItem(
            id = "newer", topicId = "topic-ui",
            contentType = ContentType.WEB_ARTICLE, tag = "其他",
            title = "Newer", summary = "Newer", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 200L,
        )
        val context = MaterialContextSelector.select("nope", allTopics, listOf(old, newer))
        assertEquals("newer", context.selectedSnippets[0].itemId)
        assertEquals("old", context.selectedSnippets[1].itemId)
    }

    @Test
    fun `related items sorted by recency descending among themselves`() {
        val old = KnowledgeItem(
            id = "old", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "架构",
            title = "Old", summary = "old", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 100L,
        )
        val newer = KnowledgeItem(
            id = "newer", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "架构",
            title = "Newer", summary = "newer", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 200L,
        )
        val context = MaterialContextSelector.select("架构", allTopics, listOf(old, newer))
        assertEquals("newer", context.selectedSnippets[0].itemId)
        assertEquals("old", context.selectedSnippets[1].itemId)
    }

    @Test
    fun `duplicate items removed by id`() {
        val items = listOf(itemAiSummary, itemAiUnrelated, itemAiSummary.copy())
        val context = MaterialContextSelector.select("注意力机制", allTopics, items)
        assertEquals(2, context.selectedSnippets.size)
        assertTrue(context.selectedSnippets.count { it.itemId == "item-ai-summary" } == 1)
    }

    @Test
    fun `caps at eight items`() {
        val items = (1..12).map { i ->
            KnowledgeItem(
                id = "item-$i", topicId = "topic-ai",
                contentType = ContentType.WEB_ARTICLE,
                tag = if (i <= 8) "matchme" else "其他",
                title = "Item $i", summary = "Summary $i",
                fullText = "Text $i.",
                sourceUrl = null, createdAtEpochMillis = 1_000_000_000L + i,
            )
        }
        val context = MaterialContextSelector.select("matchme", allTopics, items)
        assertEquals(8, context.selectedSnippets.size)
    }

    @Test
    fun `caps at eight with mixed related and fallback`() {
        val related = (1..6).map { i ->
            KnowledgeItem(
                id = "rel-$i", topicId = "topic-ai",
                contentType = ContentType.WEB_ARTICLE, tag = "mixed",
                title = "Related $i", summary = "Summary $i",
                fullText = "Text $i.",
                sourceUrl = null, createdAtEpochMillis = 1_000_000_000L + i,
            )
        }
        val fallback = (1..6).map { i ->
            KnowledgeItem(
                id = "fall-$i", topicId = "topic-ai",
                contentType = ContentType.WEB_ARTICLE, tag = "其他",
                title = "Fallback $i", summary = "Fallback $i.",
                fullText = "Text $i.",
                sourceUrl = null, createdAtEpochMillis = 2_000_000_000L + i,
            )
        }
        val context = MaterialContextSelector.select("mixed", allTopics, related + fallback)
        assertEquals(8, context.selectedSnippets.size)
        (0..5).forEach { assertTrue(context.selectedSnippets[it].itemId.startsWith("rel-")) }
        assertTrue(context.selectedSnippets[6].itemId.startsWith("fall-"))
        assertTrue(context.selectedSnippets[7].itemId.startsWith("fall-"))
    }

    @Test
    fun `snippet truncated to 500 characters`() {
        val longText = "A".repeat(1000)
        val item = KnowledgeItem(
            id = "item-long", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "架构",
            title = "长文本", summary = longText, fullText = "",
            sourceUrl = null, createdAtEpochMillis = 1L,
        )
        val context = MaterialContextSelector.select("架构", allTopics, listOf(item))
        assertEquals(500, context.selectedSnippets[0].snippet.length)
    }

    @Test
    fun `short snippet not truncated`() {
        val item = KnowledgeItem(
            id = "item-short", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "架构",
            title = "短文本", summary = "Short text.", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 1L,
        )
        val context = MaterialContextSelector.select("架构", allTopics, listOf(item))
        assertEquals("Short text.", context.selectedSnippets[0].snippet)
    }

    @Test
    fun `empty items returns topic context with no snippets`() {
        val context = MaterialContextSelector.select("anything", allTopics, emptyList())
        assertEquals(allTopics, context.topicOptions)
        assertTrue(context.selectedSnippets.isEmpty())
    }

    @Test
    fun `no matching items still returns topic context with fallback snippets`() {
        val context = MaterialContextSelector.select("nope", allTopics, listOf(itemAiSummary))
        assertEquals(allTopics, context.topicOptions)
        assertEquals(1, context.selectedSnippets.size)
    }

    @Test
    fun `blank summary and fullText uses title as snippet`() {
        val item = KnowledgeItem(
            id = "item-blank", topicId = "topic-ai",
            contentType = ContentType.IMAGE_SCREENSHOT, tag = "架构",
            title = "架构示意图", summary = "", fullText = "",
            sourceUrl = null, createdAtEpochMillis = 1L,
        )
        val context = MaterialContextSelector.select("架构", allTopics, listOf(item))
        assertEquals("架构示意图", context.selectedSnippets[0].snippet)
    }

    @Test
    fun `snippet composes summary and fullText with separator`() {
        val item = KnowledgeItem(
            id = "item-compose", topicId = "topic-ai",
            contentType = ContentType.WEB_ARTICLE, tag = "架构",
            title = "组合测试", summary = "Summary text.",
            fullText = "Full text content.",
            sourceUrl = null, createdAtEpochMillis = 1L,
        )
        val context = MaterialContextSelector.select("架构", allTopics, listOf(item))
        assertEquals("Summary text. | Full text content.", context.selectedSnippets[0].snippet)
    }
}
