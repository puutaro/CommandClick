package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
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
        context: Context?,
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
                    context,
                    qrLogoHandlerArgsMaker
                )
            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {}
            QrModeSettingKeysForQrDialog.QrMode.NORMAL ->
                setQrLogoForNormal(
                    context,
                    qrLogoHandlerArgsMaker
                )
        }
    }

    private fun setQrLogoForNormal(
        context: Context?,
        qrLogoHandlerArgsMaker: QrDialogConfig.QrLogoHandlerArgsMaker,
    ){
        val qrPngNameRelativePath = UsePath.qrPngRelativePath
        val fileName =  qrLogoHandlerArgsMaker.fileName
        val fileDirName = CcPathTool.makeFannelDirName(fileName)
        val parentDirPath =  UsePath.cmdclickDefaultAppDirPath
//        qrLogoHandlerArgsMaker.parentDirPath
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
        QrLogo.createAndSaveWithGitCloneOrFileCon(
            context,
//            parentDirPath,
            fileName,
            isFileCon,
        )?.let {
            qrLogoHandlerArgsMaker.fannelContentsQrLogoView?.setImageDrawable(it)
        }
    }
    private fun setQrLogoForFannelRepo(
        context: Context?,
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
//        val fragment = qrLogoHandlerArgsMaker.fragment
//        val parentDirPath = UsePath.cmdclickDefaultAppDirPath
//            qrLogoHandlerArgsMaker.parentDirPath
        QrLogo.createAndSaveWithGitCloneOrFileCon(
//            parentDirPath,
            context,
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

        fun setLayout(
            fragment: Fragment?,
            baseLinearLayoutCompat: LinearLayoutCompat?,
            materialCardView: MaterialCardView?,
            fileContentsQrLogoLinearLayout: RelativeLayout?,
            qrLogoConfigMap: Map<String, String>,
//            layoutType: LayoutSettingsForListIndex.LayoutTypeValueStr,
        ){
            val layoutParams =
                baseLinearLayoutCompat?.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            baseLinearLayoutCompat.requestLayout()
//            when(layoutType) {
//                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR
//                -> {}
//                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID
//                -> {
////                    val layoutParams =
////                        baseLinearLayoutCompat?.layoutParams as ViewGroup.MarginLayoutParams
////                    layoutParams.setMargins(0, 0, 0, 0)
////                    baseLinearLayoutCompat.requestLayout()
//                }
//            }
            RelativeLayout.LayoutParams.MATCH_PARENT
            val oneSideLength =  RelativeLayout.LayoutParams.MATCH_PARENT
//            when(layoutType) {
//                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR ->
//                culc(
//                    fragment?.context,
//                    qrLogoConfigMap
//                )
//                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID ->
//                    RelativeLayout.LayoutParams.MATCH_PARENT
//            }
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
            linearLayoutParam.apply {
                setMargins(0, 0, 0, 0)
            }
//            when(layoutType) {
//                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR ->
//                    linearLayoutParam.setMargins(10, 10, 0, 10)
//                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID ->
//                    linearLayoutParam.apply {
//                        setMargins(0, 0, 0, 0)
//                    }
//            }
            linearLayoutParam.gravity = Gravity.CENTER
            fileContentsQrLogoLinearLayout?.layoutParams = linearLayoutParam
            val cardViewLayoutParams = materialCardView?.layoutParams as ViewGroup.MarginLayoutParams
            cardViewLayoutParams.setMargins(1, 1, 1, 1)
            materialCardView.requestLayout()
            fileContentsQrLogoLinearLayout?.setPadding(
                0, 0, 0, 0
            )
//            when(layoutType) {
//                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR
//                -> {}
//                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID
//                -> {
//                    val cardViewLayoutParams = materialCardView?.layoutParams as ViewGroup.MarginLayoutParams
//                    cardViewLayoutParams.setMargins(1, 1, 1, 1)
//                    materialCardView.requestLayout()
//                    fileContentsQrLogoLinearLayout?.setPadding(
//                        0, 0, 0, 0
//                    )
//                }
//            }
        }
    }

//    object QrIconSettingKeysForQrDialog {
//
//        enum class QrIcon(val key: String){
//            NAME("name"),
//            NAME_CONFIG_PATH("nameConfigPath"),
//            COLOR("color"),
//            BACKGROUND_COLOR("bkColor"),
//            DISABLE("disable"),
//        }
//        private val disableOn = "ON"
//        enum class ImageMacro(
//            val str: String,
//            val id: Int,
//        ) {
//            IMAGE_PATH("imagePath", -10)
//        }
//
//        fun set(
//            context: Context?,
////            filterDir: String,
////            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
//            itemName: String,
//            fannelContentsQrLogoView: AppCompatImageView?,
//            fileContentsQrLogoLinearLayout: RelativeLayout?,
//            qrLogoConfigMap: Map<String, String>,
//            iconConfigMap: Map<String, String>,
//            itemNameToNameColorConfigMap: Map<String, String>?,
//            textImagePngBitMap: Bitmap,
//        ): Boolean {
//            if(
//                context == null
//                || fannelContentsQrLogoView == null
//                || qrLogoConfigMap.isEmpty()
//                || iconConfigMap.isEmpty()
//                || itemNameToNameColorConfigMap.isNullOrEmpty()
//            ) return false
//            if(
//                isDisable(iconConfigMap)
//            ) return false
//            val iconNameColorConfigMap = makeIconNameColorConfigMap(
//                itemName,
//                itemNameToNameColorConfigMap,
//            )
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "logo_icon.txt").absolutePath,
////                listOf(
////                    "iconConfigMap: ${iconConfigMap}",
////                    "iconNameConfigMap: ${itemNameToNameColorConfigMap}",
////                    "iconNameColorConfigMap: ${iconNameColorConfigMap}",
////                    "iconId: ${getIconId(
////                        iconNameColorConfigMap
////                    )}",
////                ).joinToString("\n\n\n")
////            )
//            val iconId =
//                getIconId(iconNameColorConfigMap)
//                    ?: return false
//            when(iconId){
//                ImageMacro.IMAGE_PATH.id -> setImagePath(
//                    context,
//                    fannelContentsQrLogoView,
//                    fileContentsQrLogoLinearLayout,
////                    listIndexTypeKey,
//                    qrLogoConfigMap,
////                    filterDir,
//                    itemName,
//                    textImagePngBitMap,
//                    iconNameColorConfigMap,
//                )
//                else -> setIconOrQr(
//                    context,
//                    fannelContentsQrLogoView,
//                    fileContentsQrLogoLinearLayout,
//                    iconNameColorConfigMap,
//                    iconId,
//                )
//            }
//            return true
//        }
//
//        private fun setImagePath(
//            context: Context,
//            fannelContentsQrLogoView: AppCompatImageView?,
//            fileContentsQrLogoLinearLayout: RelativeLayout?,
////            listIndexTypeKey: TypeSettingsForListIndex.ListIndexTypeKey,
//            qrLogoConfigMap: Map<String, String>,
////            filterDir: String,
//            itemName: String,
//            textImagePngBitMap: Bitmap,
//            iconNameColorConfigMap: Map<String, String>?
//        ) {
//            val itemPath = itemName
////            when(listIndexTypeKey){
//////                TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
//////                -> return
////                TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
////                -> itemName
////                TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
////                -> File(filterDir, itemName).absolutePath
////            }
////            fannelContentsQrLogoView?.load(
////                itemPath
////            )
//            val maxHeight = culc(
//                context,
//                qrLogoConfigMap
//            )
//            val myBitmap = makeBitMap(
//                itemPath,
//                maxHeight,
//                textImagePngBitMap
//            )
//
//            fannelContentsQrLogoView?.setImageBitmap(myBitmap)
////            getIconColor(iconNameColorConfigMap).let {
////                fannelContentsQrLogoView?.imageTintList =
////                    context.getColorStateList(it)
////            }
//            getBkColor(
//                iconNameColorConfigMap
//            ).let {
//                fileContentsQrLogoLinearLayout?.backgroundTintList =
//                    context.getColorStateList(it)
//            }
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "logo.txt").absolutePath,
////                listOf(
////                    "qrLogoConfigMap: ${qrLogoConfigMap}",
////                    "macheigt: ${maxHeight}",
////                    "listIndexTypeKey: ${listIndexTypeKey.key}",
////                    "filterDir: ${filterDir}",
////                    "itemName: ${itemName}",
////                    "itemPath: ${itemPath}",
////                    "itemPath.isFile: ${File(itemPath).isFile}",
////                ).joinToString("\n\n")
////            )
//            return
//        }

