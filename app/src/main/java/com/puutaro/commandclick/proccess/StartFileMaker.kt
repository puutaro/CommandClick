package com.puutaro.commandclick.proccess

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsMacroType
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.CcPathTool
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
    private val cmdBookmaker = UrlFileSystems.cmdBookmaker
    private val webSearcher = UrlFileSystems.webSearcher
    private val askGpt35 = UrlFileSystems.askGpt35

    fun makecmdTerminalListFiles(
        fragment: Fragment,
    ){
        val context = fragment.context
        val fannelDirName = CcPathTool.makeFannelDirName(
            SystemFannel.cmdTerminal
        )
        makeCmdTerminalListFile(
            context,
            fannelDirName,
            AssetsFileManager.cmdListTxt,
        )
        makeCmdTerminalListFile(
            context,
            fannelDirName,
            AssetsFileManager.extraKeyListTxt,
        )
    }

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
        val longPressMenuDirPath = ScriptPreWordReplacer.pathReplace(
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
                UrlFileSystems.getFileNameFromUrl(urlIntender),
                UrlFileSystems.getFileNameFromUrl(webSearcher),
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
                withContext(Dispatchers.IO){
                    UrlFileSystems.createFile(
                        urlTrans,
                        currentAppDirPath,
                    )
                }
            }
            val selectMenuListFilePath = ScriptPreWordReplacer.pathReplace(
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

    fun makeForWebSearcher(
        fragment: Fragment,
        currentAppDirPath: String,
    ){
        val webSearcherFannelName = UrlFileSystems.getFileNameFromUrl(
            webSearcher
        )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val leftMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.leftMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    leftMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            JsMacroType.GO_BACK_JS.str
                        ),
                    )
                )
            }
            withContext(Dispatchers.IO) {
                val centerMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.centerMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    centerMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            JsMacroType.HIGHLIGHT_SCH_JS.str
                        ),
                    )
                )
                val centerLongPressMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.centerLongPressMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    centerLongPressMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            siteSummaryUrl
                        ),
                    )
                )
            }
            withContext(Dispatchers.IO) {
                val centerMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.rightMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    centerMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            JsMacroType.LAUNCH_LOCAL_JS.str
                        ),
                    )
                )
            }
            withContext(Dispatchers.IO) {
                val srcImageAnchorMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.srcImageAnchorMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    srcImageAnchorMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            siteSummaryUrl
                        ),
                    )
                )
            }
            withContext(Dispatchers.IO) {
                val srcAnchorListFilePathForWebSearcher = ScriptPreWordReplacer.pathReplace(
                    UsePath.srcAnchorListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    srcAnchorListFilePathForWebSearcher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            siteSummaryUrl
                        ),
                    )
                )
            }
            withContext(Dispatchers.IO) {
                val imageMenuListFilePathForWebSearher = ScriptPreWordReplacer.pathReplace(
                    UsePath.imageMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName
                )
                InitSettingListFile.makeFile(
                    imageMenuListFilePathForWebSearher,
                    currentAppDirPath,
                    webSearcherFannelName,
                    listOf(
                        UrlFileSystems.getFileNameFromUrl(
                            shareImage
                        ),
                    )
                )
            }
        }
    }

    fun makeForConfig(
        fragment: Fragment,
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


    fun makeFile(
        settingListFilePathSrc: String,
        currentAppDirPath: String,
        scriptName: String,
        settingConList: List<String>,
    ){
        val settingListFilePath = ScriptPreWordReplacer.pathReplace(
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

private fun makeCmdTerminalListFile(
    context: Context?,
    fannelDirName: String,
    targetAssetsFilePath: String,
) {
    val fannelDirPath = "${UsePath.cmdclickSystemAppDirPath}/${fannelDirName}"
    val listFilePath = targetAssetsFilePath.replace(
        AssetsFileManager.cmdTerminalDirPath,
        fannelDirPath
    )
    val listFilePathObj = File(listFilePath)
    if(
        listFilePathObj.isFile
    ) return
    val listFileDirPath = listFilePathObj.parent
        ?: return
    val listFileName = listFilePathObj.name
        ?: return
    FileSystems.writeFile(
        listFileDirPath,
        listFileName,
        AssetsFileManager.readFromAssets(
            context,
            targetAssetsFilePath
        )
    )
}
