package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.util.FileSystems
import java.io.File

class WebUrlVariables {
    companion object {
        val queryUrl = "https://www.google.co.id/search?q="
        val escapeStr = "about:blank"
        val httpPrefix = "http://"
        val httpsPrefix = "https://"
        val filePrefix = "file:"
        val slashPrefix = "/"
        val jsPrefix = "javascript:"
        val commandClickGitUrl = "https://github.com/puutaro/CommandClick#readme"
        val commandClickRepositoryUrl = "https://github.com/puutaro/commandclick-repository.git"
        val base64Prefix = "data:image/"
        val base64JpegPrefix = "${base64Prefix}jpeg;base64"
        val base64PngPrefix = "${base64Prefix}png;base64"

        fun makeUrlHistoryFile(
            dirPath: String,
        ){
            val urlHistoryContents = "CommandClick\t${commandClickGitUrl}\n"
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
}