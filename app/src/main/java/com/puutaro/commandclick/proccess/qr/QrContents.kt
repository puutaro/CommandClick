package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.variables.QrLaunchType

object QrContents {
    fun makeFromMap(
        qrConWithNewLine: String,
        qrMap: Map<String, String>
    ): String {
        val qrCon = qrConWithNewLine.replace("\n", "")
        return when (true) {
            qrCon.startsWith(QrLaunchType.CpFile.prefix),
            -> makeQrCon(
                QrLaunchType.CpFile.prefix,
                qrMap,
            )
            qrCon.startsWith(QrLaunchType.ScpDir.prefix),
            -> makeQrCon(
                QrLaunchType.ScpDir.prefix,
                qrMap,
            )
            qrCon.startsWith(QrLaunchType.ON_GIT.prefix)
            -> makeQrCon(
                QrLaunchType.ON_GIT.prefix,
                qrMap,
            )
            qrCon.startsWith(QrLaunchType.G_CALENDAR.prefix),
            qrCon.startsWith(QrLaunchType.G_CALENDAR.prefix.uppercase()),
            -> makeQrCon(
                QrLaunchType.G_CALENDAR.prefix,
                qrMap,
            )
            qrCon.startsWith(QrLaunchType.MAIL.prefix),
            qrCon.startsWith(QrLaunchType.MAIL.prefix.uppercase()),
            qrCon.startsWith(QrLaunchType.MAIL2.prefix),
            qrCon.startsWith(QrLaunchType.MAIL2.prefix.uppercase()),
            -> makeGmailCon(
                QrLaunchType.MAIL.prefix,
                qrMap
            )
            qrCon.startsWith(QrLaunchType.WIFI.prefix),
            qrCon.startsWith(QrLaunchType.WIFI.prefix.uppercase())
            -> makeWifiCon(
                QrLaunchType.WIFI.prefix,
                qrMap
            )
            qrCon.startsWith(QrLaunchType.SMS.prefix),
            qrCon.startsWith(QrLaunchType.SMS.prefix.uppercase()),
            -> makeSmsCon(
                QrLaunchType.SMS.prefix,
                qrMap
            )
            qrCon.startsWith(QrLaunchType.TEL.prefix),
            qrCon.startsWith(QrLaunchType.TEL.prefix.uppercase()),
            -> makeTelCon(
                QrLaunchType.TEL.prefix,
                qrMap
            )
            else -> String()
        }
    }

    private fun makeQrCon(
        prefix: String,
        qrMap: Map<String, String>
    ): String {
        return prefix +
                qrMap.filterValues {
                    it.isNotEmpty()
                }.map {
                    "${it.key}=${it.value}"
                }.joinToString(";")
    }

    private fun makeTelCon(
        prefix: String,
        qrMap: Map<String, String>
    ): String {
        val number =qrMap.get(TelKey.NUMBER.key)
            ?: String()
        return "${prefix}${number}"
    }

    private fun makeWifiCon(
        prefix: String,
        qrMap: Map<String, String>
    ): String {
        val ssid =qrMap.get(WifiKey.SSID.key)
        val pin =qrMap.get(WifiKey.PIN.key)
        return "${prefix}T:WPA;S:${ssid};P:${pin};;"
    }

    private fun makeSmsCon(
        prefix: String,
        qrMap: Map<String, String>
    ): String {
//        "smsto:<phone>:<message>"
        val number = qrMap.get(SmsKey.NUMBER.key)
        val body = qrMap.get(SmsKey.BODY.key)
        return prefix + listOf(
            number,
            body,
        ).filter { !it.isNullOrEmpty() }.joinToString(":")
    }

    private fun makeGmailCon(
        prefix: String,
        qrMap: Map<String, String>
    ): String {
//        mailto:<email>?subject=<subject>&body=<body>
        val main = prefix + qrMap.get(GmailKey.MAIL_AD.key)
        return main + makeMailSubCode(qrMap)
    }

    private fun makeMailSubCode(
        qrMap: Map<String, String>
    ): String {
        val subjectKeyName = GmailKey.SUBJECT.key
        val subjectValue = qrMap.get(subjectKeyName)?.let {
            if(it.isEmpty()) return@let String()
            "${subjectKeyName}=${it}"
        } ?: String()
        val bodyKeyName = GmailKey.BODY.key
        val bodyValue = qrMap.get(bodyKeyName)?.let {
            if(it.isEmpty()) return@let String()
            "${bodyKeyName}=${it}"
        } ?: String()
        if(
            subjectValue.isEmpty() && bodyValue.isEmpty()
        ) return String()
        return "?" + listOf(
            subjectValue,
            bodyValue
        ).joinToString("&")
    }
}