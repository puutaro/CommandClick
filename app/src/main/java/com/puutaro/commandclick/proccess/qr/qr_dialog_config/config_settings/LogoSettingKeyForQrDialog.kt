package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

import android.content.Context
import android.view.Gravity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object QrLogoSettingsForQrDialog {
    enum class QrLogoSettingKey(
        val key: String
    ){
        ONE_SIDE_LENGTH("oneSideLength"),
        DISABLE("disable"),
        TYPE("type"),
        ICON("icon")
    }

    fun makeLogoConfigMap(
        qrDialogConfigMap: Map<String, String>
    ): Map<String, String> {
        return qrDialogConfigMap.get(QrDialogConfig.QrDialogConfigKey.LOGO.key).let {
            if(
                it.isNullOrEmpty()
            ) return@let emptyMap()
            CmdClickMap.createMap(
                it,
                '|'
            ).toMap()
        }
    }

    fun setQrLogoHandler(
        qrLogoHandlerArgsMaker: QrDialogConfig.QrLogoHandlerArgsMaker,
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
        qrLogoHandlerArgsMaker: QrDialogConfig.QrLogoHandlerArgsMaker,
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
            Type.how(qrLogoConfigMap) ==
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
        qrLogoHandlerArgsMaker: QrDialogConfig.QrLogoHandlerArgsMaker,
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


    object Type {
        fun how(
            logoConfigMap: Map<String, String>,
        ): String {
            val defaultQrType = QrTypeSettingsForQrDialog.QrTypeSettingKey.GIT_CLONE.type
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
    }

    object Disable {

        private enum class QrDisableSettingKey(
            val key: String
        ) {
            ON("ON"),
        }
        fun how(
            logoConfigMap: Map<String, String>,
        ): Boolean {
            if(
                logoConfigMap.isEmpty()
            ) return false
            val disableQrKeyName =
                QrLogoSettingKey.DISABLE.key
            val disableQrValue =
                logoConfigMap.get(disableQrKeyName)
            if(
                disableQrValue.isNullOrEmpty()
            ) return false
            return disableQrValue ==
                    QrDisableSettingKey.ON.key
        }
    }

    object OneSideLength {

        fun set(
            fragment: Fragment,
            fileContentsQrLogoLinearLayout: LinearLayoutCompat?,
            qrLogoConfigMap: Map<String, String>
        ){
            val oneSideLength = culc(
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
        private fun culc(
            fragment: Fragment,
            qrLogoSettingMap: Map<String, String>,
        ): Int {
            val defaultOnsideLengthSrc = 100
            val defaultOneSideLength = ScreenSizeCalculator.toDp(
                fragment.context,
                defaultOnsideLengthSrc
            )
            val oneSideLengthKeyName = QrLogoSettingKey.ONE_SIDE_LENGTH.key
            if(
                qrLogoSettingMap.isEmpty()
            ) return defaultOneSideLength
            return qrLogoSettingMap.get(oneSideLengthKeyName).let {
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

    object QrIconSettingKeysForQrDialog {

        enum class QrIcon(val key: String){
            NAME("name"),
            NAME_CONFIG_PATH("nameConfigPath"),
            COLOR("color"),
            BACKGROUND_COLOR("bkColor"),
            DISABLE("disable"),
        }
        private val disableOn = "ON"

        fun set(
            context: Context?,
            itemName: String,
            fannelContentsQrLogoView: AppCompatImageView?,
            fileContentsQrLogoLinearLayout: LinearLayoutCompat?,
            qrLogoConfigMap: Map<String, String>,
            iconConfigMap: Map<String, String>,
            itemNameToNameColorConfigMap: Map<String, String>?,
        ): Boolean {
            if(
                context == null
                || fannelContentsQrLogoView == null
                || qrLogoConfigMap.isEmpty()
                ||  iconConfigMap.isEmpty()
                || itemNameToNameColorConfigMap.isNullOrEmpty()
            ) return false
            if(
                isDisable(iconConfigMap)
            ) return false
            val iconNameColorConfigMap = makeIconNameColorConfigMap(
                itemName,
                itemNameToNameColorConfigMap,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "logo_icon.txt").absolutePath,
//                listOf(
//                    "iconConfigMap: ${iconConfigMap}",
//                    "iconNameConfigMap: ${itemNameToNameColorConfigMap}",
//                    "iconNameColorConfigMap: ${iconNameColorConfigMap}",
//                    "iconId: ${getIconId(
//                        iconNameColorConfigMap
//                    )}",
//                ).joinToString("\n\n\n")
//            )
            val iconId =
                getIconId(iconNameColorConfigMap)
                    ?: return false
            fannelContentsQrLogoView.load(
                AppCompatResources.getDrawable(context, iconId)
            )
            getIconColor(iconNameColorConfigMap).let {
                fannelContentsQrLogoView.imageTintList =
                    context.getColorStateList(it)
            }
            getBkColor(
                iconNameColorConfigMap
            ).let {
                fileContentsQrLogoLinearLayout?.backgroundTintList =
                    context.getColorStateList(it)
            }
            return true
        }


        private fun isDisable(
            iconConfigMap: Map<String, String>
        ): Boolean {
            val disableKey = QrIcon.DISABLE.key
            if(
                iconConfigMap.isEmpty()
            ) return false
            return iconConfigMap.get(disableKey) == disableOn
        }

        private fun makeIconNameColorConfigMap(
            itemName: String,
            itemNameToIconConfigMap: Map<String, String>?,
        ): Map<String, String>? {
            if(
                itemNameToIconConfigMap.isNullOrEmpty()
            ) return null
            val mapSrc =
                itemNameToIconConfigMap.get(itemName)
                    ?: itemNameToIconConfigMap.get(IconNameConfigKey.DEFAULT.key)
            if(
                mapSrc.isNullOrEmpty()
            ) return null
            return CmdClickMap.createMap(
                mapSrc,
                ','
            ).toMap()
        }
        private fun getIconId(
            iconNameColorConfigMap: Map<String, String>?,
        ): Int? {
            if(
                iconNameColorConfigMap.isNullOrEmpty()
            ) return null
            val nameKey = QrIcon.NAME.key
            val iconName =
                iconNameColorConfigMap.get(nameKey)
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "logo_get_iconId").absolutePath,
//                listOf(
//                    "itemname: ${iconNameColorConfigMap.get(nameKey)}",
//                    "default: ${iconNameColorConfigMap.get(IconNameConfigKey.DEFAULT.key)}",
//                    "iconName: ${iconName}",
//                ).joinToString("\n\n\n")
//            )
            if(
                iconName.isNullOrEmpty()
            ) return null
            return CmdClickIcons.values().firstOrNull {
                it.str == iconName
            }?.id
        }

        private fun getIconColor(
            iconNameColorConfigMap: Map<String, String>?,
        ): Int {
            val defaultColor = CmdClickColor.LIGHT_GREEN.id
            if(
                iconNameColorConfigMap.isNullOrEmpty()
            ) return defaultColor
            val colorKey = QrIcon.COLOR.key
            val colorName =
                iconNameColorConfigMap.get(colorKey)
            return CmdClickColor.values().firstOrNull {
                it.str == colorName
            }?.id ?: defaultColor
        }

        private fun getBkColor(
            iconNameColorConfigMap: Map<String, String>?,
        ): Int {
            val defaultColor = CmdClickColor.LIGHT_GREEN.id
            if(
                iconNameColorConfigMap.isNullOrEmpty()
            ) return defaultColor
            val bkColorKey = QrIcon.BACKGROUND_COLOR.key
            val colorName =
                iconNameColorConfigMap.get(bkColorKey)
            return CmdClickColor.values().firstOrNull {
                it.str == colorName
            }?.id ?: defaultColor
        }


        fun makeIconConfigMap(
            qrLogoConfigMap: Map<String, String>,
        ): Map<String, String> {
            val iconKeyName = QrLogoSettingKey.ICON.key
            return CmdClickMap.createMap(
                qrLogoConfigMap.get(iconKeyName),
                '?'
            ).toMap()
        }

        fun makeIconNameConfigMap(
           editFragment: EditFragment,
            iconConfigMap: Map<String, String>,
        ): Map<String, String> {
            val nameConfigPathKey = QrIcon.NAME_CONFIG_PATH.key
            val iconNameConfigPath = iconConfigMap.get(nameConfigPathKey)
            if(
                !iconNameConfigPath.isNullOrEmpty()
            ) {
                val readSharePreferenceMap = editFragment.readSharePreferenceMap
                val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(readSharePreferenceMap)
                val currentFannelName = SharePrefTool.getCurrentFannelName(readSharePreferenceMap)
                val iconNameMapTsvCon =
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        ReadText(iconNameConfigPath).readText(),
                        editFragment.setReplaceVariableMap,
                        currentAppDirPath,
                        currentFannelName,
                    )
                return CmdClickMap.createMapFromTsv(
                    iconNameMapTsvCon,
                    '\n'
                ).toMap()
            }
            val iconConfigMapStr = iconConfigMap.map {
                    "${it.key}=${it.value}"
            }.joinToString(",")
            return mapOf(
                    IconNameConfigKey.DEFAULT.key to iconConfigMapStr
            )

        }

        enum class IconNameConfigKey(
            val key: String
        ){
            DEFAULT("default"),
        }
    }
}