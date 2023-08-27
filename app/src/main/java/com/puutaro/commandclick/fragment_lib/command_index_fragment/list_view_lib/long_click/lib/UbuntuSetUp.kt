package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.res.AssetManager
import android.os.Environment
import android.system.Os
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory.TAR
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.CompressorStreamFactory.GZIP
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory.createArchiver
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.time.LocalDateTime


object UbuntuSetUp {

    private val genRootDir = "/data/user/0/com.puutaro.commandclick/files/ubuntu-gen"
    private val ubuntuRfs = "$genRootDir/ubuntuARM64-fs"
    private val ubuntuGenShellPath = "${genRootDir}/android-ubuntu-gen.sh"
    private val startUbuntuShellPath = "${genRootDir}/start-ubuntu.sh"
    private val ubuntuTarGzPath = "${genRootDir}/ubuntu.tar.gz"
    private val prootPath = "${genRootDir}/proot/bin/proot"
    private val busyboxPath = "${genRootDir}/busybox"
    private val libAndroidShmem = "${genRootDir}/libandroid-shmem.so"
    private val libDisableSeLinux = "${genRootDir}/libdisableselinux.so"
    private val prootMp2 = "${genRootDir}/proot.mp2"


    fun set(
        cmdIndexFragment: CommandIndexFragment
    ){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                execSet(cmdIndexFragment)
            }
        } catch (e: Exception){
            Toast.makeText(
                cmdIndexFragment.context,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    suspend fun execSet(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val context = cmdIndexFragment.context
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            terminalViewModel.currentMonitorFileName,
            "${LocalDateTime.now()}\n"
        )
        FileSystems.removeDir(
            genRootDir
        )
        FileSystems.createDirs(
            genRootDir
        )
//        FileSystems.copyDirectory(
//            "/storage/emulated/0/Documents/cmdclick/AppDir/default/ubuntu-gen",
//            genRootDir
//        )
//        FileSystems.createDirs(
//            ubuntuRfs
//        )
//
//
//        val srcFile: File = File(ubuntuTarGzPath)
//        val desDir = File(ubuntuRfs)
//
//        Log.e("desDir", desDir.exists().toString())
//        // desDir.delete();
//        // desDir.delete();
//        withContext(Dispatchers.Main){
//            Toast.makeText(
//                context,
//                "start decompress",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        decompressTarGz(
//            ubuntuTarGzPath,
//            ubuntuRfs
//        )
//        withContext(Dispatchers.Main){
//            Toast.makeText(
//                context,
//                "start chmod",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        LinuxCmd.exec(
//            cmdIndexFragment,
//            listOf(
//                "chmod",
//                "-R",
//                "+x",
//                genRootDir
//            ).joinToString("\t")
//        )
//
//
//
////        copyAssetsExecute("proot.mp2");
////        copyAssetsExecute("busybox");
//////        copyAssetsExecute("bash");
////        copyAssetsExecute("libandroid-shmem.so");
////        copyAssetsExecute("libdisableselinux.so")
//
//        val tempFile: File = File(genRootDir + "/temp")
//        if (tempFile.exists() == false) tempFile.mkdirs()
//        Os.setenv("LD_PRELOAD", genRootDir + "/libdisableselinux.so", true)
//        Os.setenv("PROOT_TMP_DIR", genRootDir + "/temp", true)
//        withContext(Dispatchers.Main){
//            Toast.makeText(
//                context,
//                "start ubunut",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        runProot(
//            genRootDir + "/proot.mp2 -R " + ubuntuRfs,
//            terminalViewModel,
//        )
//        return




        FileSystems.copyDirectory(
            "/storage/emulated/0/Documents/cmdclick/AppDir/default/ubuntu-gen",
            genRootDir
        )
        FileSystems.createDirs(
            ubuntuRfs
        )
        val dirList = FileSystems.showDirList(genRootDir).joinToString("\n")
        val fileList = FileSystems.sortedFiles(genRootDir).joinToString("\n")
        val isGenShell = File(ubuntuGenShellPath).isFile
        val envs = Os.environ().toList().joinToString("\n")

//        val err = LinuxCmd.exec(
//            cmdIndexFragment,
//            listOf(
//                "chmod",
//                "-R",
//                "777",
//                genRootDir
//            ).joinToString("\t")
//        )
        val err2 = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "chmod",
                "-R",
                "+x",
                genRootDir
            ).joinToString("\t")
        )

        val monitorCon = ReadText(
            UsePath.cmdclickMonitorDirPath,
            terminalViewModel.currentMonitorFileName,
        ).readText()

        withContext(Dispatchers.Main){
            Toast.makeText(
                context,
                "start decompress",
                Toast.LENGTH_SHORT
            ).show()
        }
        decompressTarGz(
            ubuntuTarGzPath,
            ubuntuRfs
        )
        withContext(Dispatchers.Main){
            Toast.makeText(
                context,
                "start chmod",
                Toast.LENGTH_SHORT
            ).show()
        }
        val fileList2 = FileSystems.showDirList(
            ubuntuRfs
        ).joinToString("\n")
        val err3 = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "chmod",
                "-R",
                "+x",
                ubuntuRfs
            ).joinToString("\t")
        )
        withContext(Dispatchers.Main){
            Toast.makeText(
                context,
                "start ubuntu",
                Toast.LENGTH_SHORT
            ).show()
        }
        val result = LinuxCmd.exec(
            cmdIndexFragment,
            listOf(
                "sh",
                startUbuntuShellPath,
            ).joinToString("\t")
        )
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            terminalViewModel.currentMonitorFileName,
            "dirList: $dirList\n" +
                    "envs: $envs\n fileList: $fileList\nisGenShell: ${isGenShell}\nfileList2: ${fileList2}\nresult: ${result}---"
        )
