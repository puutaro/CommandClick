package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.UrlFileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object StartFileMaker {

    private val siteSummaryUrl = UrlFileSystems.siteSummaryUrl
    private val urlIntender = UrlFileSystems.urlIntender
    private val shareImage = UrlFileSystems.shareImage
    private val textToSpeech = UrlFileSystems.textToSpeech
    private val urlTrans = UrlFileSystems.urlTrans
    private val selectMenu = UrlFileSystems.selectMenu
    private val highlightSch = UrlFileSystems.highlightSch
    private val cmdBookmaker = UrlFileSystems.cmdBookmaker
    private val webSearcher = UrlFileSystems.webSearcher
    private val askGpt35 = UrlFileSystems.askGpt35


    fun makeForStartupScript(
        fragment: Fragment,
        currentAppDirPath: String
    ){
        val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                UrlFileSystems.createFile(
                    siteSummaryUrl,
                    currentAppDirPath,
                )
            }
            withContext(Dispatchers.IO){
                UrlFileSystems.createFile(
                    urlIntender,
                    currentAppDirPath,
                )
            }
            withContext(Dispatchers.IO){
                UrlFileSystems.createFile(
                    shareImage,
                    currentAppDirPath,
                )
            }
            withContext(Dispatchers.IO){
                UrlFileSystems.createFile(
                    textToSpeech,
                    currentAppDirPath,
                )
            }
            withContext(Dispatchers.IO){
                UrlFileSystems.createFile(
                    webSearcher,
                    currentAppDirPath,
                )
            }
            UrlFileSystems.createFile(
                cmdBookmaker,
                currentAppDirPath,
            )
            UrlFileSystems.createFile(
                askGpt35,
                currentAppDirPath,
            )
        }
        CommandClickScriptVariable.makeAutoJsFile(
            currentAppDirPath,
            cmdclickStartupJsName
        )
        val longPressMenuDirPath = InitSettingListFile.pathReplace(
            UsePath.longPressMenuDirPath,
            currentAppDirPath,
            cmdclickStartupJsName
        )
        FileSystems.createDirs(
            longPressMenuDirPath
        )
        InitSettingListFile.makeFile(
            UsePath.homeScriptUrlsFilePath,
            currentAppDirPath,
            cmdclickStartupJsName,
            listOf(
                "${currentAppDirPath}/" +
                        UrlFileSystems.getFileNameFromUrl(
                            textToSpeech
                        ),
                "${currentAppDirPath}/" +
                        UrlFileSystems.getFileNameFromUrl(
                            askGpt35
                        ),
                "${currentAppDirPath}/" +
                        UrlFileSystems.getFileNameFromUrl(
                            cmdBookmaker
                        ),
            )
        )
        InitSettingListFile.makeFile(
            UsePath.srcImageAnchorLongPressMenuFilePath,
            currentAppDirPath,
            cmdclickStartupJsName,
            listOf(
                UrlFileSystems.getFileNameFromUrl(siteSummaryUrl),
                UrlFileSystems.getFileNameFromUrl(shareImage),
                UrlFileSystems.getFileNameFromUrl(webSearcher),
                UrlFileSystems.getFileNameFromUrl(urlIntender),
            )
        )
        InitSettingListFile.makeFile(
            UsePath.imageLongPressMenuFilePath,
            currentAppDirPath,
            cmdclickStartupJsName,
            listOf(
                UrlFileSystems.getFileNameFromUrl(urlIntender),
                UrlFileSystems.getFileNameFromUrl(shareImage)
            )
        )
        InitSettingListFile.makeFile(
            UsePath.srcAnchorLongPressMenuFilePath,
            currentAppDirPath,
            cmdclickStartupJsName,
            listOf(
                UrlFileSystems.getFileNameFromUrl(siteSummaryUrl),
                UrlFileSystems.getFileNameFromUrl(urlIntender),
                UrlFileSystems.getFileNameFromUrl(webSearcher),
            )
        )
    }

    fun makeForSelectMenu(
        fragment: Fragment,
        currentAppDirPath: String
    ){
        val selectMenuFannelName = UrlFileSystems.getFileNameFromUrl(
            selectMenu
        )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                UrlFileSystems.createFile(
                    selectMenu,
                    currentAppDirPath,
                )
                UrlFileSystems.createFile(
                    highlightSch,
                    currentAppDirPath,
                )
                withContext(Dispatchers.IO){
                    UrlFileSystems.createFile(
                        urlTrans,
                        currentAppDirPath,
                    )
                }
            }
            val selectMenuListFilePath = InitSettingListFile.pathReplace(
                UsePath.menuListFilePathForSelectMenu,
                currentAppDirPath,
                selectMenuFannelName
            )
            InitSettingListFile.makeFile(
                selectMenuListFilePath,
                currentAppDirPath,
                selectMenuFannelName,
                listOf(
                    UrlFileSystems.getFileNameFromUrl(
                        siteSummaryUrl
                    ),
                    UrlFileSystems.getFileNameFromUrl(
                        urlTrans
                    ),
                    UrlFileSystems.getFileNameFromUrl(
                        webSearcher
                    ),
                )
            )
        }
    }

    fun makeForConfig(
        fragment: Fragment,
        currentAppDirPath: String
    ){
        CoroutineScope(Dispatchers.IO).launch {
            InitSettingListFile.makeFile(
                UsePath.homeFannelsFilePath,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName,
                listOf(
                    "${UsePath.cmdclickDefaultAppDirName}__.js",
                )
            )
        }
    }

    private fun makeFannelScript(
        fragment: Fragment,
        currentAppDirPath: String,
        fanneName: String,
        assetsRelativePath: String
    ){
        if(
            File(
                currentAppDirPath,
                fanneName
            ).isFile
        ) return
        val context = fragment.context
        val content = AssetsFileManager.readFromAssets(
            context,
            assetsRelativePath
        )
        FileSystems.writeFile(
            currentAppDirPath,
            fanneName,
            content
        )
    }
}

private object InitSettingListFile {

    fun pathReplace(
        longPressFilePath: String,
        currentAppDirPath: String,
        scriptName: String,
    ): String {
        val fannelDirName = scriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        return longPressFilePath.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                scriptName,
            )
        }
    }

    fun makeFile(
        settingListFilePathSrc: String,
        currentAppDirPath: String,
        scriptName: String,
        settingConList: List<String>,
    ){
        val settingListFilePath = pathReplace(
            settingListFilePathSrc,
            currentAppDirPath,
            scriptName
        )
        val settingListFilePathObj =
            File(settingListFilePath)
        if(
            settingListFilePathObj.isFile
        ) return
        val settingListDirPath = settingListFilePathObj.parent
            ?: return
        val settingListFileName = settingListFilePathObj.name
        val contents = settingConList.joinToString("\n")
        FileSystems.writeFile(
            settingListDirPath,
            settingListFileName,
            contents
        )
    }
}