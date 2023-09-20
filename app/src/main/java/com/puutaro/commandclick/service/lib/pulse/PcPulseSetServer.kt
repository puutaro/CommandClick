package com.puutaro.commandclick.service.lib.pulse

import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket


object PcPulseSetServer {

    private val pcPulseSetServerPort = UsePort.pcPulseSetServer.num
    private var pcPulseSetServer: ServerSocket? = null
    private var enablePcPulseSetServerRoop = true
    private var isTerminated = false
    var pulseRecieverJob: Job? = null
    private var pulseSoundThread: PulseSoundThread? = null
    private val cmdclickTempMonitorDirPath = UsePath.cmdclickTempMonitorDirPath
    private val cmdclickTmpUpdateMonitorFileName = UsePath.cmdclickTmpUpdateMonitorFileName

    @Throws(Exception::class)
    @JvmStatic
    suspend fun launch(
        context: Context?,
        serverAddress: String,
        notificationId: String,
        channelId: Int,
        serverReceivingAddressPort: String,
        notificationManager: NotificationManagerCompat,
        cancelPendingIntent: PendingIntent
    ) {
        val enableRestart = PulseSoundThread(
            serverAddress,
        ).enableSock()
        when(enableRestart) {
            true -> restartPulse(
                context,
                serverAddress,
                notificationId,
                channelId,
                serverReceivingAddressPort,
                notificationManager,
                cancelPendingIntent
            )
            else -> startPulse(
                context,
                serverAddress,
                notificationId,
                channelId,
                serverReceivingAddressPort,
                notificationManager,
                cancelPendingIntent
            )
        }

    }

    private suspend fun restartPulse(
        context: Context?,
        serverAddress: String,
        notificationId: String,
        channelId: Int,
        serverReceivingAddressPort: String,
        notificationManager: NotificationManagerCompat,
        cancelPendingIntent: PendingIntent
    ){
        withContext(Dispatchers.IO) {
            pulseRecieverJob?.cancel()
        }
        withContext(Dispatchers.IO) {
            displayProcess(
                context,
                reStatProcDesc,
            )
            delay(100)
            pulseSoundThread = PulseSoundThread(
                serverAddress,
            )
            recieveNotification(
                context,
                notificationId,
                channelId,
                serverReceivingAddressPort,
                notificationManager,
                cancelPendingIntent
            )
            pulseSoundThread?.run(
                context
            )
        }
    }

    private suspend fun startPulse(
        context: Context?,
        serverAddress: String,
        notificationId: String,
        channelId: Int,
        serverReceivingAddressPort: String,
        notificationManager: NotificationManagerCompat,
        cancelPendingIntent: PendingIntent
    ){
        withContext(Dispatchers.IO) {
            displayProcess(
                context,
                fullStatProcDesc.format(
                    NetworkTool.getIpv4Address(context)
                )
            )
        }
        pcPulseSetServer = withContext(Dispatchers.IO) {
            ServerSocket(pcPulseSetServerPort)
        }
        enablePcPulseSetServerRoop = true

        while (enablePcPulseSetServerRoop) {
            isTerminated = false
            val client = withContext(Dispatchers.IO) {
                try {
                    pcPulseSetServer?.accept()
                } catch (e:Exception){
                    isTerminated = true
                    null
                }
            }
            if(isTerminated) return
            clientHandle(
                client,
                context,
                serverAddress,
                notificationId,
                channelId,
                serverReceivingAddressPort,
                notificationManager,
                cancelPendingIntent
            )
        }
    }

    fun exit(){
        enablePcPulseSetServerRoop = false
        if (
            pcPulseSetServer != null
            && pcPulseSetServer?.isClosed != true
        ) pcPulseSetServer?.close()
        pulseSoundThread?.exit()
        pulseRecieverJob?.cancel()
    }

    private fun clientHandle(
        client: Socket?,
        context: Context?,
        serverAddress: String,
        notificationId: String,
        channelId: Int,
        serverReceivingAddressPort: String,
        notificationManager: NotificationManagerCompat,
        cancelPendingIntent: PendingIntent
    ) {

        val isr = InputStreamReader(client?.getInputStream())
        val reader = StringBuilder()
        val writer: OutputStream? = client?.getOutputStream()

        try {
            val br = BufferedReader(isr)
            while (br.ready()) {
                reader.append(br.read().toChar())
            }
            val body = ubuntuSetUpShell
            val response = String.format(
                "HTTP/1.1 200 OK\nContent-Length: %d\r\n\r\n%s",
                body.length,
                body
            )
            pulseRecieverJob = CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    delay(3000)
                    pulseSoundThread = PulseSoundThread(
                        serverAddress,
                    )
                    recieveNotification(
                        context,
                        notificationId,
                        channelId,
                        serverReceivingAddressPort,
                        notificationManager,
                        cancelPendingIntent
                    )
                    pulseSoundThread?.run(
                        context
                    )
                }
            }
            writer?.write(response.toByteArray())
        } catch (ex: Exception) {
            client?.close()
        }
    }

    private fun displayProcess(
        context: Context?,
        updateMonitorContents: String
    ){
        FileSystems.writeFile(
            cmdclickTempMonitorDirPath,
            cmdclickTmpUpdateMonitorFileName,
            updateMonitorContents

        )
        val updateMonitorIntent = Intent()
        updateMonitorIntent.action = BroadCastIntentScheme.MONITOR_TEXT_PATH.action
        context?.sendBroadcast(updateMonitorIntent)
    }

    private fun recieveNotification(
        context: Context?,
        notificationId: String,
        channelId: Int,
        serverReceivingAddressPort: String,
        notificationManager: NotificationManagerCompat,
        cancelPendingIntent: PendingIntent
    ){
        if(context == null) return
        val notificationBuilder = NotificationCompat.Builder(
            context,
            notificationId
        )
            .setSmallIcon(R.drawable.ic_media_play)
            .setAutoCancel(true)
            .setContentTitle("Recieving..")
            .setContentText(serverReceivingAddressPort)
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
            channelId,
            notificationInstance
        )
    }
}



private val ubuntuSetUpShell = """
    #!/bin/sh
        
    pulseaudio --kill; sleep 0.5 \
	; PULSE_SERVER=  && pulseaudio --start \
    ; pactl load-module module-null-sink sink_name=TCP_output \
    && pacmd update-sink-proplist TCP_output device.description=TCP_output \
    && pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=TCP_output.monitor record=true port=10080 listen=0.0.0.0

    """.trimIndent()


private val fullStatProcDesc = """
    ### Execute bellow in PC
    
    1. curl %s:${UsePort.pcPulseSetServer.num} | sh
    2. start "pavucontrol" 
    3. select "output -> TCP_output" in "pavucontrol"

    """.trimIndent()


private val reStatProcDesc = """
    ### Execute bellow in PC
    
    1. start "pavucontrol" 
    2. select "output -> TCP_output" in "pavucontrol"

    """.trimIndent()

