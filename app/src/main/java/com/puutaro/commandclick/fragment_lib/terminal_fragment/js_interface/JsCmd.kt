package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.utils.UlaFiles
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.time.LocalDateTime


class JsCmd(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val currentMonitorFileName = terminalViewModel.currentMonitorFileName

    @JavascriptInterface
    fun run(
        url: String,
        executeShellPath:String
    ){
        if(context == null) return
        try {
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} ulaInstanceStart"
//            )
            val ulaFiles = UlaFiles(
                context,
                context.applicationInfo.nativeLibraryDir,
                onInit = false
            )
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} busyboxInstanceStart"
//            )
//
//            val busyboxExecutor = BusyboxExecutor(
//                ulaFiles,
//            )
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} prootCmdStart"
//            )
//            val output = busyboxExecutor.executeProotCommand(
//                listOf("su", "-", "cmdclick", "-c", "ls"),
//                outputType = TerminalOutputType.last
//            )
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} prootCmdEnd\n${output}"
//            )
            val executeShellObj = File(executeShellPath)
            val executeShellDirPath = executeShellObj.parent
                ?: return
            val executeShellName = executeShellObj.name
                ?: return
            FileSystems.writeFile(
                ulaFiles.filesOneRootfsHomeCmdclickCmdDir.absolutePath,
                ulaFiles.cmdShell,
                ReadText(
                    executeShellDirPath,
                    executeShellName
                ).readText()
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()}\n curl start"
            )
            val shellOutput = CurlManager.get(
                url,
                String(),
                String(),
                2000,
            )
            if(
                shellOutput.isEmpty()
            ) {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName,
                    "### ${LocalDateTime.now()}\n no output"
                )
                return
            }
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()}\n ${shellOutput}"
            )

//            val ssh =
//                sshConnectSetting(
//                    "192.168.0.4",
//                    "cmdclick",
//                    "cmdclick"
//                )
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} sshCmdStart"
//            )
//            // コマンド実行
//            sessionCommand(
//                ssh.startSession(),
//                "ls",
//                5
//            )
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} sshCmdEnd"
//            )
//            ssh.disconnect()
        } catch (e: Exception) {
            FileSystems.writeFile(
                cmdclickDefaultAppDirPath,
                "prootError.txt",
                "### ${LocalDateTime.now()}\n${e.toString()}"
            )
        }
    }

//    @Throws(IOException::class)
//    private fun sessionCommand(session: Session, command: String, timeout: Long) {
//        try {
//            val cmd: Session.Command = session.exec(command)
//            val output = IOUtils.readFully(cmd.inputStream).toString()
//            cmd.join(timeout, TimeUnit.SECONDS)
//            FileSystems.writeFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                ReadText(
//                    cmdclickMonitorDirPath,
//                    currentMonitorFileName,
//                ).readText() + "\n${output}"
//            )
//            System.out.println(
//                """
//
//                ** exit status: ${cmd.getExitStatus()}
//                """.trimIndent()
//            )
//        } finally {
//            session.close()
//        }
//    }
//    @Throws(IOException::class)
//    private fun sshConnectSetting(host: String, username: String, password: String): SSHClient {
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} SSHClient Start"
//        )
//        val ssh = SSHClient()
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} loadKnownHosts Start"
//        )
//        try {
//            ssh.loadKnownHosts()
//        } catch (e: Exception){
//            FileSystems.updateFile(
//                cmdclickMonitorDirPath,
//                currentMonitorFileName,
//                "### ${LocalDateTime.now()} loadKnownHosts failure"
//            )
//        }
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} connect Start"
//        )
//        ssh.connect(host, 10022)
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} allowedMethods Start"
//        )
//        ssh.userAuth.allowedMethods
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} keepAliveInterval Start"
//        )
//        // keepAliveの感覚設定
//        ssh.connection.keepAlive.keepAliveInterval = 5
//
//        FileSystems.updateFile(
//            cmdclickMonitorDirPath,
//            currentMonitorFileName,
//            "### ${LocalDateTime.now()} ulaInstanceStart"
//        )
//        return ssh
//    }

}