//        Toast.makeText(
//            context,
//            "${err}\n${dirList}\n${fileList}---",
//            Toast.LENGTH_LONG
//        ).show()
//        val result2 = LinuxCmd.exec(
//            cmdIndexFragment,
//            listOf(
//                "sh",
//                startUbuntuShellPath,
//                ">> ${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
//            ).joinToString("\t")
//        )
//        Toast.makeText(
//            context,
//            "result\n${result}\n${result2}",
//            Toast.LENGTH_LONG
//        ).show()
//        val monitorCon2 = ReadText(
//            UsePath.cmdclickMonitorDirPath,
//            terminalViewModel.currentMonitorFileName,
//        ).readText()
//        FileSystems.writeFile(
//            UsePath.cmdclickMonitorDirPath,
//            terminalViewModel.currentMonitorFileName,
//            "$monitorCon2\n\n$result end"
//        )
    }

    @Throws(IOException::class)
    private fun unTarGz(archive: File) {
        val archiver: Archiver = createArchiver(TAR, GZIP)
        archiver.extract(archive, archive.parentFile)
//        log.trace("unTarGz {}", archive)
    }

    private fun decompressTarGz(
        inputFilePath: String,
        outputDirPath: String) {
        // 出力先フォルダの作成
        File(outputDirPath).mkdirs()

        // tarファイル名を作成
        val outputTarFile =
            inputFilePath.substring(inputFilePath.lastIndexOf("/")).replace(".tar.gz", ".tar.tmp")
        var outputTarDir = if (outputDirPath.endsWith("/")) outputDirPath.substring(
            0,
            outputDirPath.lastIndexOf("/")
        ) else outputDirPath
        outputTarDir = outputTarDir.substring(0, outputTarDir.lastIndexOf("/"))
        val outputTarPath = "$outputTarDir/$outputTarFile"
        FileInputStream(inputFilePath).use { fis ->
            GzipCompressorInputStream(fis).use { archive ->
                FileOutputStream(outputTarPath).use { fos ->
                    // エントリーの中身を出力
                    var size = 0
                    val buf = ByteArray(1024)
                    while (archive.read(buf).also { size = it } > 0) {
                        fos.write(buf, 0, size)
                    }
                }
            }
        }
        FileInputStream(outputTarPath).use { fis ->
            TarArchiveInputStream(fis).use { tais ->
                // エントリーを1つずつファイル・フォルダに復元
                var entry: ArchiveEntry? = null
                while (tais.nextEntry.also { entry = it } != null) {
                    // ファイルを作成
                    val file = File(outputDirPath + "/" + entry?.name)

                    // フォルダ・エントリの場合はフォルダを作成して次へ
                    if (entry?.isDirectory == true) {
                        file.mkdirs()
                        continue
                    }

                    // ファイル出力する場合、
                    // フォルダが存在しない場合は事前にフォルダ作成
                    if (file.parentFile?.exists() == false) {
                        file.parentFile?.mkdirs()
                    }
                    FileOutputStream(file).use { fos ->
                        BufferedOutputStream(fos).use { bos ->
                            // エントリの出力
                            val size = 0
                            val buf = ByteArray(1024)
                            while (tais.read(buf) > 0) {
                                bos.write(buf, 0, size)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun runCmd(
        cmd: String,
        terminalViewModel: TerminalViewModel,
    ) {
        try {
            val process = Runtime.getRuntime().exec(cmd)
            var reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            var read: Int
            var buffer = CharArray(4096)
            var output = StringBuffer()
            while (reader.read(buffer).also { read = it } > 0) {
                output.append(buffer, 0, read)
            }
            reader.close()
            process.waitFor()
            if (output.toString().length != 0) Log.e("output-msg", "output: $output") else {
                reader = BufferedReader(
                    InputStreamReader(process.errorStream)
                )
                buffer = CharArray(4096)
                output = StringBuffer()
                while (reader.read(buffer).also { read = it } > 0) {
                    output.append(buffer, 0, read)
                }
                reader.close()
                Log.e("output-error-msg", output.toString())

                FileSystems.writeFile(
                    UsePath.cmdclickMonitorDirPath,
                    terminalViewModel.currentMonitorFileName,
                    "$output"
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun runExecFile(cmd: String, fileName: String) {
        val busyBox: File = File(genRootDir + "/" + fileName)
        if (busyBox.exists() == false) {
            Log.e("error-$fileName", "cannot find $fileName")
        } else {
            busyBox.setExecutable(true)
            try {
                val process = Runtime.getRuntime().exec(cmd)
                var reader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )
                var read: Int
                var buffer = CharArray(4096)
                var output = StringBuffer()
                while (reader.read(buffer).also { read = it } > 0) {
                    output.append(buffer, 0, read)
                }
                reader.close()
                process.waitFor()
                if (output.toString().length != 0) Log.e("output-msg", "output: $output") else {
                    reader = BufferedReader(
                        InputStreamReader(process.errorStream)
                    )
                    buffer = CharArray(4096)
                    output = StringBuffer()
                    while (reader.read(buffer).also { read = it } > 0) {
                        output.append(buffer, 0, read)
                    }
                    reader.close()
                    Log.e("output-error-msg", output.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun runProot(
        cmd: String,
        terminalViewModel: TerminalViewModel,
    ) {
        val desDir = File(ubuntuRfs)
        val checkProot: File = File(genRootDir + "/proot.mp2")
        if (desDir.exists() == false) Log.e(
            "file-error",
            "ubuntuRfs not found"
        ) else if (checkProot.exists() == false) {
            Log.e("file-error", "proot.mp2 not found")
        } else {
            //run cmd
            checkProot.setExecutable(true)
            try {
                val process = Runtime.getRuntime().exec(cmd)
                var reader = BufferedReader(
                    InputStreamReader(process.inputStream)
                )
                var read: Int
                var buffer = CharArray(4096)
                var output = StringBuffer()
                while (reader.read(buffer).also { read = it } > 0) {
                    output.append(buffer, 0, read)
                }
                reader.close()
                process.waitFor()
                if (output.toString().length != 0) Log.e("output-msg", "output: $output") else {
                    reader = BufferedReader(
                        InputStreamReader(process.errorStream)
                    )
                    buffer = CharArray(4096)
                    output = StringBuffer()
                    while (reader.read(buffer).also { read = it } > 0) {
                        output.append(buffer, 0, read)
                    }
                    reader.close()
                    Log.e("output-error-msg", output.toString())
                    FileSystems.writeFile(
                        UsePath.cmdclickMonitorDirPath,
                        terminalViewModel.currentMonitorFileName,
                        "$output"
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

//    private fun copyAssetsExecute(fileName: String) {
//        val appFileDirectory: String = genRootDir
//        val assets: AssetManager = getAssets()
//        val `in`: InputStream
//        val out: OutputStream
//        try {
//            `in` = assets.open(fileName)
//            val outFile = File(appFileDirectory, fileName)
//            out = FileOutputStream(outFile)
//
//            //Apache common io libs jar https://commons.apache.org/proper/commons-io/
//            IOUtils.copy(`in`, out)
//            `in`.close()
//            out.flush()
//            out.close()
//            Log.e("copy-status", "Copy success: $fileName")
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.e("error-copy", e.message.toString())
//        }
//    }

//    private fun copyAssetsExternal(fileName: String, appFileDirectory: String) {
//        val assetManager: AssetManager = getAssets()
//        val `in`: InputStream
//        var out: OutputStream? = null
//        Log.e(
//            "copy-status",
//            "Attempting to copy this file: $fileName"
//        ) // + " to: " +       assetCopyDestination);
//        try {
//            `in` = assetManager.open(fileName)
//            Log.e("copy-status", "outDir: $appFileDirectory")
//            val outFile = File(appFileDirectory, fileName)
//            out = FileOutputStream(outFile)
//
//            //Apache common io libs jar https://commons.apache.org/proper/commons-io/
//            IOUtils.copy(`in`, out)
//            `in`.close()
//            out.flush()
//            out.close()
//            Log.d("copy-status", "Copy success: $fileName")
//        } catch (e: IOException) {
//            Log.e("copy-status", "Failed to copy asset file: $fileName", e)
//        } finally {
//            IOUtils.closeQuietly(out)
//        }
//    }

    @Throws(IOException::class)
    fun doDecompress(srcFile: File?, destDir: File?) {
        var `is`: TarArchiveInputStream? = null
        try {
            `is` = TarArchiveInputStream(BufferedInputStream(FileInputStream(srcFile), 4096))
            var entry: TarArchiveEntry? = null
            while (`is`.nextTarEntry.also { entry = it } != null) {
                if (entry!!.isDirectory) {
                    Log.e("dir-name", entry!!.name.toString())
                    val directory = File(destDir, entry!!.name)
                    directory.mkdirs()
                } else {
                    var os: OutputStream? = null
                    try {
                        os = BufferedOutputStream(
                            FileOutputStream(File(destDir, entry!!.name)), 4096
                        )
                        IOUtils.copy(`is`, os)
                    } finally {
                        IOUtils.closeQuietly(os)
                    }
                }
            }
        } finally {
            IOUtils.closeQuietly(`is`)
        }
    }
}
