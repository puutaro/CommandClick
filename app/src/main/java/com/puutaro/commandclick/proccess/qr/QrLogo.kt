package com.puutaro.commandclick.proccess.qr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.style.Color
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.FannelIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Random

object QrLogo {
    private val logoList = FannelIcons.values().map { it.id }
    private val logListSize = logoList.size
    private val qrPngRelativePath = UsePath.qrPngRelativePath
    private val twoThreeLogListSize = (logoList.size * 2) / 3
    private var usedLogoIndexList = mutableListOf<Int>()
    private val qrNewLine = "cmdclickQRNewLine"
    private val maxQrConLength = 500

    fun toBitMapWrapper(
        qrLogoDrawable: Drawable,
        oneSideLength: Int = 1000
    ): Bitmap? {
        return qrLogoDrawable.toBitmapOrNull(
            oneSideLength,
            oneSideLength
        )
    }

    fun setTitleFannelLogo(
        fragment: Fragment,
        titleImageView: AppCompatImageView?,
//        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = fragment.context
            ?: return
        if(
            titleImageView == null
        ) return
//        val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
        val logoPngPath = listOf(
            UsePath.fannelLogoPngPath,
        ).joinToString("/").let {
            ScriptPreWordReplacer.replace(
                it,
                selectedScriptName
            )
        }
//            "${UsePath.cmdclickDefaultAppDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
        if(!File(logoPngPath).isFile) return

        val isEditExecute = checkEditExecute(
//            currentAppDirPath,
            selectedScriptName,
        )
        titleImageView.setPadding(2, 2,2,2)
        titleImageView.background = if(isEditExecute) {
            AppCompatResources.getDrawable(context, R.color.terminal_color)
        } else AppCompatResources.getDrawable(context, R.color.fannel_icon_color)
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        Glide
            .with(context)
            .load(logoPngPath)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(titleImageView)
//        titleImageView.load(logoPngPath)
    }

