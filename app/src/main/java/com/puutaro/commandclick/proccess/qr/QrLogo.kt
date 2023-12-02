package com.puutaro.commandclick.proccess.qr

import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.Fragment
import coil.load
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
import com.puutaro.commandclick.common.variable.icon.FannelIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Random

class QrLogo(
    private val fragment: Fragment,
) {

    private val logoList = FannelIcons.values().map { it.id }
    private val logListSize = logoList.size
    private val qrPngRelativePath = UsePath.qrPngRelativePath
    private val twoThreeLogListSize = (logoList.size * 2) / 3
    private var usedLogoIndexList = mutableListOf<Int>()


    fun setTitleQrLogo(
        titleImageView: AppCompatImageView?,
        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = fragment.context
            ?: return
        if(
            titleImageView == null
        ) return
        val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
        val qrLogoPath = "$currentAppDirPath/$fannelDirName/${UsePath.qrPngRelativePath}"
        if(!File(qrLogoPath).isFile) return

        val isEditExecute = checkEditExecute(
            currentAppDirPath,
            selectedScriptName,
        )
        titleImageView.setPadding(2, 2,2,2)
        titleImageView.background = if(isEditExecute) {
            AppCompatResources.getDrawable(context, R.color.terminal_color)
        } else AppCompatResources.getDrawable(context, R.color.fannel_icon_color)
        titleImageView.load(qrLogoPath)
    }

    fun createMonochrome(
        qrSrcStr: String,
    ): Drawable? {
        val context = fragment.context ?: return null
        val rnd = Random(System.currentTimeMillis())
        val insertLogoIndex =  decideLogoIndex(rnd)
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
                        .getDrawable(context, com.termux.shared.R.drawable.ic_copy)?.apply {
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
            LogSystems.stdErr(e.toString())
        }
        return null
    }

    fun create(
        qrSrcStr: String,
    ): Drawable? {
        val context = fragment.context ?: return null
        val rnd = Random(System.currentTimeMillis())
        val insertLogoIndex =  decideLogoIndex(rnd)
        try {
            val data = QrData.Url(qrSrcStr)

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
                        .getDrawable(context, logoList[insertLogoIndex])?.apply {
                            setTint(
                                Color(
                                    255,
                                    rnd.nextInt(150),
                                    rnd.nextInt(150),
                                    rnd.nextInt(150)
                                )
                            )
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
                val ballFrameColor = Color(
                    255,
                    rnd.nextInt(150),
                    rnd.nextInt(150),
                    rnd.nextInt(150)
                )
                colors {
                    dark = QrVectorColor.Solid(
                        ContextCompat.getColor(context, R.color.terminal_color)
                    )
                    ball = QrVectorColor.Solid(
                        ballFrameColor
                    )
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
            LogSystems.stdErr(e.toString())
        }
        return null
    }
    fun createAndSaveRnd(
        qrSrcStr: String,
        currentAppDirPath: String,
        fannelName: String,
    ): Drawable? {
        try{
            val drawable = create(qrSrcStr)
                ?: return null
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
                    val qrPngPath = "${currentAppDirPath}/${fannelDirName}/$qrPngRelativePath"
                    val qrPngPathObj =
                        File(qrPngPath)
                    val qrDirPath = qrPngPathObj.parent
                        ?: return@withContext
                    val qrBitMap = drawable.toBitmapOrNull(1000, 1000)
                        ?: return@withContext
                    FileSystems.savePngFromBitMap(
                        qrDirPath,
                        qrPngPathObj.name,
                        qrBitMap
                    )
                }
            }
            return drawable
        } catch(e: Exception){
            LogSystems.stdErr(e.toString())
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
        currentAppDirPath: String,
        selectedScriptName: String,
    ): Boolean {
        val scriptContentsList = ReadText(
            currentAppDirPath,
            selectedScriptName,
        ).textToList()
        val editExecuteAlwaysStr = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        val isEditExecuteForJs = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
            LanguageTypeSelects.JAVA_SCRIPT
        ) == editExecuteAlwaysStr
        val isEditExecuteForShell = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
            LanguageTypeSelects.SHELL_SCRIPT
        ) == editExecuteAlwaysStr
        return isEditExecuteForJs || isEditExecuteForShell
    }
}