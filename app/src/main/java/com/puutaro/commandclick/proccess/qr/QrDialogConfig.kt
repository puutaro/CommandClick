package com.puutaro.commandclick.proccess.qr

import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.ConfigMapTool
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
        DISABLE("disable"),
        TYPE("type"),
    }


    enum class QrTypeSettingKey(
        val type: String
    ){
        FILE_CON("fileCon"),
        GIT_CLONE("gitCone"),
    }
    enum class QrDisableSettingKey(
        val key: String
    ) {
        ON("ON"),
        OFF("OFF"),
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

    fun howDisableQrLogo(
        logoConfigMap: Map<String, String>,
    ): Boolean {
        val disableQrKeyName =
            QrLogoSettingKey.DISABLE.key
        if(
            logoConfigMap.isEmpty()
        ) return false
        val disableQrValue =
            logoConfigMap.get(disableQrKeyName)
        if(
            disableQrValue.isNullOrEmpty()
        ) return false
        return disableQrValue ==
                QrDisableSettingKey.OFF.key
    }

    fun howQrType(
        logoConfigMap: Map<String, String>,
    ): String {
        val defaultQrType = QrTypeSettingKey.GIT_CLONE.type
        val typeKeyName = QrLogoSettingKey.TYPE.key
        if(
            logoConfigMap.isEmpty()
        ) return defaultQrType
        val qrType = logoConfigMap.get(typeKeyName)
        if(
            qrType.isNullOrEmpty()
        ) return defaultQrType
        return qrType
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

    class QrLogoHandlerArgsMaker(
        val fragment: Fragment,
        val recentAppDirPath: String,
        val readSharePreffernceMap: Map<String, String>,
        val qrLogoConfigMap: Map<String, String>,
        val parentDirPath: String,
        val fileName: String,
        val fannelContentsQrLogoView: AppCompatImageView?,
    )

    fun setQrLogoHandler(
        qrLogoHandlerArgsMaker: QrLogoHandlerArgsMaker,
    ){
        val fileName = qrLogoHandlerArgsMaker.fileName
        val qrLogoConfigMap = qrLogoHandlerArgsMaker.qrLogoConfigMap
        if(
            fileName.isEmpty()
            || fileName == "-"
        ) return
        val isFannelRepoQrMode = howFannelRepoQrMode(qrLogoConfigMap)
        when(isFannelRepoQrMode){
            true -> setQrLogoForFannelRepo(
                qrLogoHandlerArgsMaker
            )
            else -> setQrLogoForNormal(
                qrLogoHandlerArgsMaker
            )
        }
    }

    private fun setQrLogoForNormal(
        qrLogoHandlerArgsMaker: QrLogoHandlerArgsMaker,
    ){
        val qrPngNameRelativePath = UsePath.qrPngRelativePath
        val fileName =  qrLogoHandlerArgsMaker.fileName
        val fileDirName = CcPathTool.makeFannelDirName(fileName)
        val parentDirPath =  qrLogoHandlerArgsMaker.parentDirPath
        val fileDirPath = "${parentDirPath}/${fileDirName}"
        val qrPngPath = "${fileDirPath}/${qrPngNameRelativePath}"
        val qrPngPathObj = File(qrPngPath)
        if(qrPngPathObj.isFile){
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.load(qrPngPath)
            return
        }
        val qrLogoConfigMap = qrLogoHandlerArgsMaker.qrLogoConfigMap
        val isFileCon =
            howQrType(qrLogoConfigMap) ==
                    QrTypeSettingKey.FILE_CON.type
        val fragment = qrLogoHandlerArgsMaker.fragment
        QrLogo(fragment).createAndSaveWithGitCloneOrFileCon(
            parentDirPath,
            fileName,
            isFileCon,
        )?.let {
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.setImageDrawable(it)
        }
    }
    private fun setQrLogoForFannelRepo(
        qrLogoHandlerArgsMaker: QrLogoHandlerArgsMaker,
    ){
        val qrPngNameRelativePath = UsePath.qrPngRelativePath
        val fannelName = qrLogoHandlerArgsMaker.fileName
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val qrPngPathObjInInstallIndex =
            File(
                "${UsePath.cmdclickFannelDirPath}/${fannelDirName}/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInInstallIndex.isFile
        ) {
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.load(qrPngPathObjInInstallIndex.absolutePath)
            return
        }
        val currentAppDirPath = qrLogoHandlerArgsMaker.recentAppDirPath
        val qrPngPathObjInCurrentAppDir =
            File(
                "${currentAppDirPath}/$fannelDirName/${qrPngNameRelativePath}"
            )
        if(
            qrPngPathObjInCurrentAppDir.isFile
        ) {
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.load(qrPngPathObjInCurrentAppDir.absolutePath)
            return
        }
        val fragment = qrLogoHandlerArgsMaker.fragment
        val parentDirPath = qrLogoHandlerArgsMaker.parentDirPath
        QrLogo(fragment).createAndSaveWithGitCloneOrFileCon(
            parentDirPath,
            fannelName,
            false
        )?.let {
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.setImageDrawable(it)
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