package com.puutaro.commandclick.proccess.qr

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.url.SiteUrl

object QrDecodedTitle {

    private const val displayTitleTextLimit = 30
    private val jsDescSeparator = QrSeparator.sepalator.str

    suspend fun makeQrDecodeMap(
        context: Context?,
        scanConWithNewline: String
    ): Map<QrDecodeKey, String> {
        val scanCon = scanConWithNewline.replace("\n", "")
        return when(true){
            scanCon.startsWith(QrLaunchType.Http.prefix),
            scanCon.startsWith(QrLaunchType.Https.prefix)
            -> makeDocFromUrl(
                context,
                scanCon
            )
            scanCon.startsWith(QrLaunchType.Javascript.prefix) -> {
                val registerTitle = scanCon.take(
                    displayTitleTextLimit
                )
                execMakeQrDecodeMap(
                    "Exec js",
                    scanCon,
                    registerTitle,
                    scanCon
                )

            }
            scanCon.startsWith(QrLaunchType.CpFile.prefix) -> {
                createCopyFileDecodeMap(scanCon)
            }
            scanCon.startsWith(QrLaunchType.ScpDir.prefix)
            -> createScpDirDecodeMap(scanCon)
            scanCon.startsWith(QrLaunchType.JsDesc.prefix)
            -> extractDecodeMapForJsDesc(scanCon)
            scanCon.startsWith(QrLaunchType.WIFI.prefix),
            scanCon.startsWith(QrLaunchType.WIFI.prefix.uppercase())
            -> createWifiDecodeMap(scanCon)
            scanCon.startsWith(QrLaunchType.SMS.prefix),
            scanCon.startsWith(QrLaunchType.SMS.prefix.uppercase())
            -> createSmsDecodeMap(scanCon)
            scanCon.startsWith(QrLaunchType.MAIL.prefix),
            scanCon.startsWith(QrLaunchType.MAIL.prefix.uppercase()),
            scanCon.startsWith(QrLaunchType.MAIL2.prefix),
            scanCon.startsWith(QrLaunchType.MAIL2.prefix.uppercase()),
            ->  createGmailDecodeMap(scanCon)
            scanCon.startsWith(QrLaunchType.G_CALENDAR.prefix),
            scanCon.startsWith(QrLaunchType.G_CALENDAR.prefix.uppercase())
            -> createGcalendarDecodeMap(scanCon)
            scanCon.startsWith(QrLaunchType.ON_GIT.prefix)
            -> createOnGitDecodeMap(scanCon)
            else -> {
                execMakeQrDecodeMap(
                    "Copy",
                    scanCon,
                    "Copy ok?: $scanCon",
                    scanCon

                )

            }
        }
    }

