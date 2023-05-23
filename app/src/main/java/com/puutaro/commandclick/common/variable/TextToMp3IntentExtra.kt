package com.puutaro.commandclick.common.variable

enum class TextToMp3IntentExtra(
    val scheme: String
) {
    text("text"),
    outDir("outDir"),
    atomicName("atomicName")
}