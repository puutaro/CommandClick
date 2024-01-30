package com.puutaro.commandclick.common.variable.variables

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object WebUrlVariables {

    val queryUrl = "https://www.google.co.id/search?q="
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

    fun makeUrlHistoryFile(
        dirPath: String,
    ){
        val urlHistoryContents = "CommandClick\t$commandClickFirstLaunchUrl\n"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        if(
            File(
                dirPath,
                cmdclickUrlHistoryFileName
            ).isFile
        ) return
        FileSystems.writeFile(
            dirPath,
            cmdclickUrlHistoryFileName,
            urlHistoryContents
        )
    }
}