//        private fun makeBitMap(
//            itemPath: String,
//            maxHeight: Int,
//            textImagePngBitMap: Bitmap,
//        ): Bitmap {
//            val myBitmap: Bitmap = try {
//                BitmapFactory.decodeFile(itemPath)
//            } catch (e: Exception){
//                textImagePngBitMap
//            }
//            if(
//                myBitmap.height <= maxHeight
//            ) return myBitmap
//            val resizeBitmap = BitmapTool.resizeByMaxHeight(
//                myBitmap,
//                maxHeight.toDouble(),
//            )
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "log_bitmap.txt").absolutePath,
////                listOf(
////                    "resizeBitmap: ${resizeBitmap.height}"
////                ).joinToString("\n\n")
////            )
//            return resizeBitmap
//        }

//        private fun setIconOrQr(
//            context: Context,
//            fannelContentsQrLogoView: AppCompatImageView?,
//            fileContentsQrLogoLinearLayout: RelativeLayout?,
//            iconNameColorConfigMap: Map<String, String>?,
//            iconId: Int,
//        ){
//            fannelContentsQrLogoView?.load(
//                AppCompatResources.getDrawable(context, iconId)
//            )
//            getIconColor(iconNameColorConfigMap).let {
//                fannelContentsQrLogoView?.imageTintList =
//                    context.getColorStateList(it)
//            }
//            getBkColor(
//                iconNameColorConfigMap
//            ).let {
//                fileContentsQrLogoLinearLayout?.backgroundTintList =
//                    context.getColorStateList(it)
//            }
//        }


