package com.puutaro.commandclick.common.variable.variant

import java.util.Locale

object Translate {

    val languageLocaleMap = mapOf<String, Locale>(
        "ja" to Locale.JAPANESE,
        "en" to Locale.ENGLISH,
        "zh" to Locale.CHINESE,
        "es" to Locale("es"),
        "ko" to Locale.KOREAN
    )
}