package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.UrlFileSystems

object QrMapper {

    val onGitTemplate =
        QrLaunchType.ON_GIT.prefix +
                "${OnGitKey.PREFIX.key}=${UrlFileSystems.gitUserContentPrefix};" +
                "${OnGitKey.LIST_PATH.key}=manage/fannels/list/fannels.txt;" +
                "${OnGitKey.DIR_PATH.key}=fannel;" +
                "${OnGitKey.NAME.key}=%s"

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

    fun makeCpFileMap(
        cpCpFileSrcCon: String
    ): Map<String, String?> {
        val cpCpFileMapSrcStr =
            cpCpFileSrcCon
                .split(":")
                .filterIndexed { index, _ ->
                    index > 0
                }.joinToString(":")
        return cpCpFileMapSrcStr.split(";").map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
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

    fun makeMainUrl(
        mainUrlSrc: String?
    ): String? {
        if(
            mainUrlSrc.isNullOrEmpty()
        ) return null
        val httpPrefix = WebUrlVariables.httpPrefix
        if(
            mainUrlSrc.startsWith(httpPrefix)
            || mainUrlSrc.startsWith(WebUrlVariables.httpsPrefix)
        ) return mainUrlSrc
        return "${httpPrefix}${mainUrlSrc}"
    }
}

enum class CpFileKey(
    val key: String
){
    PATH("path"),
    CURRENT_APP_DIR_PATH_FOR_SERVER("currentAppDirPathForServer"),
    ADDRESS("address"),
    IS_MOVE_CURRENT_DIR("onMoveCurrentDir"),
    CP_FILE_MACRO_FOR_SERVICE("cpFileMacro"),
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