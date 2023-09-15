package com.puutaro.commandclick.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset


object LinuxCmd {
    fun exec(
        fragment: Fragment,
        cmdList: String
    ): String {
        try {
            val pb = ProcessBuilder().command(
                cmdList.split("\t")
            ).redirectErrorStream(true)
            val process = pb.start()
            val output = process.waitFor()

            var errContents = String()
            BufferedReader(
                InputStreamReader(
                    process.errorStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    errContents += "\n" + line
//                println(line)
                }
            }
            var outputContents = String()
            BufferedReader(
                InputStreamReader(
                    process.inputStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    outputContents += "\n" + line
//                println(line)
                }
            }
            return outputContents + "\n" + errContents
        } catch (e: Exception){
            return e.toString()
//            Toast.makeText(
//                fragment.context,
//                e.toString(),
//                Toast.LENGTH_LONG
//            ).show()
            return e.toString()
        }
    }

    fun execNoWait(
        cmdList: String
    ) {
        try {
            val pb = ProcessBuilder().command(
                cmdList.split("\t")
            ).redirectErrorStream(true)
            pb.start()
        } catch (e: Exception){
            println("pass")
        }
    }

    fun adbShell(
        fragment: Fragment,
        cmd: String
    ): String {
        try {
            val process = Runtime.getRuntime().exec(cmd)
            process.waitFor()
            var errContents = String()
            BufferedReader(
                InputStreamReader(
                    process.errorStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    errContents += "\n" + line
//                println(line)
                }
            }
            var outputContents = String()
            BufferedReader(
                InputStreamReader(
                    process.inputStream,
                    Charset.defaultCharset()
                )
            ).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    outputContents += "\n" + line
//                println(line)
                }
            }
            return outputContents + "\n" + errContents
        } catch(e: Exception){
            Toast.makeText(
                fragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
            return e.toString()
        }
    }
}