//        private fun isDisable(
//            iconConfigMap: Map<String, String>
//        ): Boolean {
//            val disableKey = QrIcon.DISABLE.key
//            if(
//                iconConfigMap.isEmpty()
//            ) return false
//            return iconConfigMap.get(disableKey) == disableOn
//        }

//        private fun makeIconNameColorConfigMap(
//            itemName: String,
//            itemNameToIconConfigMap: Map<String, String>?,
//        ): Map<String, String>? {
//            if(
//                itemNameToIconConfigMap.isNullOrEmpty()
//            ) return null
//            val mapSrc =
//                itemNameToIconConfigMap.get(itemName)
//                    ?: itemNameToIconConfigMap.get(IconNameConfigKey.DEFAULT.key)
//            if(
//                mapSrc.isNullOrEmpty()
//            ) return null
//            return CmdClickMap.createMap(
//                mapSrc,
//                ','
//            ).toMap()
//        }
//        private fun getIconId(
//            iconNameColorConfigMap: Map<String, String>?,
//        ): Int? {
//            if(
//                iconNameColorConfigMap.isNullOrEmpty()
//            ) return null
//            val nameKey = QrIcon.NAME.key
//            val iconName =
//                iconNameColorConfigMap.get(nameKey)
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "logo_get_iconId").absolutePath,
////                listOf(
////                    "itemname: ${iconNameColorConfigMap.get(nameKey)}",
////                    "default: ${iconNameColorConfigMap.get(IconNameConfigKey.DEFAULT.key)}",
////                    "iconName: ${iconName}",
////                ).joinToString("\n\n\n")
////            )
//            if(
//                iconName.isNullOrEmpty()
//            ) return null
//            val imageStrToIdList = CmdClickIcons.values().map {
//                it.str to it.id
//            } + ImageMacro.values().map {
//                it.str to it.id
//            }
//            return imageStrToIdList.firstOrNull {
//                val imageMacroStr = it.first
//                imageMacroStr == iconName
//            }?.second
//        }

