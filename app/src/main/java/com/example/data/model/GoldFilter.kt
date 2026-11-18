package com.example.data.model

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

enum class GoldFilter(val displayName: String) {
    NATURAL("Natural"),
    GOLD_24K("24K Warmth"),
    CHAMPAGNE_MATTE("Champagne"),
    OBSIDIAN_NOIR("Obsidian"),
    AMBER_GLOW("Amber Glow");

    fun toColorMatrix(): ColorMatrix? {
        return when (this) {
            NATURAL -> null
            GOLD_24K -> ColorMatrix(
                floatArrayOf(
                    1.12f, 0.05f, 0f, 0f, 10f,
                    0.02f, 1.04f, 0f, 0f, 4f,
                    0f, 0f, 0.84f, 0f, -6f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            CHAMPAGNE_MATTE -> ColorMatrix(
                floatArrayOf(
                    0.96f, 0.08f, 0.04f, 0f, 14f,
                    0.04f, 0.94f, 0.04f, 0f, 10f,
                    0.02f, 0.04f, 0.88f, 0f, 6f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            OBSIDIAN_NOIR -> ColorMatrix().apply {
                setToSaturation(0.18f)
            }
            AMBER_GLOW -> ColorMatrix(
                floatArrayOf(
                    1.18f, 0.08f, 0f, 0f, 14f,
                    0.04f, 1.00f, 0f, 0f, 2f,
                    0f, 0f, 0.72f, 0f, -12f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
    }

    fun toColorFilter(): ColorFilter? = toColorMatrix()?.let { ColorFilter.colorMatrix(it) }

    companion object {
        fun fromName(raw: String): GoldFilter =
            entries.firstOrNull { it.name == raw } ?: NATURAL
    }
}
