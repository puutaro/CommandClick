package com.puutaro.commandclick.util

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.proccess.history.url_history.UrlLogoHistoryTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.url.UrlOrQuery
import java.io.File
import java.lang.ref.WeakReference


class ShortCutManager(
    private val activityRef: WeakReference<MainActivity>,
) {
//    val context = activity.applicationContext
//    var shortcutNamePromptDialog: Dialog? = null

        fun createShortCut() {
            val activity = activityRef.get()
                ?: return
            val startUpPref = FannelInfoTool.getSharePref(activity)
            val fannelInfoMap = FannelInfoTool.makeFannelInfoMapByShare(
                startUpPref
            )
            val currentShellFileName = FannelInfoTool.getCurrentFannelName(
                fannelInfoMap
            )
            val urlAndTitle = getUrlAndTitle(
                activity,
                currentShellFileName,
            )
            val url = urlAndTitle?.first
            val execIntent = createExecIntent(
                activity,
                fannelInfoMap,
                url
            )
            val title = urlAndTitle?.second
            val shortcutTitle = when(
                !title.isNullOrEmpty()
                && FannelInfoTool.isEmptyFannelName(currentShellFileName)
            ) {
                true -> title
                else -> CcPathTool.trimAllExtend(
                    FannelInfoTool.getCurrentFannelName(fannelInfoMap)
                )
            }
            val icon = createIcon(
                activity,
                url,
                currentShellFileName
            )
            execCreateShortcut(
                execIntent,
                shortcutTitle,
                icon
            )
//            return
//
//            createShortcutDialog(
//                execIntent
//            )
        }

    private fun createIcon(
        activity: MainActivity,
        url: String?,
        fannelName: String
    ): Icon {
        if(
            url.isNullOrEmpty()
        ) {
            val logoPngPath = sequenceOf(
                UsePath.fannelLogoPngPath,
            ).joinToString("/").let {
                ScriptPreWordReplacer.replace(
                    it,
                    fannelName
                )
            }
            return when (File(logoPngPath).isFile) {
                true -> {
                    val bitmap = BitmapTool.convertFileToBitmap(logoPngPath)
                    Icon.createWithBitmap(bitmap)
                }
                else -> Icon.createWithResource(
                    activity,
                    com.puutaro.commandclick.R.mipmap.ic_cmdclick_launcher
                )
            }
        }
        val logoBase64TxtPath = UrlLogoHistoryTool.getCaptureBase64TxtPathByUrl(
//                            currentAppDirPath,
            url,
        )?.absolutePath
            ?: return Icon.createWithResource(
                activity,
                com.puutaro.commandclick.R.mipmap.ic_cmdclick_launcher
            )
        return ReadText(logoBase64TxtPath).readText().let {
            BitmapTool.Base64Tool.decode(
                it
            )
        }.let {
            Icon.createWithBitmap(it)
        }
//        val imageName = FileSystems.sortedFiles(capPartPngDirPath).firstOrNull()
//            ?: return Icon.createWithResource(
//                context,
//                com.puutaro.commandclick.R.mipmap.ic_cmdclick_launcher
//            )
//        return Icon.createWithFilePath(
//            File(capPartPngDirPath, imageName).absolutePath
//        )

//        FannelInfoTool.getCurrentFannelName(fannelInfoMap)
    }
    private fun execCreateShortcut(
        execIntent: Intent,
        shortCutLabel: String,
        icon: Icon,
    ){
        val activity = activityRef.get()
            ?: return
        val shortCutId = getRandomString()
        val shortcut = ShortcutInfo.Builder(activity, shortCutId)
            .setShortLabel(shortCutLabel)
            .setLongLabel(shortCutLabel)
            .setIcon(icon)
            .setIntent(
                execIntent
            )
            .build()

        val manager = activity.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        manager.requestPinShortcut(shortcut, null)
    }


    private fun createExecIntent(
        activity: MainActivity,
        fannelInfoMap: HashMap<String, String>,
        url: String?,
    ): Intent {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentShellFileName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentStateName = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )


        val execIntent = Intent(activity, activity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

//        execIntent.putExtra(
//            FannelInfoSetting.current_app_dir.name,
//            currentAppDirPath
//        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_name.name,
            currentShellFileName
        )
        execIntent.putExtra(
            FannelInfoSetting.current_fannel_state.name,
            currentStateName
        )

        if(
            FannelInfoTool.isEmptyFannelName(currentShellFileName)
            && !url.isNullOrEmpty()
        ){
            execIntent.action = Intent.ACTION_MAIN
            execIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            execIntent.action = Intent.ACTION_WEB_SEARCH
            execIntent.data = Uri.parse(UrlOrQuery.convert(url))
        }
        return execIntent

    }

    private fun getUrlAndTitle(
        activity: MainActivity,
        currentShellFileName: String,
    ): Pair<String, String>? {
        if(
            !FannelInfoTool.isEmptyFannelName(currentShellFileName)
        ) return null

        val terminalFragment =
            TargetFragmentInstance.getCurrentTerminalFragment(activity)
            ?: return null
        val terminalWebView = terminalFragment.binding.terminalWebView
        return (terminalWebView.url ?: String()) to (terminalWebView.title ?: String())
    }

//    private fun createShortcutDialog(
//        execIntent: Intent
//    ){
//        shortcutNamePromptDialog = Dialog(
//            activity
//        )
//        shortcutNamePromptDialog?.setContentView(
//            com.puutaro.commandclick.R.layout.prompt_dialog_layout
//        )
//        val promptTitleTextView =
//            shortcutNamePromptDialog?.findViewById<AppCompatTextView>(
//                com.puutaro.commandclick.R.id.prompt_dialog_title
//            )
//        promptTitleTextView?.text = "Input shortcut label"
//        val promptMessageTextView =
//            shortcutNamePromptDialog?.findViewById<AppCompatTextView>(
//                com.puutaro.commandclick.R.id.prompt_dialog_message
//            )
//        promptMessageTextView?.isVisible = false
//        val promptEditText =
//            shortcutNamePromptDialog?.findViewById<AutoCompleteTextView>(
//                com.puutaro.commandclick.R.id.prompt_dialog_input
//            )
//        val promptCancelButton =
//            shortcutNamePromptDialog?.findViewById<AppCompatImageButton>(
//                com.puutaro.commandclick.R.id.prompt_dialog_cancel
//            )
//        promptCancelButton?.setOnClickListener {
//            shortcutNamePromptDialog?.dismiss()
//            shortcutNamePromptDialog = null
//        }
//        val promptOkButtonView =
//            shortcutNamePromptDialog?.findViewById<AppCompatImageButton>(
//                com.puutaro.commandclick.R.id.prompt_dialog_ok
//            )
//        promptOkButtonView?.setOnClickListener {
//            shortcutNamePromptDialog?.dismiss()
//            shortcutNamePromptDialog = null
//            val shortcutNameEditable = promptEditText?.text
//            if(
//                shortcutNameEditable.isNullOrEmpty()
//            ) return@setOnClickListener
//            val inputLabelName = shortcutNameEditable.toString()
//            execCreateShortcut(
//                execIntent,
//                inputLabelName
//            )
//        }
//        shortcutNamePromptDialog?.setOnCancelListener {
//            shortcutNamePromptDialog?.dismiss()
//            shortcutNamePromptDialog = null
//        }
//        shortcutNamePromptDialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        shortcutNamePromptDialog?.window?.setGravity(
//            Gravity.BOTTOM
//        )
//        shortcutNamePromptDialog?.show()
//    }

}


internal fun getRandomString() : String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..10)
        .map { charset.random() }
        .joinToString("")
}
