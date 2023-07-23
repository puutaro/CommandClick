package com.puutaro.commandclick.common.variable

import me.bush.translator.Language

object Translate {
    val languageMap = mapOf<String, Language>(
        "ja" to Language.JAPANESE,
        "en" to Language.ENGLISH,
        "zh" to Language.CHINESE_TRADITIONAL,
        "es" to Language.SPANISH
    )
}