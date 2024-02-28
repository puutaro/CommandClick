package com.puutaro.commandclick.proccess.import

import android.content.Context
import android.util.Log
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import kotlinx.coroutines.*
import java.io.File

object JsImportManager {

    val jsImportPreWord = "jsimport"
    private val assetsPrefix = "/android_asset/"

    fun import(
        context: Context?,
        row: String,
        scriptPath: String,
        setReplaceVariableCompleteMap: Map<String, String>? = null
    ): String {
        val jsFileObj = File(scriptPath)
        if(!jsFileObj.isFile) return String()
        val recentAppDirPath = jsFileObj.parent
            ?: return String()
        val scriptFileName = jsFileObj.name
        val trimRow = row
            .trim()
            .trim(';')
        if(
            !trimRow.contains(jsImportPreWord)
        ) return row
        val trimImportPathSource = trimRow
            .replace(jsImportPreWord, "")
            .trim()
            .let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableCompleteMap,
                    recentAppDirPath,
                    scriptFileName
                )
            }
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    recentAppDirPath,
                    scriptFileName
                )
            }
            .let {
                QuoteTool.trimBothEdgeQuote(it)
            }
        if(
            trimImportPathSource.startsWith(
                WebUrlVariables.httpPrefix
            ) || trimImportPathSource.startsWith(
                WebUrlVariables.httpsPrefix
            )
        ) {
            return execCurl(
                context,
                trimImportPathSource
            )
        }
        if(
            trimImportPathSource.startsWith(
                assetsPrefix
            )
        ){
            return AssetsFileManager.readFromAssets(
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
        return ReadText(
            readPathObj.absolutePath
        ).readText()

    }

    private fun execCurl(
        context: Context?,
        trimImportPathSource: String
    ): String {
        var downloadOk = false
        var downloadString = String()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                downloadString = withContext(Dispatchers.IO) {
                    CurlManager.get(
                        context,
                        trimImportPathSource,
                        String(),
                        String(),
                        2000,
                    ).let {
                        CurlManager.convertResToStrByConn(it)
                    }
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