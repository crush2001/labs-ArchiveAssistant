package com.lyihub.archiveassistant.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.BasicTextField
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.lyihub.archiveassistant.R
import com.lyihub.archiveassistant.domain.KnowledgeItem
import com.lyihub.archiveassistant.domain.Topic
import com.lyihub.archiveassistant.ui.theme.ImperialBronze
import com.lyihub.archiveassistant.ui.theme.ImperialCinnabar
import com.lyihub.archiveassistant.ui.theme.ImperialIvory
import com.lyihub.archiveassistant.ui.theme.ImperialLightGold
import com.lyihub.archiveassistant.ui.theme.ImperialParchment
import com.lyihub.archiveassistant.ui.theme.ImperialUmber

private val HomePanel = ImperialParchment
private val HomeBackground = ImperialIvory
private val HomeDeepInk = ImperialUmber
private val HomeGold = ImperialUmber
private val HomeGoldBlock = ImperialLightGold
private val HomeInk = ImperialUmber
private val HomePaper = ImperialIvory

private val MinistryTicketShape = GenericShape { size, _ ->
    val cut = size.height * 0.16f
    moveTo(cut, 0f)
    lineTo(size.width - cut, 0f)
    lineTo(size.width, cut)
    lineTo(size.width, size.height - cut)
    lineTo(size.width - cut, size.height)
    lineTo(cut, size.height)
    lineTo(0f, size.height - cut)
    lineTo(0f, cut)
    close()
}

private val FolderFallbackTitles = listOf(
    "大模型架构研究",
    "UX/UI 灵感板",
    "阅读剪报",
    "旅行参考",
    "开源工具",
    "待归档",
)

private data class DashboardFolder(
    val id: String,
    val title: String,
    val itemCount: Int,
    val updatedAtEpochMillis: Long?,
    val topic: Topic?,
)

@Composable
fun HomePane(
    title: String,
    parserValidationMessage: String?,
    recentTopics: List<Topic>,
    itemsByTopic: Map<String, List<KnowledgeItem>>,
    searchQuery: String,
    onTopicSelected: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenManage: () -> Unit,
    onCreateTopic: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onOpenClipboard: () -> Unit,
    onOpenMemorialDemo: (() -> Unit)? = null,
    smartSummarizationMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val pendingCount = pendingCount(recentTopics, itemsByTopic)
    val folders = dashboardFolders(recentTopics, itemsByTopic, searchQuery)

    Box(
        modifier = modifier
            .testTag("home-pane")
            .fillMaxSize()
            .background(HomeBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HomeContentColumn(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 36.dp,
                        end = 16.dp,
                        bottom = 28.dp,
                    )
                    .fillMaxWidth(),
            ) {
                HomeMosaic(
                    appTitle = title,
                    pendingCount = pendingCount,
                    folders = folders,
                    validationMessage = parserValidationMessage,
                    smartSummarizationMessage = smartSummarizationMessage,
                    searchQuery = searchQuery,
                    onOpenClipboard = onOpenClipboard,
                    onOpenMemorialDemo = onOpenMemorialDemo,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onTopicSelected = onTopicSelected,
                    onOpenSettings = onOpenSettings,
                    onOpenManage = onOpenManage,
                    onCreateTopic = onCreateTopic,
                )
            }
        }
    }
}

