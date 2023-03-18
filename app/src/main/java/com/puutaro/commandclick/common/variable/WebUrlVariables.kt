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