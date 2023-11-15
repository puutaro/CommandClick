package com.puutaro.commandclick.proccess.ubuntu

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.StreamWrapper
import java.util.Properties


object SshManager {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private const val sysMonitorFileName = UsePath.cmdClickMonitorFileName_2
    private val ubuntuUser = UbuntuInfo.user

    fun execScript(
        scriptPath: String,
        tabSepaStr: String,
        monitorFileName: String,
        isOutput: Boolean
    ):String {
        val tabSepaStrWithQuote = tabSepaStr.split("\t").map{
            "\"${it}\""
        }.joinToString("\t")
        return execCommand(
            "bash --login \"${scriptPath}\" $tabSepaStrWithQuote 2>&1",
            monitorFileName,
            isOutput
        )
    }

    private fun execCommand(
        command: String,
        monitorFileName: String = sysMonitorFileName,
        isOutput: Boolean
    ): String {
        var channel: ChannelExec? = null
        var session: Session? = null
        try {
//            LogSystems.stdSys("ssh start")
            val jsch = JSch()
            session = jsch.getSession(
                ubuntuUser,
                "127.0.0.1",
                UsePort.DROPBEAR_SSH_PORT.num
            )
            session.setPassword(ubuntuUser)
            // Avoid asking for key confirmation
            val prop = Properties()
            prop["StrictHostKeyChecking"] = "no"
            session.setConfig(prop)
            session.connect()
            channel = session.openChannel("exec") as ChannelExec
//            LogSystems.stdSys("ssh exec command..")
            channel.setCommand(command)
//            LogSystems.stdSys("ssh channel connect..")
            channel.connect()
//            LogSystems.stdSys("ssh set err stream..")
//            LogSystems.stdSys("ssh output..")
            val output = outputHandler(
                channel,
                monitorFileName,
                isOutput
            )
//            LogSystems.stdSys("ssh channel disconnect..")
            channel.disconnect()

//            LogSystems.stdSys("ssh disconnect..")
            // Close the connection to the SSH server
            session.disconnect()
//            LogSystems.stdSys("ssh comp")
            return output
        } catch (e: java.lang.Exception) {
            channel?.disconnect()
            session?.disconnect()
            LogSystems.stdErr(e.toString())
            return String()
        }
    }

    private fun outputHandler(
        channel: ChannelExec,
        monitorFileName: String,
        isOutput: Boolean
    ): String {
        return when(isOutput){
            true
            -> extractInputStream(
                    channel,
                )
            else
            -> {
                writeInputStream(
                    channel,
                    monitorFileName
                )
                String()
            }

        }
    }

    private fun writeInputStream(
        channel: ChannelExec,
        monitorFileName: String,
    ){
        try {
            StreamWrapper.writeByReader(
                channel.inputStream.bufferedReader(Charsets.UTF_8),
                cmdclickMonitorDirPath,
                monitorFileName,
            )
        } finally {
            //            LogSystems.stdErr("writeInputStream end")
            if(channel.inputStream != null) {
                channel.inputStream.close()
            }
            //            LogSystems.stdErr("writeInputStream end2")
        }
    }



    private fun extractInputStream(
        channel: ChannelExec,
    ): String {
        var output = String()
        try {
            //            LogSystems.stdSys("start extractInputStream..0")
            output = StreamWrapper.extractByReader(
                channel.inputStream.bufferedReader(Charsets.UTF_8)
            )
            //            LogSystems.stdSys("after extractInputStream..")
        } finally {
            if(channel.inputStream != null) {
//            LogSystems.stdSys("close inputstream extractInputStream..")
                channel.inputStream.close()
//                LogSystems.stdSys(" close ok inputstream extractInputStream")
            }
        }
        return output
    }
}