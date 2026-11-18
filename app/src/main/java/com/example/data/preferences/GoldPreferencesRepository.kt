package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.GoldPalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.goldDataStore: DataStore<Preferences> by preferencesDataStore(name = "instagold_studio_prefs")

data class GoldStudioSettings(
    val palette: GoldPalette = GoldPalette.GOLD_24K,
    val ghostModeEnabled: Boolean = false,
    val hdSaveEnabled: Boolean = true,
    val goldFrameOverlay: Boolean = true
)

class GoldPreferencesRepository(private val context: Context) {
    private object Keys {
        val PALETTE = stringPreferencesKey("gold_palette")
        val GHOST_MODE = booleanPreferencesKey("ghost_mode")
        val HD_SAVE = booleanPreferencesKey("hd_save")
        val GOLD_FRAME = booleanPreferencesKey("gold_frame")
    }

    val settingsFlow: Flow<GoldStudioSettings> = context.goldDataStore.data.map { prefs ->
        val paletteRaw = prefs[Keys.PALETTE] ?: GoldPalette.GOLD_24K.name
        val palette = GoldPalette.entries.firstOrNull { it.name == paletteRaw } ?: GoldPalette.GOLD_24K
        GoldStudioSettings(
            palette = palette,
            ghostModeEnabled = prefs[Keys.GHOST_MODE] ?: false,
            hdSaveEnabled = prefs[Keys.HD_SAVE] ?: true,
            goldFrameOverlay = prefs[Keys.GOLD_FRAME] ?: true
        )
    }

    suspend fun setPalette(palette: GoldPalette) {
        context.goldDataStore.edit { it[Keys.PALETTE] = palette.name }
    }

    suspend fun setGhostMode(enabled: Boolean) {
        context.goldDataStore.edit { it[Keys.GHOST_MODE] = enabled }
    }

    suspend fun setHdSave(enabled: Boolean) {
        context.goldDataStore.edit { it[Keys.HD_SAVE] = enabled }
    }

    suspend fun setGoldFrameOverlay(enabled: Boolean) {
        context.goldDataStore.edit { it[Keys.GOLD_FRAME] = enabled }
    }
}
