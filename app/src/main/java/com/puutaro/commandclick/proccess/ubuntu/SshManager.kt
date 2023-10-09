package com.puutaro.commandclick.proccess.ubuntu

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.StreamWrapper
import java.time.LocalDateTime
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
        return execCommmand(
            "bash --login \"${scriptPath}\" $tabSepaStrWithQuote 2>&1",
            monitorFileName,
            isOutput
        )
    }

    private fun execCommmand(
        command: String,
        monitorFileName: String = sysMonitorFileName,
        isOutput: Boolean
    ): String {
        var channel: ChannelExec? = null
        var session: Session? = null
        try {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "### ${LocalDateTime.now()} ssh start"
            )
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

            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh exec command.."
            )
            channel.setCommand(command)

            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh channel connect.."
            )
            channel.connect()
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh set err stream.."
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh output.."
            )
            val output = outputHandler(
                channel,
                monitorFileName,
                isOutput
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh channel disconnect.."
            )
            channel.disconnect()


            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ssh disconnect.."
            )
            // Close the connection to the SSH server
            session.disconnect()
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "### ${LocalDateTime.now()} ssh comp"
            )
            return output
        } catch (e: java.lang.Exception) {
            channel?.disconnect()
            session?.disconnect()
            FileSystems.writeFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "${e}"
            )
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
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "## writeInputStream end"
            )
            if(channel.inputStream != null) {
                channel.inputStream.close()
            }
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                sysMonitorFileName,
                "## writeInputStream end2"
            )
        }
    }



    private fun extractInputStream(
        channel: ChannelExec,
    ): String {
        var output = String()
        try {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "### start extractInputStream..0"
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "### start extractInputStream..1"
            )
            output = StreamWrapper.extractByReader(
                channel.inputStream.bufferedReader(Charsets.UTF_8)
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                UsePath.cmdClickMonitorFileName_1,
                "### after extractInputStream.."
            )
        } finally {
            if(channel.inputStream != null) {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                    "### close inputstream extractInputStream.."
                )
                channel.inputStream.close()
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    UsePath.cmdClickMonitorFileName_1,
                    "### close ok inputstream extractInputStream"
                )
            }
        }
        return output
    }
}