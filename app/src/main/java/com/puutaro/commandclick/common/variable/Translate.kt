package com.puutaro.commandclick.common.variable

import me.bush.translator.Language
import java.util.Locale

object Translate {
    val languageMap = mapOf<String, Language>(
        "ja" to Language.JAPANESE,
        "en" to Language.ENGLISH,
        "zh" to Language.CHINESE_TRADITIONAL,
        "es" to Language.SPANISH,
        "ko" to Language.KOREAN
    )

    val languageLocaleMap = mapOf<String, Locale>(
        "ja" to Locale.JAPANESE,
        "en" to Locale.ENGLISH,
        "zh" to Locale.CHINESE,
        "es" to Locale("es"),
        "ko" to Locale.KOREAN
    )
}