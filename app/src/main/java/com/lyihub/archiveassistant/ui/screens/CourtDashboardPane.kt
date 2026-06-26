package com.lyihub.archiveassistant.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lyihub.archiveassistant.domain.KnowledgeItem
import com.lyihub.archiveassistant.domain.MinistryProfile
import com.lyihub.archiveassistant.domain.SixMinistryCatalog
import com.lyihub.archiveassistant.domain.Topic
import com.lyihub.archiveassistant.ui.components.PaneContainer
import com.lyihub.archiveassistant.ui.components.PaneContentPadding
import com.lyihub.archiveassistant.ui.components.PaneDivider
import com.lyihub.archiveassistant.ui.components.PaneHeader

@Composable
fun CourtDashboardPane(
    topics: List<Topic>,
    itemsByTopic: Map<String, List<KnowledgeItem>>,
    modifier: Modifier = Modifier,
) {
    val totalItems = itemsByTopic.values.sumOf { it.size }
    val pendingReview = demoPendingReviewCount(itemsByTopic)
    val todayItems = demoTodayCount(itemsByTopic)

    PaneContainer(modifier = modifier.testTag("court-dashboard-pane")) {
        PaneHeader(
            title = "朝堂视角",
            subtitle = "六部总览 / 门下待审 / 跨部关联",
        )
        PaneDivider()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                PaneContentPadding {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        DashboardHero(
                            totalItems = totalItems,
                            pendingReview = pendingReview,
                            todayItems = todayItems,
                        )
                        MinistryDashboardGrid(
                            topics = topics,
                            itemsByTopic = itemsByTopic,
                        )
                        CrossMinistryInsight()
                        PendingMemorialBrief()
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHero(
    totalItems: Int,
    pendingReview: Int,
    todayItems: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color(0xFFFBF4E4),
        border = BorderStroke(1.dp, Color(0xFFD2AF65).copy(alpha = 0.42f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "今日奏报",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4D3416),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "一屏掌握信息流入、待审与沉淀",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF755A30),
                    )
                }
                CourtStatusBadge(label = "待批", value = "6")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricTile(label = "今日入库", value = todayItems.toString(), modifier = Modifier.weight(1f))
                MetricTile(label = "门下待审", value = pendingReview.toString(), modifier = Modifier.weight(1f))
                MetricTile(label = "六部案牍", value = totalItems.toString(), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CourtStatusBadge(
    label: String,
    value: String,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFF9B2F28).copy(alpha = 0.09f),
        border = BorderStroke(1.dp, Color(0xFF9B2F28).copy(alpha = 0.24f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF7D2C24),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF7D2C24),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.White.copy(alpha = 0.58f),
        border = BorderStroke(1.dp, Color(0xFFD2AF65).copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF6E4D18),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF735D3B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MinistryDashboardGrid(
    topics: List<Topic>,
    itemsByTopic: Map<String, List<KnowledgeItem>>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(title = "尚书省 · 六部仪表盘")
        SixMinistryCatalog.ministries.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { ministry ->
                    MinistryMeterCard(
                        ministry = ministry,
                        topic = topics.firstOrNull { it.id == ministry.topicId },
                        itemCount = itemsByTopic[ministry.topicId]?.size ?: 0,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MinistryMeterCard(
    ministry: MinistryProfile,
    topic: Topic?,
    itemCount: Int,
    modifier: Modifier = Modifier,
) {
    val accent = parseColor(ministry.colorHex)
    val todayDelta = demoTodayDelta(ministry, itemCount)
    val pending = demoPendingFor(ministry)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = accent.copy(alpha = 0.065f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ministry.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                    Text(
                        text = ministry.domain,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                MinistryMiniSeal(text = ministry.name.take(1), color = accent)
            }
            Text(
                text = ministry.responsibility,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = itemCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = topic?.let { "总归档" } ?: "待建档",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Text(
                        text = "+$todayDelta 今日",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF7A5518),
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "$pending 待审",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CrossMinistryInsight() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(title = "门下省 · 跨部关联")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    InsightNode(label = "工部", title = "大模型技术突破", color = Color(0xFF2F7D72))
                    Text(
                        text = "同源趋势",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9A6F1D),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    InsightNode(label = "礼部", title = "AI 教育政策", color = Color(0xFFB66A45))
                }
                Text(
                    text = "AI 识别到两条内容都指向“端侧模型进入教育场景”的同一趋势，可合并追踪。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InsightNode(
    label: String,
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.26f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PendingMemorialBrief() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(title = "御前 · 待批奏章")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = Color(0xFFFFF8E9),
            border = BorderStroke(1.dp, Color(0xFFD7B56D).copy(alpha = 0.38f)),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(Color(0xFF9B2F28).copy(alpha = 0.09f))
                        .border(1.dp, Color(0xFF9B2F28).copy(alpha = 0.22f), MaterialTheme.shapes.large),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "6",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF7D2C24),
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "六封待批",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4D3416),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "右滑准入库，左滑驳回，上滑留中三日后再议。",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF755A30),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun MinistryMiniSeal(
    text: String,
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(MaterialTheme.shapes.small)
            .background(color.copy(alpha = 0.11f))
            .border(1.dp, color.copy(alpha = 0.34f), MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun demoTodayCount(itemsByTopic: Map<String, List<KnowledgeItem>>): Int =
    SixMinistryCatalog.ministries.sumOf { ministry ->
        demoTodayDelta(ministry, itemsByTopic[ministry.topicId]?.size ?: 0)
    }

private fun demoPendingReviewCount(itemsByTopic: Map<String, List<KnowledgeItem>>): Int =
    SixMinistryCatalog.ministries.sumOf { ministry ->
        demoPendingFor(ministry).coerceAtMost((itemsByTopic[ministry.topicId]?.size ?: 0) + 1)
    }

private fun demoTodayDelta(ministry: MinistryProfile, itemCount: Int): Int =
    ((ministry.name.first().code + itemCount) % 3) + 1

private fun demoPendingFor(ministry: MinistryProfile): Int =
    when (ministry.topicId) {
        SixMinistryCatalog.XingTopicId -> 3
        SixMinistryCatalog.GongTopicId -> 2
        SixMinistryCatalog.LiRitesTopicId -> 1
        else -> 0
    }

private fun parseColor(value: String): Color = try {
    Color(android.graphics.Color.parseColor(value))
} catch (_: Exception) {
    Color(0xFF8A6421)
}
