package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.EditSiteBroadCast
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.IntentLauncher
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File
import java.lang.ref.WeakReference


class JsIntent(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private val editSiteBroadCast = EditSiteBroadCast(terminalFragmentRef)


    @JavascriptInterface
    fun launchEditSite(
        editPath: String,
        extraMapStr: String,
        filterCode: String,
    ) {
        editSiteBroadCast.send(
            editPath,
            extraMapStr,
            filterCode
        )
    }

    @JavascriptInterface
    fun launchUrl(
        currentPageUrl: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        val openUrlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(currentPageUrl)
        )
        terminalFragment.startActivity(openUrlIntent)
    }

    @JavascriptInterface
    fun launchApp(
        action: String,
        uriString: String,
        extraListStrTabSepa: String,
        extraListIntTabSepa: String,
        extraListLongTabSepa: String,
        extraListFloatTabSepa: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity

        IntentLauncher.send(
            activity,
            action,
            uriString,
            extraListStrTabSepa,
            extraListIntTabSepa,
            extraListLongTabSepa,
            extraListFloatTabSepa,
        )
    }

    @JavascriptInterface
    fun sendGmail(
        title: String,
        body: String,
        extraMapCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?:return
        val context = terminalFragment.context
            ?: return
        val sendGmailExtraMap = CmdClickMap.createMap(
            extraMapCon,
            SendGmailSchema.separator
        ).toMap()
        val url = sendGmailExtraMap.get(
            SendGmailSchema.Schema.URL.schema
        )
        val captureUri = when(
            url.isNullOrEmpty()
        ){
            false -> SendGmailSchema.createCapture(
                terminalFragment,
                title,
                url,
            )
            else -> null
        }
        val to = sendGmailExtraMap.get(
            SendGmailSchema.Schema.TO.schema
        ) ?: String()
        try {
            val gmailIntent = Intent(Intent.ACTION_SEND) // データーを送信するインテント
            gmailIntent.type = "message/rfc822"
            gmailIntent.putExtra(Intent.EXTRA_EMAIL, to)
            gmailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            gmailIntent.putExtra(Intent.EXTRA_TEXT,body)
            gmailIntent.putExtra(Intent.EXTRA_CC, String())
            gmailIntent.putExtra(Intent.EXTRA_BCC, String())
            captureUri?.let {
                gmailIntent.putExtra(Intent.EXTRA_STREAM, it)
            }
            context.startActivity(
                Intent.createChooser(
                    gmailIntent,
                    "✉ Choose gmail"
                )
            )
        } catch (e: Exception){
            ToastUtils.showShort(e.toString())
        }
    }

    private object SendGmailSchema {

        const val separator = '|'
        enum class Schema(val schema: String) {
            URL("url"),
            TO("to"),
        }

        fun createCapture(
            terminalFragment: TerminalFragment,
            title: String,
            url: String,
        ): Uri? {
            val context = terminalFragment.context
                ?: return null
            val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
            FileSystems.removeAndCreateDir(cmdclickTempDownloadDirPath)
//            val gifFile = File(UrlHistoryPath.getCaptureGifPath(url))
//            if(
//                gifFile.isFile
//            ) {
//                val shareGifFile =
//                    File(
//                        cmdclickTempDownloadDirPath,
//                        CcPathTool.toValidPathWord(title) + ".gif"
//                    )
//               FileSystems.copyFile(
//                   gifFile.absolutePath,
//                   shareGifFile.absolutePath
//               )
//                return FileProvider.getUriForFile(
//                    context,
//                    context.applicationContext.packageName + ".provider",
//                    shareGifFile
//                )
//            }
            val byteArray = BitmapTool.getLowScreenShotFromView(terminalFragment.view)?.let {
                BitmapTool.convertBitmapToByteArray(
                    BitmapTool.resizeByMaxHeight(it, 1000.0)
                )
            } ?: return null
            val captureFile = File(cmdclickTempDownloadDirPath, "${CcPathTool.toValidPathWord(title)}.png")
            FileSystems.writeFromByteArray(
                captureFile.absolutePath,
                byteArray
            )
            return FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                captureFile
            )
        }
    }

    @JavascriptInterface
    fun launchShortcut(
        currentAppDirPath: String,
        currentScriptFileName: String,
        currentFannelState: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        val execIntent = Intent(terminalFragment.activity, MainActivity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

//        execIntent.putExtra(
//            FannelInfoSetting.current_app_dir.name,
//            currentAppDirPath
//        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_name.name,
            currentScriptFileName
        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_state.name,
            currentFannelState
        )
        terminalFragment.activity?.startActivity(execIntent)
    }

    @JavascriptInterface
    fun shareImage(
        imageFilePath: String,
    ){
        /*
        Launch share image intent
        */
        val imageFilePathObj = File(imageFilePath)
        if(
            !imageFilePathObj.isFile
        ) {
            ToastUtils.showLong("no exist\n ${imageFilePath}")
        }
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context

        IntentVariant.sharePngImage(
            imageFilePathObj,
            context,
        )
    }
}
