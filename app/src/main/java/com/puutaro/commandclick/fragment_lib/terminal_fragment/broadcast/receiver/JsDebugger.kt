package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.R
import android.app.NotificationChannel
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WevViewDialogUriPrefix
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.file.UrlFileSystems

object JsDebugger {

    private val chanelId = ServiceChannelNum.jsDebugger

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
        val debugLevelStr = intent.getStringExtra(
            BroadCastIntentExtraForJsDebug.BroadcastSchema.DEBUG_LEVEL.scheme
        )
        val debugLevel =
            BroadCastIntentExtraForJsDebug.DebugLevelType.values().firstOrNull {
                it.level == debugLevelStr
            } ?: BroadCastIntentExtraForJsDebug.DebugLevelType.LOW
        val notificationIdToImportance = when(debugLevel){
            BroadCastIntentExtraForJsDebug.DebugLevelType.LOW
            -> NotificationIdToImportance.LOW
            BroadCastIntentExtraForJsDebug.DebugLevelType.HIGH
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
        val watchPendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentSchemeTerm.JS_DEBUG_WATCH.action
        )
        val cancelPendingIntent = PendingIntentCreator.create(
            context,
            BroadCastIntentSchemeTerm.JS_DEBUG_CLOSE.action
        )
        val notificationBuilder = NotificationCompat.Builder(
            context,
            notificationIdToImportance.id
        )
            .setSmallIcon(com.puutaro.commandclick.R.drawable.icons8_file)
            .setAutoCancel(true)
            .setContentTitle("[ERROR] Js Debugger")
            .setContentText("Click log")
            .setDeleteIntent(
                cancelPendingIntent
            )
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                "CANCEL",
                cancelPendingIntent
            )
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                "LOG",
                watchPendingIntent
            )
            val notificationInstance = notificationBuilder.build()
                notificationManager.notify(
                chanelId,
                notificationInstance
            )
        }

    fun watch(
        terminalFragment: TerminalFragment,
        intent: Intent
    ){
        val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                UsePath.JS_FILE_SUFFIX
        val execDebugJsPath = UsePath.execDebugJsPath
        val launchUrlCon =
            WevViewDialogUriPrefix.TEXT_CON.prefix + execDebugJsPath
        ExecJsLoad.execExternalJs(
            terminalFragment,
            terminalFragment.currentAppDirPath,
            webSearcherName,
            listOf(launchUrlCon)
        )
    }
}

