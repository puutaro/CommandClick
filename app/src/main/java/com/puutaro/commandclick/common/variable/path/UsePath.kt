package com.puutaro.commandclick.common.variable.path

import android.os.Environment


object UsePath {
    val pdfExtend = ".pdf"
    private val tsvExtend = ".tsv"
    val SHELL_FILE_SUFFIX = ".sh"
    val JS_FILE_SUFFIX = ".js"
    val JSX_FILE_SUFFIX = ".jsx"
    val HTML_FILE_SUFFIX = ".html"
    val HTM_FILE_SUFFIX = ".htm"
    val TXT_FILE_SUFFIX = ".txt"
    private val rootPath = Environment.getExternalStorageDirectory().absolutePath +
            "/" + Environment.DIRECTORY_DOCUMENTS
    private const val cmdclickDirName = "cmdclick"
    val cmdclickDirPath = "$rootPath/$cmdclickDirName"
    private const val cmdclickConfDirName = "conf"
    private const val cmdclickConfRelativePath = "$cmdclickDirName/$cmdclickConfDirName"
    val cmdclickConfDirPath =  "$rootPath/$cmdclickConfRelativePath"
    private const val cmdclickTempDirName = "temp"
    private const val cmdclickTempRelativePath = "$cmdclickDirName/$cmdclickTempDirName"
    private const val cmdclickTmpDownloadDirName = "download"
    private const val cmdclickTempDownloadRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpDownloadDirName"
    val cmdclickTempDownloadDirPath = "$rootPath/$cmdclickTempDownloadRelativeDirPath"
    private const val cmdclickTmpMonitorDirName = "monitor"
    private const val cmdclickTempMonitorRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpMonitorDirName"
    val cmdclickTempMonitorDirPath = "$rootPath/$cmdclickTempMonitorRelativeDirPath"
    private const val cmdclickTmpUbuntuServiceDirName = "ubuntuService"
    private const val cmdclickTempUbuntuServiceRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpUbuntuServiceDirName"
    val cmdclickTempUbuntuServiceDirPath = "$rootPath/$cmdclickTempUbuntuServiceRelativeDirPath"
    const val cmdclickTmpUbuntuServiceActiveFileName = "isActinveUbuntuService.txt"
    const val cmdclickTmpUpdateMonitorFileName = "updateMonitor.txt"
    private const val cmdclickTempUpdateMonitorRelativeFilePath = "$cmdclickTempRelativePath/$cmdclickTmpUpdateMonitorFileName"
    val cmdclickTempUpdateMonitorFilePath = "$rootPath/$cmdclickTempUpdateMonitorRelativeFilePath"
    private const val cmdclickTmpCreateDirName = "create"
    private const val cmdclickTempCreateRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpCreateDirName"
    val cmdclickTempCreateDirPath = "$rootPath/$cmdclickTempCreateRelativeDirPath"
    private const val cmdclickTmpTextToSpeechDirName = "textToSpeech"
    private const val cmdclickTempTextToSpeechRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpTextToSpeechDirName"
    val cmdclickTempTextToSpeechDirPath = "$rootPath/$cmdclickTempTextToSpeechRelativeDirPath"
    private const val cmdclickTmpTextHtmlDirName = "txtHtml"
    private const val cmdclickTextHtmlRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpTextHtmlDirName"
    val cmdclickTextHtmlDirPath = "$rootPath/$cmdclickTextHtmlRelativeDirPath"
    const val cmdclickScrollPosiDirName = "scrollPosi"
    private const val cmdclickScrollPosiDirRelativePath = "$cmdclickTextHtmlRelativeDirPath/$cmdclickScrollPosiDirName"
    val cmdclickScrollPosiDirPath =  "$rootPath/$cmdclickScrollPosiDirRelativePath"
    private const val cmdclickAppDirAdminName = "AppDirAdmin"
    private const val cmdclickAppDirAdminRelativePath = "$cmdclickConfRelativePath/$cmdclickAppDirAdminName"
    val cmdclickAppDirAdminPath =  "$rootPath/$cmdclickAppDirAdminRelativePath"
    private const val cmdclickAppHistoryDirName = "AppHistoryDir"
    private const val cmdclickAppHistoryDirRelativePath = "$cmdclickConfRelativePath/$cmdclickAppHistoryDirName"
    val cmdclickAppHistoryDirAdminPath =  "$rootPath/$cmdclickAppHistoryDirRelativePath"
    val cmdclickButtonExecShellFileName = "cmdclickButtonExec$JS_FILE_SUFFIX"
    val cmdclickInternetButtonExecJsFileName = "internetButtonExec$JS_FILE_SUFFIX"
    private const val cmdclickCcimportDirName = "ccimport"
    private val cmdclickCcimportRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickCcimportDirName"
    val cmdclickCcimportDirPath =  "$rootPath/$cmdclickCcimportRelativeDirPath"
    private const val cmdclickNonitorDirName = "monitor"
    private val cmdclickMonitorRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickNonitorDirName"
    val cmdclickMonitorDirPath =  "$rootPath/$cmdclickMonitorRelativeDirPath"
    private const val cmdclickRepositoryDirName = "repository"
    private val cmdclickRepositoryRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickRepositoryDirName"
    val cmdclickRepositoryDirPath =  "$rootPath/$cmdclickRepositoryRelativeDirPath"
    private const val cmdclickFannelAppsDirName = "fannelApps"
    private val cmdclickFannelAppsRelativeDirPath = "$cmdclickRepositoryRelativeDirPath/$cmdclickFannelAppsDirName"
    val cmdclickFannelAppsDirPath =  "$rootPath/$cmdclickFannelAppsRelativeDirPath"
    private const val cmdclickFannelDirName = "fannel"
    private val cmdclickFannelRelativeDirPath = "$cmdclickFannelAppsRelativeDirPath/$cmdclickFannelDirName"
    val cmdclickFannelDirPath =  "$rootPath/$cmdclickFannelRelativeDirPath"