    private fun createCopyFileDecodeMap(
        scanCon: String
    ): Map<QrDecodeKey, String> {
        val cpFileMap = QrMapper.convertScanConToMap(scanCon)
        val displayTitle = "Cp from other ok?"
        val displayBody = let {
            val filePath = cpFileMap.get(CpFileKey.PATH.key)?.let makeFilePath@ {
               CcPathTool.makeSummaryPath(it)
            }
            if (
                filePath.isNullOrEmpty()
            ) {
                return@let scanCon
            }
            return@let "Copy path: ${filePath} ${scanCon}"
        }

        val registerTitle = displayBody.take(
            displayTitleTextLimit
        )
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun createScpDirDecodeMap(
        scanCon: String
    ): Map<QrDecodeKey, String> {
        val scpDirMap = QrMapper.convertScanConToMap(scanCon)
        val displayTitle = "Copy by SSH"
        val displayBody = let {
            val dirPath = scpDirMap.get(ScpDirKey.DIR_PATH.key)?.let makePath@ {
                CcPathTool.makeSummaryPath(it)
            }
            if (
                dirPath.isNullOrEmpty()
            ) {
                return@let scanCon
            }
            "Scp path: ${dirPath}"
        }
        val registerTitle = displayBody.take(
            displayTitleTextLimit
        )
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }


    private fun createWifiDecodeMap(
        scanCon: String
    ): Map<QrDecodeKey, String> {
        val displayTitle = "Connect WIFI"
        val displayBody = QrMapper.getWifiWpaSsidAndPinPair(scanCon).let {
            "WIFI ssid: ${it.first} pin: ${it.second}"
        }
        val registerTitle = displayBody.take(displayTitleTextLimit)
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }
    private fun createSmsDecodeMap(
        scanCon: String
    ): Map<QrDecodeKey, String> {
        val displayTitle = "Send SMS"
        val displayBody = QrMapper.getSmsNumAndBody(scanCon).let {
            "SMS tel: ${it.first} body: ${it.second}"
        }
        val registerTitle = displayBody.take(displayTitleTextLimit)
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun createGmailDecodeMap(
        scanCon: String,
    ): Map<QrDecodeKey, String> {
        val displayTitle = "Gmail"
        val gmailMap = QrMapper.makeGmailMap(scanCon)
        val displayBody = let {
            val subject = gmailMap?.get(GmailKey.SUBJECT.key)
            if (
                !subject.isNullOrEmpty()
            ) return@let "Gmail: $subject"
            val mailAd = gmailMap?.get(
                GmailKey.MAIL_AD.key
            )
            val body = gmailMap?.get(GmailKey.BODY.key)
            return@let "Ad ${mailAd}\nBody: ${body}"
        }
        val registerTitle = displayBody.take(displayTitleTextLimit)
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun createGcalendarDecodeMap(scanCon: String): Map<QrDecodeKey, String> {
        val gcalendarMap = QrMapper.convertScanConToMap(scanCon)
        val displayTitle = "Register calendar"
        val registerTitle = let {
            val titleSrc = gcalendarMap.get(GCalendarKey.TITLE.key)
                ?: return@let scanCon.take(displayTitleTextLimit)
            return@let "calendar: $titleSrc".take(displayTitleTextLimit)
        }
        val displayBody = scanCon
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun createOnGitDecodeMap(
        scanCon: String
    ): Map<QrDecodeKey, String> {
        val onGitMap = QrMapper.convertScanConToMap(scanCon)
        val displayTitle = "Git download, ok?"
        val displayBody = let {
            val registerTitleSrc = onGitMap.get(OnGitKey.NAME.key)
                ?: return@let scanCon
            registerTitleSrc
        }
        val registerTitle = displayBody.take(displayTitleTextLimit)
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun extractDecodeMapForJsDesc(scanCon: String): Map<QrDecodeKey, String> {
        val displayTitle = "Exec js, ok?"
        val displayBody = scanCon
            .split(jsDescSeparator)
            .firstOrNull()?.trim()
            ?.removePrefix(QrLaunchType.JsDesc.prefix)
            ?.trim()
            ?: scanCon
        val registerTitle = displayBody.take(
            displayTitleTextLimit
        )
        val registerBody = scanCon
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private suspend fun makeDocFromUrl(
        context: Context?,
        targetUrl: String
    ): Map<QrDecodeKey, String> {
        val displayTitle = "Load url, ok?"
        val titleSrc = SiteUrl.getTitle(
            context,
            targetUrl
        )
        val displayBody = targetUrl
        val registerTitle = when(
            titleSrc.isEmpty()
        ) {
            true -> "Url: ${targetUrl}"
            else -> "Url title: ${titleSrc}"
        }
        val registerBody = when(
            titleSrc.isEmpty()
        ) {
            true -> targetUrl
            else -> "Title: ${titleSrc}"
        }
        return execMakeQrDecodeMap(
            displayTitle,
            displayBody,
            registerTitle,
            registerBody,
        )
    }

    private fun execMakeQrDecodeMap(
        displayTitle: String,
        displayBody: String,
        registerTitle: String,
        registerBody: String,
    ): Map<QrDecodeKey, String> {
        return mapOf(
            QrDecodeKey.DISPLAY_TITLE to displayTitle,
            QrDecodeKey.DISPLAY_BODY to displayBody,
            QrDecodeKey.REGISTER_TITLE to registerTitle,
            QrDecodeKey.REGISTER_BODY to registerBody,
        )
    }

    enum class QrDecodeKey{
        DISPLAY_TITLE,
        DISPLAY_BODY,
        REGISTER_TITLE,
        REGISTER_BODY,

    }
}