@Composable
private fun HomeContentColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun ColumnScope.HomeMosaic(
    appTitle: String,
    pendingCount: Int,
    folders: List<DashboardFolder>,
    validationMessage: String?,
    smartSummarizationMessage: String?,
    searchQuery: String,
    onOpenClipboard: () -> Unit,
    onOpenMemorialDemo: (() -> Unit)?,
    onSearchQueryChanged: (String) -> Unit,
    onTopicSelected: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenManage: () -> Unit,
    onCreateTopic: () -> Unit,
) {
    HomeHeaderRow(
        appTitle = appTitle,
        onOpenSettings = onOpenSettings,
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp),
    )
    PrimaryActionRow(
        pendingCount = pendingCount,
        onOpenClipboard = onOpenClipboard,
        onOpenMemorialDemo = onOpenMemorialDemo,
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
    )
    SearchCell(
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        validationMessage = validationMessage,
        smartSummarizationMessage = smartSummarizationMessage,
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp),
    )
    WorkflowRow(modifier = Modifier.height(72.dp))
    MinistryControlRow(
        searchQuery = searchQuery,
        resultCount = folders.count { it.topic != null },
        onCreateTopic = onCreateTopic,
        onOpenManage = onOpenManage,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
    )
    FolderResultList(
        folders = folders,
        searchQuery = searchQuery,
        onTopicSelected = onTopicSelected,
        compact = true,
    )
}

