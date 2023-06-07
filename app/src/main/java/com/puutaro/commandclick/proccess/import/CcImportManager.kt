package com.puutaro.commandclick.proccess.import

import android.content.Context
import android.util.Log
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import kotlinx.coroutines.*
import java.io.File

object CcImportManager {

    private val importPreWord = "ccimport"
    private val assetsPrefix = "/android_asset/"

    fun replace(
        context: Context?,
        row: String,
        scriptPath: String,
    ): String {
        val jsFileObj = File(scriptPath)
        if(!jsFileObj.isFile) return String()
        val recentAppDirPath = jsFileObj.parent
            ?: return String()
        val scriptFileName = jsFileObj.name
        val fannelDirName = scriptFileName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        val trimRow = row
            .trim()
            .trim(';')
        val cmdclickDirPath = UsePath.cmdclickDirPath
        if(
            !trimRow.contains(importPreWord)
        ) return row
        val trimImportPathSource = trimRow
            .replace(importPreWord, "")
            .trim()
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    scriptPath,
                    recentAppDirPath,
                    fannelDirName,
                    scriptFileName
                )
            }
            .let {
                BothEdgeQuote.trim(it)
            }
        if(
            trimImportPathSource.startsWith(
                WebUrlVariables.httpPrefix
            ) || trimImportPathSource.startsWith(
                WebUrlVariables.httpsPrefix
            )
        ) {
            return execCurl(
                trimImportPathSource
            )
        }
        if(
            trimImportPathSource.startsWith(
                assetsPrefix
            )
        ){
            return FileSystems.readFromAssets(
                context,
                trimImportPathSource.removePrefix(assetsPrefix)
            )
        }
        if (
            trimImportPathSource.startsWith("./")
        ) {
            val readPath = trimImportPathSource.replace("./", "${recentAppDirPath}/")
            return catImportContents(
                readPath,
            )


        }
        if (
            !trimImportPathSource.startsWith("../")
        ) return catImportContents(
            trimImportPathSource,
        )
        val pathSuffix = trimImportPathSource.replace(
            Regex("^(\\.\\./)*"), ""
        )
        val cdTimes = trimImportPathSource.replace(
            pathSuffix,
            ""
        ).replace("../", "1").count()
        val currentDirPathList = recentAppDirPath.split("/")
        val currentDirPathListSize = currentDirPathList.size
        if(
            currentDirPathListSize <= cdTimes
        ) return "/$pathSuffix"
        val importPath = currentDirPathList
            .reversed()
            .slice(
                cdTimes until currentDirPathListSize
            )
            .reversed()
            .joinToString("/") + "/$pathSuffix"
        return catImportContents(
            importPath,
        )
    }

    private fun catImportContents(
        importPath: String,
    ): String {
        val readPathObj = File(importPath)
        if(
            !readPathObj.isFile
        ) return String()
        val parentDir = readPathObj.parent
            ?: return String()
        val importFileName = readPathObj.name
        return ReadText(
            parentDir,
            importFileName
        ).readText()

    }

    private fun execCurl(
        trimImportPathSource: String
    ): String {
        var downloadOk = false
        var downloadString = String()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                downloadString = withContext(Dispatchers.IO) {
                    CurlManager.get(
                        trimImportPathSource,
                        String(),
                        String(),
                        2000,
                    )
                }
                withContext(Dispatchers.IO) {
                    downloadOk = true
                }
            } catch (e: Exception){
                Log.e("curl", e.toString())

            }
        }
        runBlocking {
            for (i in 1..60){
                if(downloadOk) break
                delay(50)
            }
        }
        return downloadString
    }
}