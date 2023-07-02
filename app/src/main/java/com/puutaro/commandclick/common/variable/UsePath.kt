package com.puutaro.commandclick.common.variable

import android.os.Environment


class UsePath {
    companion object {
        private val tsvExtend = ".tsv"
        private val rootPath = Environment.getExternalStorageDirectory().absolutePath +
                "/" + Environment.DIRECTORY_DOCUMENTS
        private const val cmdclickDirName = "cmdclick"
        val cmdclickDirPath = "$rootPath/$cmdclickDirName"
        private const val cmdclickConfDirName = "conf"
        private const val cmdclickConfRelativePath = "$cmdclickDirName/$cmdclickConfDirName"
        val cmdclickConfDirPath =  "$rootPath/$cmdclickConfRelativePath"
        private const val cmdclickTempDirName = "temp"
        private const val cmdclickTempRelativePath = "$cmdclickDirName/$cmdclickTempDirName"
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
        val cmdclickButtonExecShellFileName = "cmdclickButtonExec${CommandClickScriptVariable.JS_FILE_SUFFIX}"
        val cmdclickInternetButtonExecJsFileName = "internetButtonExec${CommandClickScriptVariable.JS_FILE_SUFFIX}"
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

        private const val cmdclickFannelListDirName = "fannelList"
        private val cmdclickcmdclickFannelListDirNameRelativeDirPath = "$cmdclickRepositoryRelativeDirPath/$cmdclickFannelListDirName"
        val cmdclickFannelListDirPath =  "$rootPath/$cmdclickcmdclickFannelListDirNameRelativeDirPath"
        val fannelListMemoryName = "fannelListMemory"
        val fannelListMemoryPath = "${cmdclickFannelListDirPath}/fannelListMemoryName"

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
        private const val cmdclickAppDirRelativePath = "${cmdclickDirName}/${cmdclickAppDirParentName}"
        val cmdclickAppDirPath =  "$rootPath/$cmdclickAppDirRelativePath"
        const val cmdclickDefaultAppDirName = "default"
        private val cmdclickDefaultAppRelativePath = "$cmdclickAppDirRelativePath/$cmdclickDefaultAppDirName"
        val cmdclickDefaultAppDirPath =  "$rootPath/$cmdclickDefaultAppRelativePath"
        const val cmdclickSystemAppDirName = "system"
        private val cmdclickSystemAppRelativePath = "$cmdclickAppDirRelativePath/$cmdclickSystemAppDirName"
        val cmdclickSystemAppDirPath =  "$rootPath/$cmdclickSystemAppRelativePath"
        val cmdclickConfigFileName = "cmdclickConfig${CommandClickScriptVariable.JS_FILE_SUFFIX}"
        private val cmdclickConfigFileRelativePath = "$cmdclickSystemAppRelativePath/$cmdclickConfigFileName"
        val cmdclickConfigFilePath =  "$rootPath/$cmdclickConfigFileRelativePath"
        val ccImportManagerFannelName = "ccImportManager.js"
        val ccImportAdminFannelPath = "${cmdclickSystemAppDirPath}/${ccImportManagerFannelName}"
        val appDirManagerFannelName = "appDirManager.js"
        val appDirManagerFannelPath = "${cmdclickSystemAppDirPath}/${appDirManagerFannelName}"
        private const val cmdclickSystemDirName = "system"
        private const val cmdclickUrlSystemDirName = "url"
        const val cmdclickUrlSystemDirRelativePath = "${cmdclickSystemDirName}/${cmdclickUrlSystemDirName}"
        private const val cmdclickHitSystemDirName = "hit"
        const val cmdclickHitSystemDirRelativePath = "${cmdclickSystemDirName}/${cmdclickHitSystemDirName}"
        val cmdclickUrlHistoryFileName = "cmdclickUrlHistory${tsvExtend}"
        val cmdclickUrlHistoryBackupFileName = "cmdclickUrlBuckupHistory${tsvExtend}"
        const val cmdclickStartupJsName = "cmdclick_startup.js"
        const val longPressSrcImageAnchorJsName = "long_press_src_image_anchor.js"
        const val longPressSrcAnchorJsName = "long_press_src_anchor.js"
        const val longPressImageAnchorJsName = "long_press_image_anchor.js"
        val cmdclickFirstHistoryTitle = "firstHistoryTitle${tsvExtend}"
        val urlLoadFinished = "urlLoadFinished"
        val fannelDirSuffix = "Dir"


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
}