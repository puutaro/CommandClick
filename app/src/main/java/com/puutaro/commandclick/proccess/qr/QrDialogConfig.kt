package com.puutaro.commandclick.proccess.qr

import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrLogoSettingsForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrModeSettingKeysForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrTypeSettingsForQrDialog
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object QrDialogConfig {

    enum class QrDialogConfigKey(
        val key: String
    ) {
        CLICK("click"),
        LONG_CLICK("longClick"),
        LOGO("logo"),
        MODE("mode"),
    }

    fun makeClickConfigListStr(
        qrDialogConfigMap: Map<String, String>,
        clickKeyName: String,
    ): String? {
        return qrDialogConfigMap.get(clickKeyName)
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
                '|'
            ).toMap()
        }
    }

    fun howDisableQrLogo(
        logoConfigMap: Map<String, String>,
    ): Boolean {
        val disableQrKeyName =
            QrLogoSettingsForQrDialog.QrLogoSettingKey.DISABLE.key
        if(
            logoConfigMap.isEmpty()
        ) return false
        val disableQrValue =
            logoConfigMap.get(disableQrKeyName)
        if(
            disableQrValue.isNullOrEmpty()
        ) return false
        return disableQrValue ==
                QrLogoSettingsForQrDialog.QrDisableSettingKey.ON.key
    }

    fun howQrType(
        logoConfigMap: Map<String, String>,
    ): String {
        val defaultQrType = QrTypeSettingsForQrDialog.QrTypeSettingKey.GIT_CLONE.type
        val typeKeyName = QrLogoSettingsForQrDialog.QrLogoSettingKey.TYPE.key
        if(
            logoConfigMap.isEmpty()
        ) return defaultQrType
        val qrType = logoConfigMap.get(typeKeyName)
        if(
            qrType.isNullOrEmpty()
        ) return defaultQrType
        return qrType
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
        fragment: Fragment,
        fileContentsQrLogoLinearLayout: LinearLayoutCompat?,
        qrLogoConfigMap: Map<String, String>
    ){
        val oneSideLength = decideOneSideLength(
            fragment,
            qrLogoConfigMap
        )
        val linearLayoutParam = LinearLayoutCompat.LayoutParams(
            oneSideLength,
            oneSideLength
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "oneSide.txt").absolutePath,
//            listOf(
//                "qrLogoConfigMap: ${qrLogoConfigMap}",
//                "oneSideLength: ${oneSideLength}",
//            ).joinToString("\n\n")
//        )
        linearLayoutParam.setMargins(10, 10, 0, 10)
        linearLayoutParam.gravity = Gravity.CENTER
        fileContentsQrLogoLinearLayout?.layoutParams = linearLayoutParam
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
        val qrMode = QrModeSettingKeysForQrDialog.getQrMode(
            qrLogoConfigMap
        )
        when(qrMode){
            QrModeSettingKeysForQrDialog.QrMode.FANNEL_REPO ->
                setQrLogoForFannelRepo(
                    qrLogoHandlerArgsMaker
                )
            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {}
            QrModeSettingKeysForQrDialog.QrMode.NORMAL ->
                setQrLogoForNormal(
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
                    QrTypeSettingsForQrDialog.QrTypeSettingKey.FILE_CON.type
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
        fragment: Fragment,
        qrLogoSettingKey: Map<String, String>,
    ): Int {
        val defaultOnsideLengthSrc = 100
        val defaultOneSideLength = ScreenSizeCalculator.toDp(
            fragment.context,
            defaultOnsideLengthSrc
        )
        val oneSideLengthKeyName = QrLogoSettingsForQrDialog.QrLogoSettingKey.ONE_SIDE_LENGTH.key
        if(
            qrLogoSettingKey.isEmpty()
        ) return defaultOneSideLength
        return qrLogoSettingKey.get(oneSideLengthKeyName).let {
            if(
                it.isNullOrEmpty()
            ) return@let defaultOneSideLength
            try {
                ScreenSizeCalculator.toDp(
                    fragment.context,
                    it.toFloat()
                )
//                it.toInt()
            } catch (e: Exception){
                defaultOneSideLength
            }
        }
    }
}