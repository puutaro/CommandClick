package com.puutaro.commandclick.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.system.Os
import com.puutaro.commandclick.common.variable.UsePath
import java.io.File
import java.lang.NullPointerException

class UbuntuFiles(
    context: Context,
    private val symlinker: Symlinker = Symlinker(),
) {
    companion object {
        val downloadDirPath = Environment.getExternalStorageDirectory().absolutePath +
                "/${Environment.DIRECTORY_DOWNLOADS}"
        val rootfsTarGzName = "rootfs.tar.gz"
        val downloadRootfsTarGzPath = "${downloadDirPath}/${rootfsTarGzName}"
    }
    val libDirPath = context.applicationInfo.nativeLibraryDir
    val filesDir: File = context.filesDir
    val libDir: File = File(libDirPath)
    val supportDir: File = File(filesDir, "support")
    val emulatedScopedDir = context.getExternalFilesDir(null)!!
    val emulatedUserDir = File(emulatedScopedDir, "storage")

    val sdCardScopedDir: File? = resolveSdCardScopedStorage(context)
    val sdCardUserDir: File? = if (sdCardScopedDir != null) {
        File(sdCardScopedDir, "storage")
    } else null

    val busybox = File(supportDir, "busybox")
    val proot = File(supportDir, "proot")
    val documentDirPath = "${Environment.getExternalStorageDirectory().absolutePath}/" +
            Environment.DIRECTORY_DOCUMENTS
    val filesOneRootfs = File("${filesDir}/1/rootfs")
    val filesOneRootfsHomeCmdclickCmdDir =
        File("${filesOneRootfs.absolutePath}/home/cmdclick/cmd")
    val cmdShell = "cmd.sh"
    val filesOneRootfsSupportDir =
        File("${filesOneRootfs.absolutePath}/support")
    val filesOneRootfsSupportCommonDir =
        File("${filesOneRootfsSupportDir.absolutePath}/common")
    val filesOneRootfsEtcDir =
        File("${filesOneRootfs.absolutePath}/etc")
    val filesOneRootfsEtcDProfileDir =
        File("${filesOneRootfsEtcDir.absolutePath}/profile.d")
    val filesOneRootfsUsrLocalBinSudo =
        File("${filesOneRootfs.absolutePath}/usr/local/bin/sudo")
    val filesOneRootfsStorageDir =
        File("${filesOneRootfs.absolutePath}/storage")
    val ubuntuCompFile = File(
        "${UsePath.cmdclickDefaultAppDirPath}/ubuntuComp.txt"
    )

    fun makePermissionsUsable(containingDirectoryPath: String, filename: String) {
        val commandToRun = arrayListOf("chmod", "0777", filename)

        val containingDirectory = File(containingDirectoryPath)
        containingDirectory.mkdirs()

        val pb = ProcessBuilder(commandToRun)
        pb.directory(containingDirectory)

        val process = pb.start()
        process.waitFor()
    }

    private fun resolveSdCardScopedStorage(context: Context): File? {
        // Allegedly returns at most 2 elements, if there is a physical external storage device,
        // according to https://developer.android.com/training/data-storage/files at
        // 'Select between multiple storage locations'
        val externals = context.getExternalFilesDirs(null)
        return if (externals.size > 1) {
            externals[1]
        } else null
    }

    // Lib files must start with 'lib' and end with '.so.'
    private fun String.toSupportName(): String {
        return this.substringAfter("lib_").substringBeforeLast(".so")
    }

    @Throws(NullPointerException::class, NoSuchFileException::class, Exception::class)
    fun setupLinks() {
        supportDir.mkdirs()

        libDir.listFiles()!!.forEach { libFile ->
            var libFileName = libFile.name
            if (libFileName.startsWith("lib_proot.") ||
                    libFileName.startsWith("lib_libtalloc") ||
                    libFileName.startsWith("lib_loader")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (libFileName.endsWith(".a10.so")) {
                        libFileName = libFileName.replace(".a10.so", ".so")
                    } else {
                        return@forEach
                    }
                } else {
                    if (libFileName.endsWith(".a10.so")) {
                        return@forEach
                    }
                }
            }
            val name = libFileName.toSupportName()
            val linkFile = File(supportDir, name)
            linkFile.delete()
            symlinker.createSymlink(libFile.path, linkFile.path)
        }
    }

    fun getArchType(): String {
        val usedABI = File(libDir, "lib_arch.so").readText()
        return translateABI(usedABI)
    }

    private fun translateABI(abi: String): String {
        return when (abi) {
            "arm64-v8a" -> "arm64"
            "armeabi-v7a" -> "arm"
            "x86_64" -> "x86_64"
            "x86" -> "x86"
            else -> ""
        }
    }
}

class Symlinker {
    fun createSymlink(targetPath: String, linkPath: String) {
        Os.symlink(targetPath, linkPath)
    }
}