    fun createAndSaveWithGitCloneOrFileCon(
        context: Context?,
//        currentAppDirPath: String,
        fannelName: String,
        isFileCon: Boolean,
    ): Drawable? {
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}"
        val qrDesignFilePath = "${fannelDirPath}/${UsePath.qrDesignRelativePath}"
        val fannelRawName = CcPathTool.makeFannelRawName(fannelName)
        val qrContents = when(isFileCon) {
            true -> ReadText(
                File(
                    cmdclickDefaultAppDirPath,
                    fannelName
                ).absolutePath
            ).readText().take(maxQrConLength)
            else ->
                QrMapper.onGitTemplate.format(fannelRawName)
        }
        val qrDesignMap = createNewDesignMap(
            qrDesignFilePath,
            qrContents,
        )
        return createAndSaveFromDesignMap(
            context,
            qrDesignMap,
//            currentAppDirPath,
            fannelName,
        )
    }

    fun createMonochrome(
        fragment: Fragment,
        qrSrcStr: String,
    ): Drawable? {
        val context = fragment.context ?: return null
        try {
            val data = QrData.Url(qrSrcStr)
            val qrColor = ContextCompat.getColor(context, R.color.terminal_color)
            val options = createQrVectorOptions {
                padding = .075f
//                    .125f
                background {
                    drawable =
                        ContextCompat
                            .getDrawable(
                                context,
                                R.color.white
                            )
                }
                logo {
                    drawable = ContextCompat
                        .getDrawable(context, R.drawable.copy)?.apply {
                            setTint(
                                qrColor
                            )
                        }
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape.Circle
                    background {
                        QrVectorColor.Solid(
                            qrColor
                        )
                    }
                }
                colors {
                    dark = QrVectorColor.Solid(
                        qrColor
                    )
                    ball = QrVectorColor.Solid(
                        qrColor
                    )
                    frame = QrVectorColor.Solid(
                        qrColor
                    )
                }
                shapes {
                    darkPixel = QrVectorPixelShape
                        .RoundCorners(.5f)
                    ball = QrVectorBallShape
                        .RoundCorners(.25f)
                    frame = QrVectorFrameShape
                        .RoundCorners(.25f)
                }
            }
            return QrCodeDrawable(data, options)
        } catch(e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }
        return null
    }

    private fun createFromQrDesignMap(
       context: Context?,
        qrDesignMap: Map<String, String>,
    ): Drawable? {
        if(context == null) return null
        val rnd = Random(System.currentTimeMillis())
        val contents = getQrDesignFileKey(
            QrDesignFileKey.CONTENTS.key,
            qrDesignMap
        )
        if(
            contents.isNullOrEmpty()
        ) return null

        val logoIndex =
            getQrDesignFileKey(
                QrDesignFileKey.LOGO_INDEX.key,
                qrDesignMap
            ).let {
            if(
                it.isNullOrEmpty()
            ) return@let decideLogoIndex(rnd)
            try { it.toInt() } catch (e: Exception){ decideLogoIndex(rnd)}
        }
        val logoColor = getDarkColor(
            rnd,
            qrDesignMap,
            QrDesignFileKey.LOGO_COLOR.key,
        )
        val squareColor = getDarkColor(
            rnd,
            qrDesignMap,
            QrDesignFileKey.SQUARE_COLOR.key,
        )
        try {
            val data = QrData.Url(contents)

            val options = createQrVectorOptions {
                padding = .075f
//                    .125f
                background {
                    drawable =
                        ContextCompat
                            .getDrawable(
                                context,
                                R.color.white
                            )
                }
                logo {
                    drawable = ContextCompat
                        .getDrawable(context, logoList[logoIndex])?.apply {
                            setTint(logoColor)
                        }
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape.Circle
                    background {
                        QrVectorColor.Solid(
                            ContextCompat.getColor(context, R.color.terminal_color)
                        )
                    }
                }
                colors {
                    dark = QrVectorColor.Solid(
                        ContextCompat.getColor(context, R.color.terminal_color)
                    )
                    ball = QrVectorColor.Solid(squareColor)
                    frame = QrVectorColor.Solid(
                        ContextCompat.getColor(context, R.color.terminal_color)
                    )
                }
                shapes {
                    darkPixel = QrVectorPixelShape
                        .RoundCorners(.5f)
                    ball = QrVectorBallShape
                        .RoundCorners(.25f)
                    frame = QrVectorFrameShape
                        .RoundCorners(.25f)
                }
            }
            return QrCodeDrawable(data, options)
        } catch(e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }
        return null
    }

    fun createAndSaveFromDesignMap(
        context: Context?,
        qrDesignMap: Map<String, String>,
//        currentAppDirPath: String,
        fannelName: String,
    ): Drawable? {
        return qrDrawableSave(
            context,
            createFromQrDesignMap(
                context,
                qrDesignMap
            ),
//            currentAppDirPath,
            fannelName,
        )
    }

    private fun qrDrawableSave(
        context: Context?,
        qrDrawableSrc: Drawable?,
//        currentAppDirPath: String,
        fannelName: String,
    ): Drawable? {
        try{
            val qrDrawable = qrDrawableSrc
                ?: return null
            val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
                    val qrPngPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}/$qrPngRelativePath"
                    val qrBitMap = toBitMapWrapper(qrDrawable)
                        ?: return@withContext
                    FileSystems.savePngFromBitMap(
                        qrPngPath,
                        qrBitMap
                    )
                }
            }
            return qrDrawable
        } catch(e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }
        return null
    }

    private fun decideLogoIndex(
        rnd: Random
    ): Int {
        var entryIndex = 0
        for(i in (1..50)) {
            entryIndex = rnd.nextInt(logListSize)
            if(!usedLogoIndexList.contains(entryIndex)) {
                usedLogoIndexList.add(entryIndex)
                trimUsedLogoList()
                break
            }
        }
        return entryIndex
    }

    private fun trimUsedLogoList(){
        if(usedLogoIndexList.size < twoThreeLogListSize) return
        usedLogoIndexList = usedLogoIndexList.filterIndexed {
            index, _ -> index <= twoThreeLogListSize
        }.toMutableList()
    }

    private fun checkEditExecute(
//        currentAppDirPath: String,
        selectedScriptName: String,
    ): Boolean {
        val scriptContentsList = ReadText(
            File(
                UsePath.cmdclickDefaultAppDirPath,
                selectedScriptName
            ).absolutePath,
        ).textToList()
        val editExecuteAlwaysStr = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        val isEditExecuteForJs = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
//            LanguageTypeSelects.JAVA_SCRIPT
        ) == editExecuteAlwaysStr
        val isEditExecuteForShell = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
