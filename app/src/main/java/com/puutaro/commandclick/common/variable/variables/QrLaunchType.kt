package com.puutaro.commandclick.common.variable.variables

import com.puutaro.commandclick.util.url.WebUrlVariables

enum class QrLaunchType(
    val prefix: String,
) {
    Http(WebUrlVariables.httpPrefix),
    Https(WebUrlVariables.httpsPrefix),
    Javascript(WebUrlVariables.jsPrefix),
    JsDesc("jsDesc:"),
    CpFile("cpFile:"),
    ScpDir("scpDir:"),
    WIFI("wifi:"),
    SMS("smsto:"),
    MAIL("mailto:"),
    MAIL2("matmsg:"),
    TEL("tel:"),
    G_CALENDAR("gcalendar:"),
    ON_GIT("onGit:"),
}