@Composable
private fun HomeHeaderRow(
    appTitle: String,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TitleCell(
        appTitle = appTitle,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}

@Composable
private fun TitleCell(
    appTitle: String,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlainCell(
        modifier = modifier.fillMaxSize(),
        color = HomePanel,
        contentColor = HomeGold,
    ) {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .testTag("settings-trigger"),
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置",
                tint = HomeGold.copy(alpha = 0.86f),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 22.dp, end = 72.dp),
        ) {
            Text(
                text = appTitle,
                style = MaterialTheme.typography.displayLarge,
                color = HomeGold,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
            Text(
                text = "中书录入 · 批奏折 · 尚书归档",
                style = MaterialTheme.typography.bodyMedium,
                color = ImperialUmber.copy(alpha = 0.72f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PalaceActionCell(
    title: String,
    subtitle: String,
    label: String,
    color: Color,
    contentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PlainCell(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        color = if (enabled) color else ImperialParchment,
        contentColor = contentColor,
    ) {
        DecorativePlaceholder(
            imageRes = if (label == "中书") {
                R.drawable.imperial_ornament_lantern
            } else {
                R.drawable.imperial_ornament_ruyi
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(46.dp),
            alpha = 0.22f,
            tint = contentColor,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.76f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PrimaryActionRow(
    pendingCount: Int,
    onOpenClipboard: () -> Unit,
    onOpenMemorialDemo: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PalaceActionCell(
            title = "宣拾遗",
            subtitle = "读取剪切板",
            label = "中书",
            color = HomeGoldBlock,
            contentColor = HomeDeepInk,
            modifier = Modifier.weight(1f),
            onClick = onOpenClipboard,
            testTag = "clipboard-button",
        )
        MemorialCell(
            pendingCount = pendingCount,
            onClick = onOpenMemorialDemo,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchCell(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    validationMessage: String?,
    smartSummarizationMessage: String?,
    modifier: Modifier = Modifier,
) {
    PlainCell(
        modifier = modifier.fillMaxSize(),
        color = HomePaper,
        contentColor = HomeInk,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "藏经阁",
                    style = MaterialTheme.typography.titleLarge,
                    color = HomeInk,
                    fontWeight = FontWeight.Normal,
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = HomeInk.copy(alpha = 0.68f),
                )
            }
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = HomeInk),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home-search-input"),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ImperialIvory.copy(alpha = 0.74f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        if (searchQuery.isBlank()) {
                            Text(
                                text = "查找主题或资料...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = HomeInk.copy(alpha = 0.44f),
                            )
                        }
                        innerTextField()
                    }
                },
            )
            val message = validationMessage ?: smartSummarizationMessage
            Text(
                text = message ?: if (searchQuery.isBlank()) {
                    "输入后筛选下方文件夹与资料"
                } else {
                    "正在筛选相关文件夹"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (message == null) HomeInk.copy(alpha = 0.58f) else ImperialCinnabar,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MemorialCell(
    pendingCount: Int,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    PlainCell(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .testTag("memorial-entry-card"),
        color = HomeGoldBlock,
        contentColor = HomeDeepInk,
    ) {
        DecorativePlaceholder(
            imageRes = R.drawable.imperial_ornament_gourd,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(60.dp),
            alpha = 0.18f,
            tint = HomeDeepInk,
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = "批奏折",
                style = MaterialTheme.typography.headlineSmall,
                color = HomeDeepInk,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
            Text(
                text = "今日 $pendingCount 封待批奏章",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeDeepInk.copy(alpha = 0.72f),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun WorkflowRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        WorkflowCell("中书", "拾取、摘要、拟题", Modifier.weight(1f))
        WorkflowCell("门下", "筛选、递奏、待批", Modifier.weight(1f))
    }
}

@Composable
private fun WorkflowCell(title: String, subtitle: String, modifier: Modifier = Modifier) {
    PlainCell(
        modifier = modifier.fillMaxSize(),
        color = HomePanel,
        contentColor = HomeGold,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = HomeGold,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ImperialUmber.copy(alpha = 0.62f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MinistryControlRow(
    searchQuery: String,
    resultCount: Int,
    onCreateTopic: () -> Unit,
    onOpenManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MinistryActionButton(
            title = "尚书省",
            subtitle = if (searchQuery.isBlank()) "六个固定文件夹" else "检出 $resultCount 类",
            onClick = {},
            testTag = "ministry-header-cell",
            modifier = Modifier.weight(1f),
            enabled = false,
            highlight = true,
        )
        MinistryActionButton(
            title = "新建",
            subtitle = "新立一夹",
            onClick = onCreateTopic,
            testTag = "home-create-topic-button",
            modifier = Modifier.weight(1f),
        )
        MinistryActionButton(
            title = "管理",
            subtitle = "整理六类",
            onClick = onOpenManage,
            testTag = "manage-button",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MinistryActionButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlight: Boolean = false,
) {
    PlainCell(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        color = if (highlight) HomeDeepInk else ImperialLightGold,
        contentColor = if (highlight) ImperialIvory else HomeInk,
    ) {
        val textColor = if (highlight) ImperialIvory else HomeInk
        val secondaryColor = textColor.copy(alpha = if (highlight) 0.72f else 0.68f)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = secondaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ColumnScope.FolderResultList(
    folders: List<DashboardFolder>,
    searchQuery: String,
    onTopicSelected: (String) -> Unit,
    compact: Boolean,
) {
    if (folders.isEmpty()) {
        PlainCell(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 96.dp else 112.dp),
            color = HomePaper,
            contentColor = HomeInk,
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "未找到相关文件夹",
                    style = MaterialTheme.typography.titleLarge,
                    color = HomeInk,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = "藏经阁暂未检出「$searchQuery」相关内容",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HomeInk.copy(alpha = 0.68f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        return
    }
    folders.forEachIndexed { index, folder ->
        MinistryTicketCard(
            folder = folder,
            visual = folderVisual(index),
            onTopicSelected = onTopicSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 104.dp else 118.dp),
            compact = compact,
        )
    }
}

@Composable
private fun MinistryTicketCard(
    folder: DashboardFolder,
    visual: FolderVisual,
    onTopicSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    val enabled = folder.topic != null
    val bodyColor = visual.background
    val imageSize = if (compact) 58.dp else 88.dp
    val titleStyle = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge
    val summaryStyle = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    Box(
        modifier = modifier
            .padding(vertical = 3.dp)
            .clip(MinistryTicketShape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        bodyColor,
                        bodyColor.copy(alpha = 0.94f),
                        ImperialIvory.copy(alpha = 0.92f),
                    ),
                ),
                MinistryTicketShape,
            )
            .border(1.dp, ImperialBronze.copy(alpha = 0.62f), MinistryTicketShape)
            .clickable(enabled = enabled) { folder.topic?.let { onTopicSelected(it.id) } }
            .testTag("topic-card-${folder.id}"),
    ) {
        Image(
            painter = painterResource(id = R.drawable.memorial_xuan_paper),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.22f,
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            visual.accent.copy(alpha = 0.08f),
                            visual.accent.copy(alpha = 0.16f),
                        ),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, top = 9.dp, end = 10.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 5.dp),
            ) {
                Text(
                    text = folder.title,
                    style = titleStyle,
                    color = HomeInk,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                )
                Text(
                    text = visual.description,
                    style = summaryStyle,
                    color = HomeInk.copy(alpha = 0.84f),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(id = visual.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(imageSize),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = folder.updatedAtEpochMillis?.let { "${folder.itemCount} 篇 · ${friendlyTime(it)}" }
                        ?: "${folder.itemCount} 篇 · 待启用",
                    style = MaterialTheme.typography.labelSmall,
                    color = visual.accent.copy(alpha = 0.84f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun PlainCell(
    modifier: Modifier,
    color: Color,
    contentColor: Color,
    content: @Composable BoxScopeWithContentColor.() -> Unit,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color, shape)
            .border(1.dp, ImperialBronze.copy(alpha = 0.34f), shape),
    ) {
        BoxScopeWithContentColor(this, contentColor).content()
    }
}

private class BoxScopeWithContentColor(
    private val boxScope: androidx.compose.foundation.layout.BoxScope,
    val contentColor: Color,
) : androidx.compose.foundation.layout.BoxScope by boxScope

@Composable
private fun DecorativePlaceholder(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    tint: Color? = null,
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alpha = alpha,
        colorFilter = tint?.let { ColorFilter.tint(it) },
    )
}

private fun pendingCount(
    topics: List<Topic>,
    itemsByTopic: Map<String, List<KnowledgeItem>>,
): Int = topics.take(3).sumOf { topic ->
    ((itemsByTopic[topic.id]?.size ?: 0) + topic.title.length) % 3
}

private fun dashboardFolders(
    topics: List<Topic>,
    itemsByTopic: Map<String, List<KnowledgeItem>>,
    searchQuery: String = "",
): List<DashboardFolder> {
    if (searchQuery.isNotBlank()) {
        return topics.map { topic ->
            DashboardFolder(
                id = topic.id,
                title = topic.title,
                itemCount = itemsByTopic[topic.id]?.size ?: 0,
                updatedAtEpochMillis = topic.updatedAtEpochMillis,
                topic = topic,
            )
        }
    }
    return List(6) { index ->
        val topic = topics.getOrNull(index)
        DashboardFolder(
            id = topic?.id ?: "dashboard-folder-${index + 1}",
            title = topic?.title ?: FolderFallbackTitles[index],
            itemCount = topic?.let { itemsByTopic[it.id]?.size ?: 0 } ?: 0,
            updatedAtEpochMillis = topic?.updatedAtEpochMillis,
            topic = topic,
        )
    }
}

internal fun friendlyTime(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val diff = nowMillis - epochMillis
    return when {
        diff < 0 -> "未来"
        diff < 60_000 -> "刚刚"
        diff < 3_600_000 -> "${diff / 60_000} 分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000} 小时前"
        diff < 2_592_000_000L -> "${diff / 86_400_000} 天前"
        else -> "很久以前"
    }
}

internal fun buildHighlightedText(
    text: String,
    query: String,
    highlightColor: Color,
    highlightBgColor: Color,
): androidx.compose.ui.text.AnnotatedString {
    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()
    return buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val matchIndex = lowerText.indexOf(lowerQuery, start)
            if (matchIndex < 0) {
                append(text.substring(start))
                break
            }
            if (matchIndex > start) {
                append(text.substring(start, matchIndex))
            }
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = highlightBgColor,
                    color = highlightColor,
                )
            ) {
                append(text.substring(matchIndex, matchIndex + query.length))
            }
            start = matchIndex + query.length
        }
    }
}
