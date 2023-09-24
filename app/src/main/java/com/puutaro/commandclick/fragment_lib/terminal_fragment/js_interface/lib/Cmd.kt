package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import java.io.File

//class JsCmd(
//    private val terminalFragment: TerminalFragment
//) {
//    val context = terminalFragment.context
//    @JavascriptInterface
//    fun bExec(
//        bPath: String,
//        cmd: String,
//    ): String {
//        val bFileObj = File(
//            bPath
//        )
//        FileSystems.copyFile(
//            "/storage/emulated/0/Documents/cmdclick/AppDir/default/gohello",
//            "/data/user/0/com.puutaro.commandclick/files/${bFileObj}.name"
//        )
//        val err = LinuxCmd.exec(
//            terminalFragment,
//            listOf(
//                "chmod",
//                "777",
//                "/data/user/0/com.puutaro.commandclick/files/gohello"
//            ).joinToString("\t")
//        )
//        Toast.makeText(
//            context,
//            err.toString(),
//            Toast.LENGTH_LONG
//        ).show()
//        return LinuxCmd.exec(
//            terminalFragment,
//            listOf(
//                "curl",
//                "https://developers.cyberagent.co.jp/blog/archives/27889/"
//            ).joinToString("\t")
//        )
////        "adb",
////        "push",
////        "/storage/emulated/0/Documents/cmdclick/AppDir/default/gohello",
////        "/data/local/tmp/"
////        listOf(
////            "sh",
////            "-c",
////            cmd
////        )
//    }
//
//    @JavascriptInterface
//    fun exec(
//        cmd: String,
//    ): String {
//        FileSystems.copyFile(
//            "/storage/emulated/0/Documents/cmdclick/AppDir/default/gohello",
//            "/data/user/0/com.puutaro.commandclick/files/gohello"
//        )
//        val err = LinuxCmd.exec(
//            terminalFragment,
//            listOf(
//                "chmod",
//                "777",
//                "/data/user/0/com.puutaro.commandclick/files/gohello"
//            ).joinToString("\t")
//        )
//        Toast.makeText(
//            context,
//            err.toString(),
//            Toast.LENGTH_LONG
//        ).show()
//        return LinuxCmd.exec(
//            terminalFragment,
//            listOf(
//                "sh",
//                "-c",
//                "/data/user/0/com.puutaro.commandclick/files/gohello"
//            ).joinToString("\t")
//        )
//
////        "adb",
////        "push",
////        "/storage/emulated/0/Documents/cmdclick/AppDir/default/gohello",
////        "/data/local/tmp/"
////        listOf(
////            "sh",
////            "-c",
////            cmd
////        )
//    }
//
//    @JavascriptInterface
//    fun rExec(
//        cmd: String,
//    ): String {
//        FileSystems.copyFile(
//            "/storage/emulated/0/Documents/cmdclick/AppDir/default/gohello",
//            "/data/user/0/com.puutaro.commandclick/files/gohello"
//        )
//        val err = LinuxCmd.exec(
//            terminalFragment,
//            listOf(
//                "chmod",
//                "777",
//                "/data/user/0/com.puutaro.commandclick/files/gohello"
//            ).joinToString("\t")
//        )
//        Toast.makeText(
//            context,
//            err.toString(),
//            Toast.LENGTH_LONG
//        ).show()
//        return LinuxCmd.exec(
//            terminalFragment,
//            cmd
//        )
//    }
//
//    @JavascriptInterface
//    fun adbShell(
//        cmd: String
//    ): String {
//        Toast.makeText(
//            context,
//            "adb",
//            Toast.LENGTH_SHORT
//        ).show()
//        return LinuxCmd.adbShell(
//            terminalFragment,
//            "/data/user/0/com.puutaro.commandclick/files/gohello"
//        )
//    }
//}