    private const val cmdclickFannelListDirName = "fannelList"
    private val cmdclickcmdclickFannelListDirNameRelativeDirPath = "$cmdclickRepositoryRelativeDirPath/$cmdclickFannelListDirName"
    val cmdclickFannelListDirPath =  "$rootPath/$cmdclickcmdclickFannelListDirNameRelativeDirPath"
    val fannelListMemoryName = "fannelListMemory"
    val fannelListMemoryPath = "$cmdclickFannelListDirPath/fannelListMemoryName"

    private const val cmdclickFannelItselfDirName = "fannel"
    private val cmdclickFannelItselfRelativeDirPath = "$cmdclickFannelAppsRelativeDirPath/$cmdclickFannelItselfDirName"
    val cmdclickFannelItselfDirPath =  "$rootPath/$cmdclickFannelItselfRelativeDirPath"
    const val RootDirPathByTermux = "\$HOME/storage/shared"
    private const val cmdclickMonitorFileNameSuffix = "term"
    const val cmdClickMonitorFileName_1 = "${cmdclickMonitorFileNameSuffix}_1"
    const val cmdClickMonitorFileName_2 = "${cmdclickMonitorFileNameSuffix}_2"
    const val cmdClickMonitorFileName_3 = "${cmdclickMonitorFileNameSuffix}_3"
    const val cmdClickMonitorFileName_4 = "${cmdclickMonitorFileNameSuffix}_4"
    private const val cmdclickAppDirParentName = "AppDir"
    private const val cmdclickAppDirRelativePath = "$cmdclickDirName/$cmdclickAppDirParentName"
    val cmdclickAppDirPath =  "$rootPath/$cmdclickAppDirRelativePath"
    const val cmdclickDefaultAppDirName = "default"
    private val cmdclickDefaultAppRelativePath = "$cmdclickAppDirRelativePath/$cmdclickDefaultAppDirName"
    val cmdclickDefaultAppDirPath =  "$rootPath/$cmdclickDefaultAppRelativePath"
    const val cmdclickSystemAppDirName = "system"
    private val cmdclickSystemAppRelativePath = "$cmdclickAppDirRelativePath/$cmdclickSystemAppDirName"
    val cmdclickSystemAppDirPath =  "$rootPath/$cmdclickSystemAppRelativePath"
    val cmdclickConfigFileName = "cmdclickConfig$JS_FILE_SUFFIX"
    private val cmdclickConfigFileRelativePath = "$cmdclickSystemAppRelativePath/$cmdclickConfigFileName"
    val cmdclickConfigFilePath = "$rootPath/$cmdclickConfigFileRelativePath"
    val cmdclickConfigFannelDirName =  "cmdclickConfigDir"
    private val cmdclickConfigFannelDirRelativePath =
        "$cmdclickSystemAppRelativePath/$cmdclickConfigFannelDirName"
    val cmdclickConfigFannelDirPath =
        "$rootPath/$cmdclickConfigFannelDirRelativePath"
    val homeFannelsFilePath =
        "\${01}/\${001}/settings/homeFannelsFilePaths.txt"
    val ccImportManagerFannelName = "ccImportManager.js"
    val ccImportAdminFannelPath = "$cmdclickSystemAppDirPath/$ccImportManagerFannelName"
    val appDirManagerFannelName = "appDirManager.js"
    val appDirManagerFannelPath = "$cmdclickSystemAppDirPath/$appDirManagerFannelName"


