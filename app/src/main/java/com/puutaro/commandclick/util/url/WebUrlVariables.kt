package com.puutaro.commandclick.util.url

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object WebUrlVariables {

    val googleSearchIndexPage = "https://www.google.com/webhp"
    val queryUrlBase = "https://www.google.com/search?"
    val queryUrl = "${queryUrlBase}q="
    val blankEncodeQuery = "%20"
    val autoFocusGgleSearchUrl = "${queryUrl}${blankEncodeQuery}"
    val escapeStr = "about:blank"
    val httpPrefix = "http://"
    val httpsPrefix = "https://"
    val filePrefix = "file:"
    val slashPrefix = "/"
    val jsPrefix = "javascript:"
    val commandClickUsageUrl =
        "https://github.com/puutaro/CommandClick/blob/master/USAGE.md#command-click-usage"
    val commandClickFirstLaunchUrl =
        "https://github.com/puutaro/CommandClick/tree/master#setup-ubuntu"
    val commandClickRepositoryUrl =
        "https://github.com/puutaro/commandclick-repository.git"
    val monitorUrlPath = "${EditSettings.filePrefix}monitor_path"
    val base64Prefix = "data:image/"
    val base64JpegPrefix = "${base64Prefix}jpeg;base64"
    val base64PngPrefix = "${base64Prefix}png;base64"
    val base64WebpPrefix = "${base64Prefix}webp;base64"


    fun isGgleSearchIndexPage(url: String): Boolean {
        return  url.startsWith(httpsPrefix)
                && url.contains("google")
                && !url.contains("search?q=")
    }

    fun isMapUrl(
        previousUrl: String?,
        curUrl: String?
    ): Boolean {
        return (previousUrl?.length == curUrl?.length
                && curUrl?.contains("/maps/") == true
                )
                && curUrl.contains("google")
    }
    fun makeUrlHistoryFile(
        dirPath: String,
    ){
        val urlHistoryContents = "CommandClick\t$commandClickFirstLaunchUrl\n"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val cmdclickUrlHistoryFilePathObj = File(
            dirPath,
            cmdclickUrlHistoryFileName
        )
        if(
            cmdclickUrlHistoryFilePathObj.isFile
        ) return
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePathObj.absolutePath,
            urlHistoryContents
        )
    }
}