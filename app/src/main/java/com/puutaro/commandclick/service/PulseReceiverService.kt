package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.intent.PulseServerIntentExtra
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.pulse.PcPulseSetServer
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PulseReceiverService:
    Service() {
    private var pulseRecieverJob: Job? = null
    private var pcPulseSetServerJob: Job? = null
    private val notificationIdToImportance =
        NotificationIdToImportance.LOW
    private val chanelId = ServiceChannelNum.pulseReciever
    private val notificationManager by lazy {
        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(channel)
        notificationManager
    }
    private var broadcastReceiverForPluseServerStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.STOP_PULSE_RECIEVER.action
            ) return
            finishProcess()
            stopSelf()
        }
    }
    private var broadcastReceiverForPluseServerRestart: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentScheme.RESTART_PULSE_RECIEVER.action
            ) return
            pcPulseSetServerJob?.cancel()
        }
    }

    override fun onCreate() {
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForPluseServerStop,
            BroadCastIntentScheme.STOP_PULSE_RECIEVER.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForPluseServerStop,
            BroadCastIntentScheme.RESTART_PULSE_RECIEVER.action
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        PcPulseSetServer.exit()
        notificationManager.cancel(chanelId)
        pulseRecieverJob?.cancel()
        val pcAddress = intent?.getStringExtra(
            PulseServerIntentExtra.pcAddress.schema
        ) ?: return START_NOT_STICKY
        val serverPort = intent.getStringExtra(
            PulseServerIntentExtra.serverPort.schema
        ) ?: return START_NOT_STICKY
        val cancelPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentScheme.STOP_PULSE_RECIEVER.action,
        )
        val serverWaitAddressPort = "${pcAddress}:${UsePort.pcPulseSetServer.num}"
        val serverReceivingAddressPort = "${pcAddress}:${UsePort.pluseRecieverPort.num}"
        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setSmallIcon(R.drawable.ic_media_play)
            .setAutoCancel(true)
            .setContentTitle("wait..")
            .setContentText(serverWaitAddressPort)
            .setProgress(0, 0, true)
            .setDeleteIntent(
                cancelPendingIntent
            )
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                "cancel",
                cancelPendingIntent
            )
        val notificationInstance = notificationBuilder.build()
        notificationManager.notify(
            chanelId,
            notificationInstance
        )
        startForeground(
            chanelId,
            notificationInstance
        )
        pcPulseSetServerJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                PcPulseSetServer.launch(
                    applicationContext,
                    pcAddress,
                    serverPort,
                    notificationIdToImportance.id,
                    chanelId,
                    serverReceivingAddressPort,
                    notificationManager,
                    cancelPendingIntent
                )
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        finishProcess()
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        finishProcess()
    }

    private fun finishProcess(){
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForPluseServerStop,
        )
        pulseRecieverJob?.cancel()
        pcPulseSetServerJob?.cancel()
        PcPulseSetServer.exit()
        notificationManager.cancel(chanelId)
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }
}