//            LanguageTypeSelects.SHELL_SCRIPT
        ) == editExecuteAlwaysStr
        return isEditExecuteForJs || isEditExecuteForShell
    }

    private fun getDarkColor(
        rnd: Random,
        qrDesignMap: Map<String, String>,
        qrDesignFileKey: String,
    ): Int {
        return getQrDesignFileKey(
            qrDesignFileKey,
            qrDesignMap
        ).let {
            if(
                it.isNullOrEmpty()
            ) return@let decideDarkColor(rnd)
            try { it.toInt() } catch (e: Exception){
                decideDarkColor(rnd)
            }
        }
    }

    private fun decideDarkColor(
        rnd: Random
    ): Int {
        return Color(
            255,
            rnd.nextInt(150),
            rnd.nextInt(150),
            rnd.nextInt(150)
        )
    }

    fun readQrDesignMapWithCreate(
        qrDesignFilePath: String,
//        currentAppDirPath: String,
        fannelName: String,
    ): Map<String, String> {
        return readQrDesignMap(qrDesignFilePath).let {
            when(it.isNotEmpty()) {
                true -> updateDesignMapWithCon(
                    it,
                    qrDesignFilePath,
//                    currentAppDirPath,
                    fannelName,
                )
                else -> createConDesignMap(
                    qrDesignFilePath,
//                    currentAppDirPath,
                    fannelName,
                )
            }
        }
    }

    private fun updateDesignMapWithCon(
        qrDesignMap: Map<String, String>,
        qrDesignFilePath: String,
//        currentAppDirPath: String,
        fannelName: String,
    ): Map<String, String> {
        val contentsKeyName = QrDesignFileKey.CONTENTS.key
        val updateQrDesignMap = qrDesignMap.map {
            val currentKeyName = it.key
            when(currentKeyName == contentsKeyName) {
                true -> {
                    currentKeyName to ReadText(
                        File(
                            UsePath.cmdclickDefaultAppDirPath,
                            fannelName
                        ).absolutePath,
                    ).readText()
                }
                false ->
                    currentKeyName to it.value
            }
        }.toMap()
        saveQrDesignMap(
            qrDesignFilePath,
            updateQrDesignMap,
        )
        return updateQrDesignMap
    }

    private fun createConDesignMap(
        qrDesignFilePath: String,
//        currentAppDirPath: String,
        fannelName: String,
    ): Map<String, String> {
        val rnd = Random(System.currentTimeMillis())
        val newQrDesignMap = makeQrDesignMap(
            decideLogoIndex(rnd).toString(),
            decideDarkColor(rnd).toString(),
            decideDarkColor(rnd).toString(),
            ReadText(
                File(
                    UsePath.cmdclickDefaultAppDirPath,
                    fannelName
                ).absolutePath,
            ).readText()
        )
        saveQrDesignMap(
            qrDesignFilePath,
            newQrDesignMap,
        )
        return newQrDesignMap
    }

    private fun createNewDesignMap(
        qrDesignFilePath: String,
        qrContents: String,
    ): Map<String, String> {
        val rnd = Random(System.currentTimeMillis())
        val newQrDesignMap = makeQrDesignMap(
            decideLogoIndex(rnd).toString(),
            decideDarkColor(rnd).toString(),
            decideDarkColor(rnd).toString(),
            qrContents
        )
        saveQrDesignMap(
            qrDesignFilePath,
            newQrDesignMap,
        )
        return newQrDesignMap
    }

    private fun readQrDesignMap(
        qrDesignFilePath: String,
    ): Map<String, String> {
        return ReadText(
            qrDesignFilePath
        ).readText().let {
            CmdClickMap.createMap(
                it,
                '\n'
            )
        }.toMap().filterKeys { it.isNotEmpty() }
//            .split("\n").map {
//            CcScript.makeKeyValuePairFromSeparatedString(
//                it,
//                "="
//            )
//        }.toMap().filterKeys { it.isNotEmpty() }
    }

    private fun makeQrDesignMap(
        logoIndexIntStr: String,
        logoColorIntStr: String,
        squareColorIntStr: String,
        qrContents: String,
    ): Map<String, String>{
        return mapOf(
            QrDesignFileKey.LOGO_INDEX.key to logoIndexIntStr,
            QrDesignFileKey.LOGO_COLOR.key to logoColorIntStr,
            QrDesignFileKey.SQUARE_COLOR.key to squareColorIntStr,
            QrDesignFileKey.CONTENTS.key to qrContents,
        )
    }

    private fun saveQrDesignMap(
        qrDesignFilePath: String,
        qrDesignMap: Map<String, String>
    ){
        FileSystems.writeFile(
            qrDesignFilePath,
            qrDesignMap.map {
                "${it.key}=${it.value.replace("\n", qrNewLine)}"
            }.joinToString("\n")
        )
    }

    private fun getQrDesignFileKey(
        key: String,
        qrDesignMap: Map<String, String>
    ): String? {
        return qrDesignMap.get(key)
            ?.replace(qrNewLine, "\n")

    }
}

enum class QrDesignFileKey(
    val key: String
) {
    LOGO_INDEX("logo_index"),
    LOGO_COLOR("logo_color"),
    SQUARE_COLOR("square_color"),
    CONTENTS("con"),
}