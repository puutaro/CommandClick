package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.CcScript

object QrMapper {

    fun getWifiWpaSsidAndPinPair(
        scanStr: String
    ): Pair<String?, String?> {
        val wifiNetowrkRegex = "T:[^;]+;S:([^;]+);P:([^;]+).*".toRegex()
        return scanStr.split(":").filterIndexed {
                index, _ -> index > 0
        }.joinToString(":").replace(
            wifiNetowrkRegex,
            "$1\t$2"
        ).split("\t").let {
            it.firstOrNull() to it.lastOrNull()
        }
    }

    fun getSmsNumAndBody(
        scanStr: String
    ): Pair<String?, String?> {
        return scanStr.split(":").filterIndexed{
                index, _ -> index > 0
        }.let {
            it.firstOrNull() to it.getOrNull(1)
        }
    }

    fun makeGmailMap(
        scanStr: String
    ): Map<String, String?>? {
        val excludeMailPrefixStr =
            scanStr
                .replaceFirst(Regex(";(SUB):|;(BODY):"), "?$1=")
                .replaceFirst("?SUB=", "?${GmailKey.SUBJECT.key}=")
                .replaceFirst("?BODY=", "&${GmailKey.BODY.key}=")
                .replaceFirst(";SUB:", "&${GmailKey.SUBJECT.key}=")
                .replaceFirst(";BODY:", "&${GmailKey.BODY.key}=")
                .trim()
                .replaceFirst(Regex(";+$"), "")
                .split(":")
                .filterIndexed { index, _ ->
                    index > 0
                }.joinToString(":")
        val addAndExtraList = excludeMailPrefixStr.split("?")
        val mailAd = addAndExtraList.firstOrNull()?.trim()?.removePrefix("TO:")
            ?: return null
        val mailAdMap = mapOf(
            GmailKey.MAIL_AD.key to mailAd
        )
        val subjectBodyMap = addAndExtraList.getOrNull(1)?.split("&")?.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }?.toMap()
        if (
            subjectBodyMap.isNullOrEmpty()
        ) return mailAdMap
        return mailAdMap + subjectBodyMap
    }

    fun extractCopyPath(
        cpQrString: String
    ): Pair<String, String>? {
        val httpPrefix = WebUrlVariables.httpPrefix
        val cpFilePrefix = QrLaunchType.CpFile.prefix
        val urlAndFilePath =
            cpQrString
                .trim()
                .removePrefix(cpFilePrefix)
                .trim()
                .split(";")
        val url =
            urlAndFilePath.firstOrNull()?.trim()?.let {
                if(
                    it.startsWith(WebUrlVariables.httpPrefix)
                    || it.startsWith(WebUrlVariables.httpsPrefix)
                ) return@let it
                "${httpPrefix}${it}"
            } ?: return null
        val filePath =
            urlAndFilePath.getOrNull(1)?.trim()
                ?: return null
        return url to filePath
    }


    fun makeOnGitMap(
        onGitSrcCon: String
    ): Map<String, String?> {
        val onGitMapSrcStr =
            onGitSrcCon
                .split(":")
                .filterIndexed { index, _ ->
                    index > 0
                }.joinToString(":")
        return onGitMapSrcStr.split(";").map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }

    fun makeGCalendarMap(
        gCalendarStr: String
    ): Map<String, String?> {
        val gCalendarMapSrcStr =
            gCalendarStr
                .split(":")
                .filterIndexed { index, _ ->
                    index > 0
                }.joinToString(":")
        return gCalendarMapSrcStr.split(";").map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }
}


enum class GmailKey(
    val key: String
){
    MAIL_AD("mail_ad"),
    SUBJECT("subject"),
    BODY("body"),
}

enum class GCalendarKey(
    val key: String
){
    BIGIN_TIME("biginTime"),
    END_TIME("endTime"),
    TITLE("title"),
    DESCRIPTION("description"),
    EVENT_LOCATION("eventLocation"),
    EMAIL("android.intent.extra.EMAIL")
}

enum class OnGitKey(
    val key: String
){
    PREFIX("prefix"),
    LIST_PATH("listPath"),
    DIR_PATH("dirPath"),
    NAME("name"),
}