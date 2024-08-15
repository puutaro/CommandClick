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
    val emulatedPath = Environment.getExternalStorageDirectory().absolutePath
    val rootPath = emulatedPath +
            "/" + Environment.DIRECTORY_DOCUMENTS
    const val cmdclickDirName = "cmdclick"
    val cmdclickDirPath = "$rootPath/$cmdclickDirName"
    private const val cmdclickConfDirName = "conf"
    private const val cmdclickConfRelativePath = "$cmdclickDirName/$cmdclickConfDirName"
    val cmdclickConfDirPath =  "$rootPath/$cmdclickConfRelativePath"
    private const val cmdclickTempDirName = "temp"
    private const val cmdclickTempRelativePath = "$cmdclickDirName/$cmdclickTempDirName"
    private const val cmdclickTmpPickerDirName = "picker"
    private const val cmdclickTempPickerRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpPickerDirName"
    val cmdclickTempPickerDirPath = "$rootPath/$cmdclickTempPickerRelativeDirPath"
    private const val cmdclickTmpTmpDirName = "temp"
    private const val cmdclickTempTmpRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpTmpDirName"
    val cmdclickTempTmpDirPath = "$rootPath/$cmdclickTempTmpRelativeDirPath"
    val cmdclickPastPickerDirMemoryName = "pastPickerDirMemory.txt"
    private const val cmdclickTmpDownloadDirName = "download"
    private const val cmdclickTempDownloadRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpDownloadDirName"
    val cmdclickTempDownloadDirPath = "$rootPath/$cmdclickTempDownloadRelativeDirPath"
    private const val cmdclickTmpMonitorDirName = "monitor"
    private const val cmdclickTempMonitorRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpMonitorDirName"
    val cmdclickTempMonitorDirPath = "$rootPath/$cmdclickTempMonitorRelativeDirPath"
    private const val cmdclickTmpCmdDirName = "cmd"
    private const val cmdclickTempCmdRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpCmdDirName"
    val cmdclickTempCmdDirPath = "$rootPath/$cmdclickTempCmdRelativeDirPath"
    val cmdclickTempCmdShellName = "cmd.sh"
    private const val cmdclickTmpProcessDirName = "process"
    private const val cmdclickTempProcessRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpProcessDirName"
    val cmdclickTempProcessDirPath = "$rootPath/$cmdclickTempProcessRelativeDirPath"
    val cmdclickTempProcessesTxt = "process.txt"
    private const val cmdclickTmpUbuntuServiceDirName = "ubuntuService"
    private const val cmdclickTempUbuntuServiceRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpUbuntuServiceDirName"
    val cmdclickTempUbuntuServiceDirPath = "$rootPath/$cmdclickTempUbuntuServiceRelativeDirPath"
    const val cmdclickTmpUbuntuMonitorOff = "isMonitorOff.txt"
    const val cmdclickTmpUpdateMonitorFileName = "updateMonitor.txt"
    private const val cmdclickTmpFileTransferServiceDirName = "fileUploadService"
    private const val cmdclickTempFileTransferServiceRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpFileTransferServiceDirName"
    val cmdclickTempFileUploadServiceDirPath = "$rootPath/$cmdclickTempFileTransferServiceRelativeDirPath"
    const val uploadServiceAcceptTimeTxtName = "acceptTime.txt"
    private const val cmdclickTmpCreateDirName = "create"
    private const val cmdclickTempCreateRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpCreateDirName"
    val cmdclickTempCreateDirPath = "$rootPath/$cmdclickTempCreateRelativeDirPath"
    private const val cmdclickTmpMediaPlayerDirName = "mediaPlayer"
    private const val cmdclickTempMediaPlayerRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpMediaPlayerDirName"
    val cmdclickTempMediaPlayerDirPath = "$rootPath/$cmdclickTempMediaPlayerRelativeDirPath"
    val mediaPlayerServiceConfigPath = "${cmdclickTempMediaPlayerDirPath}/config.txt"
    val mediaPlayerServiceStreamingDirName = "streaming"
    val mediaPlayerServiceStreamingDirPath = "${cmdclickTempMediaPlayerDirPath}/${mediaPlayerServiceStreamingDirName}"
    val mediaPlayerServiceStreamingPreloadDirName = "preload"
    val mediaPlayerServiceStreamingPreloadDirPath = "${mediaPlayerServiceStreamingDirPath}/${mediaPlayerServiceStreamingPreloadDirName}"
    val mediaPlayerServiceStreamingPreloadTxtPath = "${mediaPlayerServiceStreamingPreloadDirPath}/preload.txt"
    val mediaPlayerServiceStreamingShellDirName = "shell"
    val mediaPlayerServiceStreamingShellDirPath = "${mediaPlayerServiceStreamingDirPath}/${mediaPlayerServiceStreamingShellDirName}"
    private const val mediaPlayerServiceStreamingShellOutDirName = "shellOut"
    val mediaPlayerServiceStreamingShellOutDirPath = "$mediaPlayerServiceStreamingDirPath/$mediaPlayerServiceStreamingShellOutDirName"
    private const val mediaPlayerServiceStreamingPreloadShellDirName = "preloadShell"
    val mediaPlayerServiceStreamingPreloadShellDirPath = "$mediaPlayerServiceStreamingDirPath/$mediaPlayerServiceStreamingPreloadShellDirName"
    private const val mediaPlayerServiceStreamingPreloadShellOutDirName = "preloadShellOut"
    val mediaPlayerServiceStreamingPreloadShellOutDirPath = "$mediaPlayerServiceStreamingDirPath/$mediaPlayerServiceStreamingPreloadShellOutDirName"
    private const val cmdclickTmpTextToSpeechDirName = "textToSpeech"
    private const val cmdclickTempTextToSpeechRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpTextToSpeechDirName"
    val cmdclickTempTextToSpeechDirPath = "$rootPath/$cmdclickTempTextToSpeechRelativeDirPath"
    private const val cmdclickTmpDebugDirName = "debug"
    private const val cmdclickTempDebugRelativeDirPath = "$cmdclickTempRelativePath/$cmdclickTmpDebugDirName"
    const val execJsDebugName = "jsDebug.txt"
    private const val jsDebugRelativePath = "${cmdclickTempDebugRelativeDirPath}/${execJsDebugName}"
    val jsDebugReportPath = "${rootPath}/${jsDebugRelativePath}"
    const val execJsSrcAcDebugName = "jsSrcAcDebug.txt"
    private const val jsSrcAcDebugRelativePath = "${cmdclickTempDebugRelativeDirPath}/${execJsSrcAcDebugName}"
    val jsSrcAcDebugReportPath = "${rootPath}/${jsSrcAcDebugRelativePath}"
    const val execJsGenAcDebugName = "jsGenAcDebug.txt"
    private const val jsGenAcDebugRelativePath = "${cmdclickTempDebugRelativeDirPath}/${execJsGenAcDebugName}"
    val jsGenAcDebugReportPath = "${rootPath}/${jsGenAcDebugRelativePath}"
    const val execSysDebugFileName = "sysDebug.txt"
    private const val sysDebugRelativePath = "${cmdclickTempDebugRelativeDirPath}/${execSysDebugFileName}"
    val editDebugLogPath = "${rootPath}/${sysDebugRelativePath}"
    private const val jsDebuggerMapTxtName = "jsDebuggerMap.txt"
    private const val jsDebuggerMapTxtRelativePath = "${cmdclickTempDebugRelativeDirPath}/${jsDebuggerMapTxtName}"
    val jsDebuggerMapTxtPath = "${rootPath}/${jsDebuggerMapTxtRelativePath}"
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
    private const val cmdclickJsImportDirName = "jsimport"
    private val cmdclickJsImportRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickJsImportDirName"
    val cmdclickJsImportDirPath =  "$rootPath/$cmdclickJsImportRelativeDirPath"
    private const val cmdclickSharePrefDirName = "sharePref"
    private val cmdclickSharePrefRelativeDirPath = "$cmdclickConfRelativePath/$cmdclickSharePrefDirName"
    val cmdclickSharePrefDirPath =  "$rootPath/$cmdclickSharePrefRelativeDirPath"
    private val sdRootDirTxtName = "sdRootDirPath.txt"
    val sdRootDirTxtPath = "${cmdclickSharePrefDirPath}/${sdRootDirTxtName}"
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
    private val cmdclickFannelListDirNameRelativeDirPath = "$cmdclickRepositoryRelativeDirPath/$cmdclickFannelListDirName"
    val cmdclickFannelListDirPath =  "$rootPath/$cmdclickFannelListDirNameRelativeDirPath"
    val fannelListMemoryName = "fannelListMemory"
    val fannelListMemoryPath = "$cmdclickFannelListDirPath/fannelListMemoryName"

    private const val cmdclickFannelItselfDirName = "fannel"
    private val cmdclickFannelItselfRelativeDirPath = "$cmdclickFannelAppsRelativeDirPath/$cmdclickFannelItselfDirName"
    val cmdclickFannelItselfDirPath =  "$rootPath/$cmdclickFannelItselfRelativeDirPath"
    const val rootDirPathByTermux = "\$HOME/storage/shared"
    private const val cmdclickMonitorFileNameSuffix = "monitor"
    const val cmdClickMonitorFileName_1 = "${cmdclickMonitorFileNameSuffix}_1"
    const val cmdClickMonitorFileName_2 = "${cmdclickMonitorFileNameSuffix}_2"
    const val cmdClickMonitorFileName_3 = "${cmdclickMonitorFileNameSuffix}_3"
    const val cmdClickMonitorFileName_4 = "${cmdclickMonitorFileNameSuffix}_4"
    private const val cmdclickUbuntuDirParentName = "ubuntu"
    private const val cmdclickUbuntuDirRelativePath = "$cmdclickDirName/$cmdclickUbuntuDirParentName"
    val cmdclickUbuntuDirPath =  "$rootPath/$cmdclickUbuntuDirRelativePath"
    private const val cmdclickBackupDirParentName = "backup"
    val cmdclickUbuntuBackupDirPath =  "$cmdclickUbuntuDirPath/$cmdclickBackupDirParentName"
    private const val cmdclickBackupTempDirName = "temp"
    val cmdclickUbuntuBackupTempDirPath =  "$cmdclickUbuntuBackupDirPath/$cmdclickBackupTempDirName"
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
    private val settingVariablesDirName = "settingVariables"
    val fannelSettingVariablsDirPath = "\${01}/\${001}/${settingVariablesDirName}"
    val replaceVariablesTsv = "replaceVariablesTable.tsv"
    val hideSettingVariablesConfig = "hideSettingVariables.js"
    val setReplaceVariablesConfig = "setReplaceVariables.js"
    val setVariableTypesConfig = "setVariableTypes.js"
    val ignoreHistoryPathsConfig = "ignoreHistoryPaths.js"
    val replaceVariablesTsvRelativePath = "$settingVariablesDirName/$replaceVariablesTsv"
    val fannelSettingsDirPath = "\${01}/\${001}/settings"
    val homeFannelsFilePath =
        "${fannelSettingsDirPath}/homeFannelsFilePaths.txt"
    val settingImagesDirName = "settingImages"
    val fannelSettingImagesDirPath = "\${01}/\${001}/${settingImagesDirName}"
    val saveWebConDialogFannelName = "saveWebConDialog.js"
    val savePageUrlDialogFannelName = "savePageUrlDialog.js"
    val saveGmailConDialogFannelName = "saveGmailConDialog.js"
    val fannelReadmeName = "README.md"
    val fannelReadmePath = "\${01}/\${001}/${fannelReadmeName}"


    private const val cmdclickSystemDirName = "system"
    private const val cmdclickJsSystemDirName = "js"
    const val cmdclickJsSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickJsSystemDirName"
    private const val cmdclickAppSystemDirName = "app"
    const val cmdclickAppSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickAppSystemDirName"
    const val cmdclickAppSystemDirPath = "\${01}/${cmdclickAppSystemDirRelativePath}"
    private const val cmdclickUrlSystemDirName = "url"
    const val cmdclickUrlSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickUrlSystemDirName"
    private const val cmdclickQrSystemDirName = "qr"
    const val cmdclickQrSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickQrSystemDirName"
    private const val cmdclickScrollSystemDirName = "scroll"
    const val cmdclickScrollSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickScrollSystemDirName"
    private const val cmdclickHitSystemDirName = "hit"
    const val cmdclickHitSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickHitSystemDirName"
    private const val cmdclickTempSystemDirName = "temp"
    const val cmdclickTempSystemDirRelativePath = "$cmdclickSystemDirName/$cmdclickTempSystemDirName"
    val cmdclickUrlHistoryFileName = "cmdclickUrlHistory$tsvExtend"
    val cmdclickUrlHistoryBackupFileName = "cmdclickUrlBuckupHistory$tsvExtend"
    val cmdclickQrHistoryFileName = "cmdclickQrHistory$tsvExtend"
    val cmdclickQrHistoryBackupFileName = "cmdclickQrBuckupHistory$tsvExtend"
    val cmdclickSiteScrollPosiFileName = "scrollPosi$tsvExtend"
    val cmdclickSiteScrollPosiBkFileName = "scrollBuckupPosi$tsvExtend"
    val cmdclickMonitorScrollPosiFileName = "monitorScrollPosi$tsvExtend"
    const val cmdclickPreferenceJsName = "preference.js"
    val urlLoadFinished = "urlLoadFinished"
    val fannelDirSuffix = "Dir"
    private const val fannelStateStockFileName = "fannelStateStock.tsv"
    val fannelStateStockFilePath =  "$fannelSettingsDirPath/${fannelStateStockFileName}"
    private const val fannelStateRootTableFileName = "fannelStateRootTable.tsv"
    val fannelStateRootTableFilePath =  "$fannelSettingsDirPath/${fannelStateRootTableFileName}"
    private val homeScriptUrlsFileName = "homeScriptUrlsPath.txt"
    val homeScriptUrlsFilePath =  "$fannelSettingsDirPath/${homeScriptUrlsFileName}"
    private val listIndexForEditConfigName = "listIndexConfig.js"
    val listIndexForEditConfigPath =  "$fannelSettingsDirPath/${listIndexForEditConfigName}"
    private val qrDialogConfigName = "qrDialogConfig.js"
    val qrDialogConfigPath =  "$fannelSettingsDirPath/${qrDialogConfigName}"
    private val settingButtonConfigName = "settingButtonConfig.js"
    val settingButtonConfigPath =  "$fannelSettingsDirPath/${settingButtonConfigName}"
    private val playButtonConfigName = "playButtonConfig.js"
    val playButtonConfigPath =  "$fannelSettingsDirPath/${playButtonConfigName}"
    private val extraButtonConfigName = "extraButtonConfig.js"
    val extraButtonConfigPath =  "$fannelSettingsDirPath/${extraButtonConfigName}"
    private val editButtonConfigName = "editButtonConfig.js"
    val editButtonConfigPath =  "$fannelSettingsDirPath/${editButtonConfigName}"
    private val fannelStateConfigName = "fannelStateConfig.js"
    val fannelStateConfigPath =  "$fannelSettingsDirPath/${fannelStateConfigName}"
    private val editBoxTitleConfig = "editBoxTitleConfig.js"
    val editTitleConfigPath =  "$fannelSettingsDirPath/${editBoxTitleConfig}"
    private val importDisableValList = "importDisableValList.js"
    val importDisableValListPath =  "$fannelSettingsDirPath/${importDisableValList}"
    val longPressMenuDirPath = "\${01}/\${001}/longPressMenuDir"
    val srcImageAnchorLongPressMenuFilePath =
        "$longPressMenuDirPath/srcImageAnchorLongPressMenu.txt"
    val srcAnchorLongPressMenuFilePath =
        "$longPressMenuDirPath/srcAnchorLongPressMenu.txt"
    val imageLongPressMenuFilePath =
        "$longPressMenuDirPath/imageLongPressMenu.txt"
    val noScrollSaveUrlsFilePath =
        "$fannelSettingsDirPath/noScrollSaveUrls.txt"
    val selectMenuFannelPath = "\${01}/selectMenu$JS_FILE_SUFFIX"
    val systemExecJsDirName = "systemJs"
    val appHistoryClickJsName = "appHistoryClick.js"
    val externalExecJsDirName = "externalJs"
    val externalJsForExecFannel = "externalExec.js"
    val qrDirName = "qr"
    val qrPngName = "qr.png"
    val qrPngRelativePath = "$qrDirName/$qrPngName"
    val qrDesignFileName = "qrDesign.txt"
    val qrDesignRelativePath = "$qrDirName/$qrDesignFileName"

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
            rootDirPathByTermux
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
            || prefix.isEmpty()
        ) return path
        return prefix + path.replaceFirstChar { it.uppercase() }
    }

    fun decideMonitorName(
        monitorNum: Int
    ): String {
        return when(monitorNum){
            1 -> cmdClickMonitorFileName_1
            2 -> cmdClickMonitorFileName_2
            3 -> cmdClickMonitorFileName_3
            4 -> cmdClickMonitorFileName_4
            else -> cmdClickMonitorFileName_1
        }
    }
}