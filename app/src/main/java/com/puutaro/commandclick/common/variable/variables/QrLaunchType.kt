package com.puutaro.commandclick.common.variable.variables

enum class QrLaunchType(
    val prefix: String,
) {
    Http(WebUrlVariables.httpPrefix),
    Https(WebUrlVariables.httpsPrefix),
    Javascript(WebUrlVariables.jsPrefix),
    JsDesc("jsDesc:"),
    CpFannel("cpFannel:"),
}