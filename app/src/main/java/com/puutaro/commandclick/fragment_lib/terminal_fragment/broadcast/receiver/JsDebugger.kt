package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.R
import android.app.NotificationChannel
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WevViewDialogUriPrefix
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object JsDebugger {

    private val chanelId = ServiceChannelNum.jsDebugger

    enum class StockLogMapKey(
        val key: String,
    ) {
        DATETIME(BroadCastIntentExtraForJsDebug.BroadcastSchema.DATETIME.scheme),
        NOTI_LEVEL(BroadCastIntentExtraForJsDebug.BroadcastSchema.NOTI_LEVEL.scheme),
        DEBUG_LEVEL(BroadCastIntentExtraForJsDebug.BroadcastSchema.DEBUG_GENRE.scheme)
    }

    fun putStockLogMap(
        notiDatetime: String,
        notiLevelStr: String,
        debugNotiJanreStr: String,
    ) {
        FileSystems.writeFile(
            UsePath.jsDebuggerMapTxtPath,
            listOf(
                "${StockLogMapKey.DATETIME.key}\t${notiDatetime}",
                "${StockLogMapKey.NOTI_LEVEL.key}\t${notiLevelStr}",
                "${StockLogMapKey.DEBUG_LEVEL.key}\t${debugNotiJanreStr}",
            ).joinToString("\n")
        )
    }

    fun stockLogSender(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
        val jsDebuggerMapTxtPath = UsePath.jsDebuggerMapTxtPath
        if(
            !File(jsDebuggerMapTxtPath).isFile
        ) return
        val jsDebugExtraPairList = ReadText(
            jsDebuggerMapTxtPath
        ).readText().replace("\t", "=").let {
            CmdClickMap.createMap(
                it,
                '\n'
            )
        }
        if(
            jsDebugExtraPairList.isEmpty()
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            withContext(Dispatchers.IO) {
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeTerm.DEBUGGER_NOTI.action,
                    jsDebugExtraPairList
                )
            }
        }
    }

    fun close(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val context = terminalFragment.context
            ?: return
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(chanelId)
    }

    fun launchNoti(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val context = terminalFragment.context
            ?: return
        val notiDatetime = intent.getStringExtra(
            BroadCastIntentExtraForJsDebug.BroadcastSchema.DATETIME.scheme
        )?.replace(Regex("[.].*$"), "")
            ?: LocalDateTime.now().toString()
        val debugLevelStr = intent.getStringExtra(
            BroadCastIntentExtraForJsDebug.BroadcastSchema.NOTI_LEVEL.scheme
        )
        val debugGenreStr = intent.getStringExtra(
            BroadCastIntentExtraForJsDebug.BroadcastSchema.DEBUG_GENRE.scheme
        )
        val debugGenre = BroadCastIntentExtraForJsDebug.DebugGenre.values().firstOrNull {
            it.type == debugGenreStr
        } ?: BroadCastIntentExtraForJsDebug.DebugGenre.ERR
        val debugLevel =
            BroadCastIntentExtraForJsDebug.NotiLevelType.values().firstOrNull {
                it.level == debugLevelStr
            } ?: BroadCastIntentExtraForJsDebug.NotiLevelType.LOW
        val notificationIdToImportance = when(debugLevel){
            BroadCastIntentExtraForJsDebug.NotiLevelType.LOW
            -> NotificationIdToImportance.LOW
            BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH
            -> NotificationIdToImportance.HIGH
        }

        val notificationManager = let {
            val channel = NotificationChannel(
                notificationIdToImportance.id,
                notificationIdToImportance.id,
                notificationIdToImportance.importance
            )
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.createNotificationChannel(channel)
            notificationManager
        }
        val editErrWatchPendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentSchemeTerm.DEBUGGER_SYS_WATCH.action
        )
        val jsErrwatchPendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentSchemeTerm.DEBUGGER_JS_WATCH.action
        )
        val cancelPendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentSchemeTerm.DEBUGGER_CLOSE.action
        )
        val notificationBuilder = NotificationCompat.Builder(
            context,
            notificationIdToImportance.id
        )
            .setSmallIcon(com.puutaro.commandclick.R.drawable.icons8_file)
            .setAutoCancel(true)
            .setContentTitle("[${debugGenre.label}] ${notiDatetime} ")
            .setContentText("Click ${debugGenre.buttonName}")
            .setDeleteIntent(
                cancelPendingIntent
            ).addAction(
                R.drawable.ic_menu_close_clear_cancel,
                BroadCastIntentExtraForJsDebug.DebugGenre.JS_ERR.buttonName,
                jsErrwatchPendingIntent
            ).addAction(
                R.drawable.ic_menu_close_clear_cancel,
                BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR.buttonName,
                editErrWatchPendingIntent
            )
        val notificationInstance = notificationBuilder.build()
            notificationManager.notify(
            chanelId,
            notificationInstance
        )
        FileSystems.removeFiles(
            UsePath.jsDebuggerMapTxtPath
        )
    }

    fun jsErrWatch(
        terminalFragment: TerminalFragment,
        intent: Intent
    ){
        val execDebugJsPath = UsePath.jsDebugReportPath
        val launchUrlCon =
            WevViewDialogUriPrefix.TEXT_CON.prefix + execDebugJsPath
        launchLogDialog(
            terminalFragment,
            launchUrlCon
        )
    }

    fun sysErrWatch(
        terminalFragment: TerminalFragment,
        intent: Intent
    ){

        val sysOrErrLogCon = makeErrSysLogCon()
        val editDebugLogPath = UsePath.editDebugLogPath
        FileSystems.writeFile(
            editDebugLogPath,
            sysOrErrLogCon
        )
        val launchUrlCon =
            WevViewDialogUriPrefix.TEXT_CON.prefix + editDebugLogPath
        launchLogDialog(
            terminalFragment,
            launchUrlCon
        )
    }

    private fun launchLogDialog(
        terminalFragment: TerminalFragment,
        launchUrl: String,
    ){
        val context = terminalFragment.context
            ?: return
        val jsConSrc = """
            jsDialog.webView_S(
                "${launchUrl}",
                "",
                "dismissType=both!iconName=cancel",
                "",
            );
        """.trimIndent()
        val jsCon = JavaScriptLoadUrl.makeFromContents(
            context,
            jsConSrc.split("\n"),
        ) ?: return
        terminalFragment.binding.terminalWebView.loadUrl(jsCon)

    }

    private fun makeErrSysLogCon(): String {
        val logSeparator = "CMDCLICL_LOG_SEPARATOR"
        val logPrefixRegexNewLine = Regex(
            "\n${LogSystems.logPrefix}"
        )
        val logPrefixRegex = Regex(
            "^${LogSystems.logPrefix}"
        )
//        val spanTagHolder = LogTool.spanTagHolder
        val errMark = LogTool.errMark
        var times = 0
        val sysLogCon = ReadText(
            File(
                UsePath.cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_2,
            ).absolutePath
        ).readText().replace(
            logPrefixRegex,
            logSeparator
        ).replace(
            logPrefixRegexNewLine,
            logSeparator
        ).split(logSeparator).reversed().map {
            el ->
            if(
                el.trim().isEmpty()
            ) return@map el
            val leadAndBodyList = el.split("\n")
            val lead = leadAndBodyList.firstOrNull()
                ?: return@map el
            val body = leadAndBodyList.filterIndexed {
                index, s -> index > 0
            }.joinToString("\n")
            val leadColorStr =
                LogTool.makeLeadColorCode(times)
            val colorStr = LogTool.makeColorCode(
                times
            )
            times++
            listOf(
                LogTool.makeSpanTagHolder(
                    leadColorStr,
                    lead
                ),
//                spanTagHolder.format(
//                    leadColorStr,
//                    lead
//                ),
                LogTool.makeSpanTagHolder(
                    colorStr,
                    body.trim().trim('\n')
                )
//                spanTagHolder.format(
//                    colorStr,
//                    body.trim().trim('\n')
//                )
            ).joinToString("\n") + "\n\n"
        }.joinToString("\n").replace(
            errMark,
            LogTool.makeTopSpanLogTagHolder(
                LogTool.errRedCode,
                errMark
            )
//            spanTagHolder.format(
//                LogTool.errRedCode,
//                errMark
//            )
        ).replace(
            "\n{4,}".toRegex(),
            "\n\n\n"
        )
        return "\n${sysLogCon}"
    }
}