    private const val cmdclickSystemDirName = "system"
    private const val cmdclickUrlSystemDirName = "url"
    const val cmdclickUrlSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickUrlSystemDirName"
    private const val cmdclickScrollSystemDirName = "scroll"
    const val cmdclickScrollSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickScrollSystemDirName"
    private const val cmdclickHitSystemDirName = "hit"
    const val cmdclickHitSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickHitSystemDirName"
    private const val cmdclickTempSystemDirName = "temp"
    const val cmdclickTempSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickTempSystemDirName"
    val cmdclickUrlHistoryFileName = "cmdclickUrlHistory$tsvExtend"
    val cmdclickUrlHistoryBackupFileName = "cmdclickUrlBuckupHistory$tsvExtend"
    val cmdclickSiteScrollPosiFileName = "scrollPosi$tsvExtend"
    val cmdclickSiteScrollPosiBkFileName = "scrollBuckupPosi$tsvExtend"
    const val assetsFannelsReplacePrefix = "fannels/startUp"
    const val startUpFannelAsetsDirPath = assetsFannelsReplacePrefix
    const val startUpFannleAssetsPath = assetsFannelsReplacePrefix
    const val cmdclickStartupJsName = "cmdclickStartup.js"
    val cmdclickFirstHistoryTitle = "firstHistoryTitle$tsvExtend"
    val urlLoadFinished = "urlLoadFinished"
    val fannelDirSuffix = "Dir"
    val settingsDirPath = "\${01}/\${001}/settings"
    val homeScriptUrlsFilePath =  "$settingsDirPath/homeScriptUrlsPath.txt"
    val longPressMenuDirPath = "\${01}/\${001}/longPressMenuDir"
    val srcImageAnchorLongPressMenuFilePath =
        "$longPressMenuDirPath/srcImageAnchorLongPressMenu.txt"
    val srcAnchorLongPressMenuFilePath =
        "$longPressMenuDirPath/srcAnchorLongPressMenu.txt"
    val imageLongPressMenuFilePath =
        "$longPressMenuDirPath/imageLongPressMenu.txt"
    val noScrollSaveUrlsFilePath =
        "$settingsDirPath/noScrollSaveUrls.txt"
    val selectMenuFannelPath = "\${01}/selectMenu$JS_FILE_SUFFIX"
    val menuListDirPathForSelectMenuFannel = "\${01}/\${001}/menuList"
    val menuListFilePathForSelectMenu =
        "$menuListDirPathForSelectMenuFannel/menu.txt"
    val leftMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/leftMenuList$TXT_FILE_SUFFIX"
    val leftLongPressMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/leftLongPressMenuList$TXT_FILE_SUFFIX"
    val centerMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/centerMenuList$TXT_FILE_SUFFIX"
    val centerLongPressMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/centerLongPressMenuList$TXT_FILE_SUFFIX"
    val rightMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/rightMenuList$TXT_FILE_SUFFIX"
    val srcImageAnchorMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/srcImageAnchorMenuList$TXT_FILE_SUFFIX"
    val srcAnchorListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/srcAnchorMenuList$TXT_FILE_SUFFIX"
    val imageMenuListFilePathForWebSearher =
        "$menuListDirPathForSelectMenuFannel/imageMenuList$TXT_FILE_SUFFIX"
    fun makeOmitPath(
        path: String
    ): String {
        val pathList = path.split("/")
        return pathList.lastOrNull().toString()

    }


    fun makeTermuxPathByReplace(
        path: String
    ): String {
        return path.replace(
            rootPath,
            RootDirPathByTermux
        )
    }

    fun compExtend(
        path: String,
        extend: String
    ): String {
        if(
            path.endsWith(extend)
        ) return path
        return path + extend
    }

    fun compPrefix(
        path: String,
        prefix: String
    ): String {
        if(
            path.startsWith(prefix)
        ) return path
        return prefix + path
    }
}