package com.puutaro.commandclick.common.variable.broadcast.extra

enum class TextToMp3IntentExtra(
    val scheme: String
) {
    text("text"),
    outDir("outDir"),
    atomicName("atomicName")
}