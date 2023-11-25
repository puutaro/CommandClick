package com.puutaro.commandclick.common.variable.intent.extra

enum class TextToMp3IntentExtra(
    val scheme: String
) {
    text("text"),
    outDir("outDir"),
    atomicName("atomicName")
}