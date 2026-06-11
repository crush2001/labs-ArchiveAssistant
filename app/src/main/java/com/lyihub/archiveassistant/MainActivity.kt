package com.lyihub.archiveassistant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.datastore.preferences.preferencesDataStore
import com.lyihub.archiveassistant.app.ArchiveAssistantApp
import com.lyihub.archiveassistant.data.AiEngineSettingsRepository
import com.lyihub.archiveassistant.ui.theme.ArchiveAssistantTheme

private val Context.aiEngineSettingsDataStore by preferencesDataStore(name = "ai_engine_settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val aiSettingsRepository = remember {
                AiEngineSettingsRepository(aiEngineSettingsDataStore)
            }
            ArchiveAssistantTheme {
                ArchiveAssistantApp(aiSettingsRepository = aiSettingsRepository)
            }
        }
    }
}
