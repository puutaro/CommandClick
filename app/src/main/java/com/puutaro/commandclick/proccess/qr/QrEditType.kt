package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.variables.QrLaunchType

enum class QrEditType(
    val prefixList: List<String>,
    val type: String
) {
    FREE_TEXT(
        listOf(
            QrLaunchType.Http.prefix,
            QrLaunchType.Https.prefix,
            QrLaunchType.Javascript.prefix,
            QrLaunchType.JsDesc.prefix,
        ),
        "free: Url, js, text"
    ),
    SMS(
        listOf(
            QrLaunchType.SMS.prefix,
        ),
        QrLaunchType.SMS.prefix,
    ),
    MAIL(
        listOf(
            QrLaunchType.MAIL.prefix,
            QrLaunchType.MAIL2.prefix,
        ),
        QrLaunchType.MAIL.prefix,
    ),
    TEL(
        listOf(
            QrLaunchType.TEL.prefix,
        ),
        QrLaunchType.TEL.prefix,
    ),
    WIFI(
        listOf(
            QrLaunchType.WIFI.prefix,
        ),
        QrLaunchType.WIFI.prefix,
    ),
    G_CALENDAR(
        listOf(
            QrLaunchType.G_CALENDAR.prefix,
        ),
        QrLaunchType.G_CALENDAR.prefix,
    ),
    ON_GIT(
        listOf(
            QrLaunchType.ON_GIT.prefix,
        ),
        QrLaunchType.ON_GIT.prefix,
    ),
    CpFile(
        listOf(
            QrLaunchType.CpFile.prefix,
        ),
        QrLaunchType.CpFile.prefix,
    ),
    ScpDir(
        listOf(
            QrLaunchType.ScpDir.prefix,
        ),
        QrLaunchType.ScpDir.prefix,
    ),
}