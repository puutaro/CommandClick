package com.puutaro.commandclick.common.variable.variables

enum class QrLaunchType(
    val prefix: String,
) {
    Http(WebUrlVariables.httpPrefix),
    Https(WebUrlVariables.httpsPrefix),
    Javascript(WebUrlVariables.jsPrefix),
    JsDesc("jsDesc:"),
    CpFile("cpFile:"),
    WIFI("wifi:"),
    SMS("smsto:"),
    MAIL("mailto:"),
    MAIL2("matmsg:"),
    TEL("tel:"),
    G_CALENDAR("gcalendar:"),
    ON_GIT("onGit:"),

}