//        private fun getIconColor(
//            iconNameColorConfigMap: Map<String, String>?,
//        ): Int {
//            val defaultColor = CmdClickColor.LIGHT_GREEN.id
//            if(
//                iconNameColorConfigMap.isNullOrEmpty()
//            ) return defaultColor
//            val colorKey = QrIcon.COLOR.key
//            val colorName =
//                iconNameColorConfigMap.get(colorKey)
//            return CmdClickColor.values().firstOrNull {
//                it.str == colorName
//            }?.id ?: defaultColor
//        }

//        private fun getBkColor(
//            iconNameColorConfigMap: Map<String, String>?,
//        ): Int {
//            val defaultColor = CmdClickColor.LIGHT_GREEN.id
//            if(
//                iconNameColorConfigMap.isNullOrEmpty()
//            ) return defaultColor
//            val bkColorKey = QrIcon.BACKGROUND_COLOR.key
//            val colorName =
//                iconNameColorConfigMap.get(bkColorKey)
//            return CmdClickColor.values().firstOrNull {
//                it.str == colorName
//            }?.id ?: defaultColor
//        }


//        fun makeIconConfigMap(
//            qrLogoConfigMap: Map<String, String>,
//        ): Map<String, String> {
//            val iconKeyName = QrLogoSettingKey.ICON.key
//            return CmdClickMap.createMap(
//                qrLogoConfigMap.get(iconKeyName),
//                '?'
//            ).toMap()
//        }

//        fun makeIconNameConfigMap(
//            setReplaceVariableMap: Map<String, String>?,
//            fannelInfoMap: Map<String, String>?,
//            iconConfigMap: Map<String, String>,
//        ): Map<String, String> {
//            val nameConfigPathKey = QrIcon.NAME_CONFIG_PATH.key
//            val iconNameConfigPath = iconConfigMap.get(nameConfigPathKey)
//            if(
//                !iconNameConfigPath.isNullOrEmpty()
//            ) {
////                val fannelInfoMap = editFragment.fannelInfoMap
////                val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(fannelInfoMap)
//                val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
//                val iconNameMapTsvCon =
//                    SetReplaceVariabler.execReplaceByReplaceVariables(
//                        ReadText(iconNameConfigPath).readText(),
//                        setReplaceVariableMap,
////                        currentAppDirPath,
//                        currentFannelName,
//                    )
//                return CmdClickMap.createMapFromTsv(
//                    iconNameMapTsvCon,
//                    '\n'
//                ).toMap()
//            }
//            val iconConfigMapStr = iconConfigMap.map {
//                    "${it.key}=${it.value}"
//            }.joinToString(",")
//            return mapOf(
//                    IconNameConfigKey.DEFAULT.key to iconConfigMapStr
//            )
//
//        }

//        enum class IconNameConfigKey(
//            val key: String
//        ){
//            DEFAULT("default"),
//        }
//    }

    private fun culc(
        context: Context?,
        qrLogoSettingMap: Map<String, String>,
    ): Int {
        val defaultOnsideLengthSrc = 100
        val defaultOneSideLength = ScreenSizeCalculator.toDp(
            context,
            defaultOnsideLengthSrc
        )
        val oneSideLengthKeyName = QrLogoSettingKey.ONE_SIDE_LENGTH.key
        if(
            qrLogoSettingMap.isEmpty()
        ) return defaultOneSideLength
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "logo_oneside.txt").absolutePath,
//            listOf(
//                "qrLogoSettingMap: ${qrLogoSettingMap}",
//                "defaultOneSideLength: ${defaultOneSideLength}",
//                "onesidelength: ${qrLogoSettingMap.get(oneSideLengthKeyName).let {
//                    if(
//                        it.isNullOrEmpty()
//                    ) return@let defaultOneSideLength
//                    try {
//                        ScreenSizeCalculator.toDp(
//                            context,
//                            it.toFloat()
//                        )
//                    } catch (e: Exception){
//                        defaultOneSideLength
//                    }
//                }}"
//
//                ).joinToString("\n\n")
//        )
        return qrLogoSettingMap.get(oneSideLengthKeyName).let {
            if(
                it.isNullOrEmpty()
            ) return@let defaultOneSideLength
            try {
                ScreenSizeCalculator.toDp(
                    context,
                    it.toFloat()
                )
            } catch (e: Exception){
                defaultOneSideLength
            }
        }
    }
}