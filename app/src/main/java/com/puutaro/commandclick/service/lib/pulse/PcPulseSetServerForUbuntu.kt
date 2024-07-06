package com.puutaro.commandclick.service.lib.pulse

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

object PcPulseSetServerForUbuntu {

    private var pcPulseSetServer: ServerSocket? = null
    private var enablePcPulseSetServerRoop = true
    private var isTerminated = false
    private var pulseRecieverJob: Job? = null
    private var pulseSoundThread: PulseSoundThread? = null
    private val cmdclickTempMonitorDirPath = UsePath.cmdclickTempMonitorDirPath
    private val cmdclickTmpUpdateMonitorFileName = UsePath.cmdclickTmpUpdateMonitorFileName

    @Throws(Exception::class)
    @JvmStatic
    suspend fun launch(
        context: Context?,
        pcAddress: String,
        serverPortStr: String,
        ubuntuPcPulseSetServerPort: String,
    ) {
        val serverPort = try {
            serverPortStr.toInt()
        } catch (e: Exception){
            UsePort.UBUNTU_PULSE_RECEIVER_PORT.num
        }
        startPulse(
            context,
            pcAddress,
            serverPort,
            ubuntuPcPulseSetServerPort,
        )
    }

    private suspend fun startPulse(
        context: Context?,
        pcAddress: String,
        serverPort: Int,
        ubuntuPcPulseSetServerPort: String,
    ){
//        withContext(Dispatchers.IO) {
//            displayProcess(
//                context,
//                fullStatProcDesc.format(
//                    NetworkTool.getIpv4Address(context)
//                )
//            )
//        }
        pcPulseSetServer = withContext(Dispatchers.IO) {
            val ubuntuPcPulseSetServerPortInt = try {
                ubuntuPcPulseSetServerPort.toInt()
            } catch (e: Exception){
                UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num
            }
            if (
                pcPulseSetServer != null
                && pcPulseSetServer?.isClosed != true
            ) pcPulseSetServer?.close()
            ServerSocket(ubuntuPcPulseSetServerPortInt)
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
                pcAddress,
                serverPort,
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
        pcAddress: String,
        serverPort: Int,
    ) {

        val isr = InputStreamReader(client?.getInputStream())
        val reader = StringBuilder()
        val writer: OutputStream? = client?.getOutputStream()

        try {
            val br = BufferedReader(isr)
            while (br.ready()) {
                reader.append(br.read().toChar())
            }
            pulseSoundThread?.exit()
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
                        pcAddress,
                        serverPort,
                    )
                    pulseSoundThread?.run(
                        context
                    )
                }
            }
            writer?.write(response.toByteArray())
        } catch (ex: Exception) {
            LogSystems.equals(ex.toString())
        } finally {
            isr.close()
            writer?.close()
            client?.close()
        }
    }

    private fun displayProcess(
        context: Context?,
        updateMonitorContents: String
    ){
        FileSystems.updateFile(
            File(
                cmdclickTempMonitorDirPath,
                cmdclickTmpUpdateMonitorFileName
            ).absolutePath,
            updateMonitorContents
        )
        val updateMonitorIntent = Intent()
        updateMonitorIntent.action = BroadCastIntentSchemeTerm.MONITOR_TEXT_PATH.action
        context?.sendBroadcast(updateMonitorIntent)
    }
}



private val ubuntuSetUpShell = """
    #!/bin/sh
        
    pulseaudio --kill; sleep 0.5 \
	; PULSE_SERVER=  && pulseaudio --start \
    ; pactl load-module module-null-sink sink_name=TCP_output \
    && pacmd update-sink-proplist TCP_output device.description=TCP_output \
    && pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=TCP_output.monitor record=true port=${UsePort.UBUNTU_PULSE_RECEIVER_PORT.num} listen=0.0.0.0

    """.trimIndent()


private val fullStatProcDesc = """
    ### Execute bellow in PC
    
    1. curl %s:${UsePort.UBUNTU_PC_PULSE_SET_SERVER_PORT.num} | sh
    2. start "pavucontrol" 
    3. select "output -> TCP_output" in "pavucontrol"

    """.trimIndent()


private val reStatProcDesc = """
    ### Execute bellow in PC
    
    1. start "pavucontrol" 
    2. select "output -> TCP_output" in "pavucontrol"

    """.trimIndent()

