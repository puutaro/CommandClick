package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.webkit.JavascriptInterface
import androidx.core.content.ContextCompat
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
import com.puutaro.commandclick.common.variable.intent.extra.FileUploadExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.QrConfirmDialog
import com.puutaro.commandclick.proccess.qr.QrDecodedTitle
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.proccess.qr.QrEditType
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.qr.QrUriHandler
import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Random


class JsQr(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context

    val qrScanner = QrScanner(
        terminalFragment,
        terminalFragment.currentAppDirPath
    )

    @JavascriptInterface
    fun qrPrefixList(): String {
        return QrLaunchType.values().map { it.prefix }.joinToString("\n")
    }

    @JavascriptInterface
    fun qrEditTypeList(): String {
        val freeEditType = QrEditType.FREE_TEXT.type
        return QrLaunchType.values().map { it.prefix }.map {
            prefix ->
            QrEditType.values()
                .find {
                    it.prefixList.contains(prefix)
                }?.type
                ?: freeEditType
        }.distinct().reversed().joinToString("\n")
    }

    @JavascriptInterface
    fun launchUploader(){
        val intent = Intent(
            context,
            FileUploadService::class.java
        )
        intent.putExtra(
            FileUploadExtra.CURRENT_APP_DIR_PATH_FOR_FILE_UPLOAD.schema,
            terminalFragment.currentAppDirPath
        )
        context?.let {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    @JavascriptInterface
    fun scanFromImage(
        qrImagePath: String
    ): String {
        return qrScanner.scanFromImage(
            qrImagePath
        )
    }

    @JavascriptInterface
    fun scanHandler(
        decodedText: String,
    ){
        QrUriHandler.handle(
            terminalFragment,
            terminalFragment.currentAppDirPath,
            decodedText,
        )
    }

    @JavascriptInterface
    fun scanConfirmHandler(
        qrImagePath: String
    ){
        val decodedText = QrScanner(
            terminalFragment,
            terminalFragment.currentAppDirPath
        ).scanFromImage(qrImagePath)
        CoroutineScope(Dispatchers.Main).launch {
            QrConfirmDialog(
                terminalFragment,
                null,
                null,
                terminalFragment.currentAppDirPath,
                QrDecodedTitle.makeTitle(decodedText),
                decodedText
            ).launch()
        }
    }

    @JavascriptInterface
    fun makeQrSrcFile(
        qrSrcFilePath: String,
        qrSrcMapStr: String,
    ){
        val arSrcMapCon = QuoteTool.splitBySurroundedIgnore(
            qrSrcMapStr,
            '|'
        )
//        qrSrcMapStr
//            .split("|")
            .map{ it.trim() }
            .joinToString("\n")
        FileSystems.writeFile(
            qrSrcFilePath,
            arSrcMapCon,
        )
    }

    @JavascriptInterface
    fun createAndSave(
        qrSrcStr: String,
    ) {
        if(
            context == null
        ) return
        try {
            val data = QrData.Url(qrSrcStr)

            val options = createQrVectorOptions {

                padding = .125f

                background {
                    drawable = ContextCompat
                        .getDrawable(context, R.color.white)
                }

                logo {
                    drawable = ContextCompat
                        .getDrawable(context, R.drawable.icons8_history)
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape
                        .Circle
                }
                colors {
                    dark = QrVectorColor
                        .Solid(Color(0xff345288))
                    ball = QrVectorColor.Solid(
                        ContextCompat.getColor(context, R.color.black)
                    )
                    frame = QrVectorColor.LinearGradient(
                        colors = listOf(
                            0f to android.graphics.Color.RED,
                            1f to android.graphics.Color.BLUE,
                        ),
                        orientation = QrVectorColor.LinearGradient
                            .Orientation.LeftDiagonal
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
            val qrDrawable = QrCodeDrawable(data, options)
            val qrBitMap = QrLogo.toBitMapWrapper(qrDrawable)
                ?: return
            FileSystems.savePngFromBitMap(
                File(
                    UsePath.cmdclickTempDownloadDirPath,
                "qr.png"
                ).absolutePath,
                qrBitMap
            )
        }catch(e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }

    @JavascriptInterface
    fun makeScpQrSrcStr(dirPath: String): String {
        return QrDialogMethod.makeScpDirQrStr(
            terminalFragment,
            dirPath
        )
    }

    @JavascriptInterface
    fun makeCpFileQr(
        path: String,
    ): String {
        return QrDialogMethod.makeCpFileQrNormal(
            terminalFragment,
            path,
        )
    }

    @JavascriptInterface
    fun saveQrImage(
        srcQrStr: String,
        savePath: String,
    ){
        val drawable =
            QrLogo(terminalFragment)
                .createMonochrome(
                    srcQrStr
                ) ?: return
        val qrBitMap = QrLogo.toBitMapWrapper(drawable)
            ?: return
        FileSystems.savePngFromBitMap(
            savePath,
            qrBitMap
        )

    }

    @JavascriptInterface
    fun createAndSaveRnd(
        qrSrcStr: String,
    ) {
        val blueGreen = Color(
            255, 64, 199, 190,
        )
        val kansouDeepBlue = Color(
            255, 35, 64, 207
        )
        val orange = Color(
            255, 252, 148, 3
        )
        val carki = Color(
            255, 74, 120, 42
        )
        val blueGreenKansou = Color(
            255, 42, 113, 120
        )
        val deepBlueGreenKansou = Color(
            255, 6, 84, 92
        )
        val lightBlueGreenKansou = Color(
            255, 5, 232, 255
        )
        val deepGreen = Color(
            255, 1, 61, 9
        )
        val lightDeepGreen = Color(
            255, 52, 199, 71
        )
        val graphicsColorList = listOf(
            android.graphics.Color.RED,
            android.graphics.Color.BLUE,
            android.graphics.Color.BLACK,
            android.graphics.Color.YELLOW,
            android.graphics.Color.CYAN,
            android.graphics.Color.GREEN,
            android.graphics.Color.MAGENTA,
            blueGreen,
            kansouDeepBlue,
            orange,
            carki,
            blueGreenKansou,
            deepBlueGreenKansou,
            lightBlueGreenKansou,
            deepGreen,
            lightDeepGreen
        )

        val orientationList = listOf(
            QrVectorColor.LinearGradient
                .Orientation.LeftDiagonal,
            QrVectorColor.LinearGradient
                .Orientation.RightDiagonal,
            QrVectorColor.LinearGradient
                .Orientation.Horizontal,
            QrVectorColor.LinearGradient
                .Orientation.Vertical

        )
        val shapeList = listOf(
            QrVectorLogoShape.Circle,
            QrVectorLogoShape.Rhombus,
//            QrVectorLogoShape.Default,
        )
        val dorkBoundLimit = 50
        if(
            context == null
        ) return
        val rnd = Random(System.currentTimeMillis())
        try {
            val data = QrData.Url(qrSrcStr)

            val options = createQrVectorOptions {

                padding = .125f

                background {
                    drawable = ContextCompat
                        .getDrawable(context, R.color.white)
//                        generateRandomColor(rnd)
//                    ContextCompat
//                        .getDrawable(context, R.color.white)
                }
                logo {
                    drawable = ContextCompat
                        .getDrawable(context, R.drawable.fannel_royal_navy)?.apply {
                            setTint(
                                Color(
                                    255,
                                    rnd.nextInt(150),
                                    rnd.nextInt(150),
                                    rnd.nextInt(150)
                                )
                            )
//                                        context.getColor(R.color.terminal_color)
                        }
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape.Circle
                    background {
                        QrVectorColor.Solid(
                            ContextCompat.getColor(context, R.color.terminal_color)
                        )
                    }
                    colors {

                    }
                }
//                val dark = generateRandomColour().darker();
//                val aaa = rnd.nextInt(256)
//                val aa = Color(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
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
//                        QrVectorColor
//                        .Solid(
//                            Color(
//                                255,
//                                rnd.nextInt(dorkBoundLimit),
//                                rnd.nextInt(dorkBoundLimit),
//                                rnd.nextInt(dorkBoundLimit)
//                            )
//                        )
                    ball = QrVectorColor.Solid(
                        ballFrameColor
//                        graphicsColorList.random()
//                        ContextCompat.getColor(context, R.color.terminal_color)
                    )
                    val weight =  Math.random().toFloat()
                    frame = QrVectorColor.Solid(
//                        ballFrameColor
                        ContextCompat.getColor(context, R.color.terminal_color)
                    )
//                        QrVectorColor.LinearGradient(
//                        colors = listOf(
//                            0f to graphicsColorList.random(),
//                            1f to graphicsColorList.random(),
//                        ),
//                        orientation = orientationList.random()
//                    )
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
            val qrLogoDrawable = QrCodeDrawable(data, options)
            val qrBitMap = QrLogo.toBitMapWrapper(qrLogoDrawable)
                ?: return
            FileSystems.savePngFromBitMap(
                File(
                    UsePath.cmdclickTempDownloadDirPath,
                "qr.png"
                ).absolutePath,
                qrBitMap
            )
        }catch(e: Exception){
            LogSystems.stdErr(e.toString())
        }

    }

    private fun generateRandomColor(
        rnd: Random
    ): ColorDrawable {
        // This is the base color which will be mixed with the generated one
        val baseColor = Color.WHITE
        val baseRed = Color.red(baseColor)
        val baseGreen = Color.green(baseColor)
        val baseBlue = Color.blue(baseColor)
        val red: Int = (baseRed + rnd.nextInt(256)) / 2
        val green: Int = (baseGreen + rnd.nextInt(256)) / 2
        val blue: Int = (baseBlue + rnd.nextInt(256)) / 2
//        val baseColor = 200
//        val baseRed = rnd.nextInt(256)
//        val baseGreen = rnd.nextInt(256)
//        val baseBlue = rnd.nextInt(256)
//        val red: Int = (baseRed + rnd.nextInt(256)) / 2
//        val green: Int = (baseGreen + rnd.nextInt(256)) / 2
//        val blue: Int = (baseBlue + rnd.nextInt(256)) / 2
        return ColorDrawable(
            convertColorFromHSV(
                Color(
                    255,
                    rnd.nextInt(baseRed),
                    rnd.nextInt(baseGreen),
                    rnd.nextInt(baseBlue),
                ),
                0F
            )

        )
//        return Color(
//                    255,
//                    rnd.nextInt(red),
//                    rnd.nextInt(green),
//                    rnd.nextInt(blue),
//
//                )
    }

    fun convertColorFromHSV(color: Int, brightness: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = 0.01f
        hsv[2] -= brightness
        return Color.HSVToColor(hsv)
    }
}