package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.variables.QrLaunchType

object QrSchema {

    fun makeQrMapFromCon(
        qrConWithNewLine: String,
    ): Map<String, String> {
        val qrCon = qrConWithNewLine.replace("\n", "")
        return when (true) {
            qrCon.startsWith(QrLaunchType.CpFile.prefix),
            -> {
                makeQrMap(
                    qrCon,
                    CpFileKey.values().map { it.key }
                )
            }
            qrCon.startsWith(QrLaunchType.ScpDir.prefix),
            -> makeQrMap(
                qrCon,
                ScpDirKey.values().map { it.key }
            )
            qrCon.startsWith(QrLaunchType.ON_GIT.prefix)
            -> makeQrMap(
                qrCon,
                OnGitKey.values().map { it.key }
            )
            qrCon.startsWith(QrLaunchType.G_CALENDAR.prefix),
            qrCon.startsWith(QrLaunchType.G_CALENDAR.prefix.uppercase()),
            -> makeQrMap(
                qrCon,
                GCalendarKey.values().map { it.key }
            )
            qrCon.startsWith(QrLaunchType.MAIL.prefix),
            qrCon.startsWith(QrLaunchType.MAIL.prefix.uppercase()),
            qrCon.startsWith(QrLaunchType.MAIL2.prefix),
            qrCon.startsWith(QrLaunchType.MAIL2.prefix.uppercase()),
            -> compMap(
                QrMapper.makeGmailMap(qrCon),
                GmailKey.values().map { it.key }
            )
            qrCon.startsWith(QrLaunchType.WIFI.prefix),
            qrCon.startsWith(QrLaunchType.WIFI.prefix.uppercase())
            -> {
                QrMapper.getWifiWpaSsidAndPinPair(qrCon).let {
                    mapOf(
                        WifiKey.SSID.key to (it.first ?: String()),
                        WifiKey.PIN.key to (it.second ?: String()),
                    )
                }
            }
            qrCon.startsWith(QrLaunchType.SMS.prefix),
            qrCon.startsWith(QrLaunchType.SMS.prefix.uppercase()),
            -> {
               QrMapper.getSmsNumAndBody(qrCon).let {
                   mapOf(
                       SmsKey.NUMBER.key to (it.first ?: String()),
                       SmsKey.BODY.key to (it.second ?: String()),
                   )
               }
            }
            qrCon.startsWith(QrLaunchType.TEL.prefix),
            qrCon.startsWith(QrLaunchType.TEL.prefix.uppercase()),
            -> {
                val telPrefix = QrLaunchType.TEL.prefix
                mapOf(
                    TelKey.NUMBER.key to qrCon
                        .removePrefix(telPrefix)
                        .removePrefix(telPrefix.uppercase())
                )
            }
            else
            -> mapOf(FreeTextKey.FREE_TEXT.key to qrCon)
        }
    }

    private fun makeQrMap(
        qrCon: String,
        keyList: List<String>
    ): Map<String, String> {
        return compMap(
            QrMapper.convertScanConToMap(qrCon),
            keyList
        )
    }

    private fun compMap(
        qrMap: Map<String, String?>?,
        keyList: List<String>
    ): Map<String, String> {
//        if(
//            qrMap.isNullOrEmpty()
//        ) return mapOf()
        return keyList.map {
            it to (qrMap?.get(it) ?: String())
        }.toMap().filterKeys { it.isNotEmpty() }
    }
}