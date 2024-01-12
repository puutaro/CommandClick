package com.puutaro.commandclick.proccess.qr

import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Map.CmdClickMap
import com.puutaro.commandclick.util.Map.ConfigMapTool
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object QrDialogConfig {

    enum class QrDialogConfigKey(
        val key: String
    ) {
        CLICK("click"),
        LONG_CLICK("longClick"),
        LOGO("logo"),
    }

    enum class QrLogoSettingKey(
        val key: String
    ){
        ONE_SIDE_LENGTH("oneSideLength"),
        ON_FANNEL_REPO_LOGO_MODE("onFannelRepoLogoMode"),
        ON_FILE_CON("onFileCon"),
    }

    enum class ClickModeValues(
        val mode: String
    ){
        FILE_CONTENTS("con"),
        EXEC_QR("execQR"),
        DESC("desc"),
        EDIT_LOGO("editLogo")
    }

    fun makeDialogConfigMap(
        readSharePreffernceMap: Map<String, String>
    ): Map<String, String> {
        return ConfigMapTool.create(
            UsePath.qrDialogConfigPath,
            String(),
            readSharePreffernceMap
        ) ?: mapOf()
    }

    fun makeLogoConfigMap(
        qrDialogConfigMap: Map<String, String>
    ): Map<String, String> {
        return qrDialogConfigMap.get(QrDialogConfigKey.LOGO.key).let {
            if(
                it.isNullOrEmpty()
            ) return@let emptyMap()
            CmdClickMap.createMap(
                it,
                "|"
            ).toMap()
        }
    }

    fun howFileConQr(
        logoConfigMap: Map<String, String>,
    ): Boolean {
        val onFileConKeyName = QrLogoSettingKey.ON_FILE_CON.key
        if(
            logoConfigMap.isEmpty()
        ) return false
        return logoConfigMap.containsKey(onFileConKeyName)
    }

    private fun howFannelRepoQrMode(
        logoConfigMap: Map<String, String>,
    ): Boolean {
        val onFannelLepoQrModeKeyName = QrLogoSettingKey.ON_FANNEL_REPO_LOGO_MODE.key
        if(
            logoConfigMap.isEmpty()
        ) return false
        return logoConfigMap.containsKey(onFannelLepoQrModeKeyName)
    }

    fun howEnableClick(
        clickKey: String,
        qrDialogConfigMap: Map<String, String>,
    ): Boolean {
        return qrDialogConfigMap.get(clickKey).let {
            if(it == null) return@let true
            if(
                it.isEmpty()
            ) return@let false
            true
        }
    }

    fun setOneSideLength(
        imageView: AppCompatImageView?,
        qrLogoConfigMap: Map<String, String>
    ){
        val oneSideLength = decideOneSideLength(qrLogoConfigMap)
        imageView?.layoutParams?.height = oneSideLength
        imageView?.layoutParams?.width = oneSideLength
    }

    fun setQrLogoHandler(
        fragment: Fragment,
        readSharePreffernceMap: Map<String, String>,
        qrLogoConfigMap: Map<String, String>,
        parentDirPath: String,
        fileName: String,
        fannelContentsQrLogoView: AppCompatImageView?,
    ){
        if(
            fileName.isEmpty()
            || fileName == "-"
        ) return
        val isFannelRepoQrMode = howFannelRepoQrMode(qrLogoConfigMap)
        when(isFannelRepoQrMode){
            true -> setQrLogoForFannelRepo(
                fragment,
                readSharePreffernceMap,
                parentDirPath,
                fileName,
                fannelContentsQrLogoView,
            )
            else -> setQrLogoForNormal(
                fragment,
                qrLogoConfigMap,
                parentDirPath,
                fileName,
                fannelContentsQrLogoView,
            )
        }
    }

    private fun setQrLogoForNormal(
        fragment: Fragment,
        qrLogoConfigMap: Map<String, String>,
        parentDirPath: String,
        fileName: String,
        fannelContentsQrLogoView: AppCompatImageView?,
    ){
        val qrPngNameRelativePath = UsePath.qrPngRelativePath
        val fileDirName = CcPathTool.makeFannelDirName(fileName)
        val fileDirPath = "${parentDirPath}/${fileDirName}"
        val qrPngPath = "${fileDirPath}/${qrPngNameRelativePath}"
        val qrPngPathObj = File(qrPngPath)
        if(qrPngPathObj.isFile){
            fannelContentsQrLogoView?.load(qrPngPath)
            return
        }
        val isFileCon = howFileConQr(qrLogoConfigMap)
        QrLogo(fragment).createAndSaveWithGitCloneOrFileCon(
            parentDirPath,
            fileName,
            isFileCon,
        )?.let {
            fannelContentsQrLogoView?.setImageDrawable(it)
        }
    }
    private fun setQrLogoForFannelRepo(
        fragment: Fragment,
        readSharePreffernceMap: Map<String, String>,
        parentDirPath: String,
        fannelName: String,
        fannelContentsQrLogoView: AppCompatImageView?,
    ){
        val qrPngNameRelativePath = UsePath.qrPngRelativePath
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val qrPngPathObjInInstallIndex =
            File(
                "${UsePath.cmdclickFannelDirPath}/${fannelDirName}/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInInstallIndex.isFile
        ) {
            fannelContentsQrLogoView?.load(qrPngPathObjInInstallIndex.absolutePath)
            return
        }
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val qrPngPathObjInCurrentAppDir =
            File(
                "${currentAppDirPath}/$fannelDirName/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInCurrentAppDir.isFile
        ) {
            fannelContentsQrLogoView?.load(qrPngPathObjInCurrentAppDir.absolutePath)
            return
        }
        QrLogo(fragment).createAndSaveWithGitCloneOrFileCon(
            parentDirPath,
            fannelName,
            false
        )?.let {
            fannelContentsQrLogoView?.setImageDrawable(it)
        }
    }

    private fun decideOneSideLength(
        qrLogoSettingKey: Map<String, String>,
    ): Int {
        val defaultOneSideLength = 150
        val oneSideLengthKeyName = QrLogoSettingKey.ONE_SIDE_LENGTH.key
        if(
            qrLogoSettingKey.isEmpty()
        ) return defaultOneSideLength
        return qrLogoSettingKey.get(oneSideLengthKeyName).let {
            if(it.isNullOrEmpty()) return@let defaultOneSideLength
            try { it.toInt() } catch (e: Exception){ defaultOneSideLength }
        }
    }
}