package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import android.os.Build
import android.os.Environment
import android.system.Os
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems

import com.puutaro.commandclick.util.file.SdPath
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File
import java.lang.NullPointerException

class UbuntuFiles(
    context: Context,
    private val symlinker: Symlinker = Symlinker(),
) {
    companion object {
        val downloadDirPath = UsePath.emulatedPath +
                "/${Environment.DIRECTORY_DOWNLOADS}"
        val rootfsTarGzName = "rootfs.tar.gz"
        val downloadRootfsTarGzPath = File(downloadDirPath, rootfsTarGzName).absolutePath
        const val rootfsTarName = "rootfs.tar"
        const val waitQuizTsvName = "wait_quiz.tsv"
        const val ubuntuEnvTsvName = "ubuntu_env.tsv"
        const val supportDirName = "support"
        const val supportDirInUbuntu = "/${supportDirName}"
        const val startupFilePath = "${supportDirInUbuntu}/startup.sh"
        const val extractAudioStreamingMapShellName = "extract_audio_streaming_map.sh"
        val ubuntuExtraStartupShellsTsvPath = let {
            ScriptPreWordReplacer.replace(
                "${UsePath.fannelSettingsDirPath}/ubuntuExtraStartupShellPaths.tsv",
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName
            )
        }
        const val pulseAudioStartUpShellPath = "${supportDirInUbuntu}/pulse_setup.sh"
        const val extraStartupShellName = "extra_startup.sh"
        val mustProcessGrepCmdsTxt = "mustProcessGrepCmds.txt"
        val ubuntuBackupRootfsPath = File(UsePath.cmdclickUbuntuBackupDirPath, rootfsTarName).absolutePath
        const val rootfsDirName = "rootfs"
        val ubuntuBackupTempRootfsDirPath = File(UsePath.cmdclickUbuntuBackupTempDirPath, rootfsDirName).absolutePath
        fun getUbuntuBackupRootfsDirPath(): String {
            return when(
                isBackupRootfsInSd()
            ){
                false -> File(
                    UsePath.cmdclickUbuntuBackupDirPath,
                    rootfsDirName
                ).absolutePath
                else -> File(
                    SdPath.getSdUseRootPath(),
                    SdPath.rootfsRelativePath
                ).absolutePath
            }
        }

        private fun isBackupRootfsInSd(): Boolean {
            val useSdRootPath = SdPath.getSdUseRootPath().ifEmpty {
                return false
            }
            val backupRootfsDirPathObj =
                File(useSdRootPath, SdPath.rootfsRelativePath)
            return FileSystems.showDirList(
                backupRootfsDirPathObj.absolutePath
            ).isNotEmpty()
        }

        fun isUbuntuRestore(): Boolean {
            return FileSystems.showDirList(
                getUbuntuBackupRootfsDirPath()
            ).isNotEmpty()
        }

        fun getUbuntuBackupRootfsDirPathOnlyWrite(): String {
            val sdRootDirPath = SdPath.getSdUseRootPath()
            return when(
                sdRootDirPath.isEmpty()
            ) {
                false -> File(
                    sdRootDirPath,
                    SdPath.rootfsRelativePath
                ).absolutePath
                else -> File(
                    UsePath.cmdclickUbuntuBackupDirPath,
                    rootfsDirName
                ).absolutePath
            }
        }
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
    val filesOneRootfs = File("${filesDir}/1/rootfs")
    val filesOneRootfsSupportDir =
        File("${filesOneRootfs.absolutePath}/support")
    val extractAudioStreamingMapShell =
        File(filesOneRootfsSupportDir.absolutePath, extractAudioStreamingMapShellName)
    val extraStartupShell =
        File(filesOneRootfsSupportDir.absolutePath, extraStartupShellName)
    val filesOneRootfsSupportProcDir =
        File("${filesOneRootfsSupportDir.absolutePath}/proc")
    val filesOneRootfsSupportCmdDir =
        File("${filesOneRootfsSupportDir.absolutePath}/cmd")
    val rsyncDownloaderDirPath =
        File("${filesOneRootfsSupportDir.absolutePath}/rsync_downloader")
    val rsyncDownloaderShellPath =
        File("${rsyncDownloaderDirPath.absolutePath}/rsync_downloader.sh")
    val ubuntuManagerDirPath =
        File("${filesOneRootfsSupportDir.absolutePath}/ubuntu_manager")
    val ubuntuManagerShellPath =
        File("${ubuntuManagerDirPath.absolutePath}/launch_manager.sh")
    val filesOneRootfsSupportCommonDir =
        File("${filesOneRootfsSupportDir.absolutePath}/common")
    val filesOneRootfsEtcDir =
        File("${filesOneRootfs.absolutePath}/etc")
    val filesOneRootfsEtcProfile =
        File("${filesOneRootfsEtcDir.absolutePath}/profile")
    val filesUsrLocalBin =
        File("${filesOneRootfs.absolutePath}/usr/local/bin")
    val filesOneRootfsEtcDProfileDir =
        File("${filesOneRootfsEtcDir.absolutePath}/profile.d")
    val filesOneRootfsUsrLocalBin =
        File("${filesOneRootfs.absolutePath}/usr/local/bin/")
    val filesOneRootfsStorageDir =
        File("${filesOneRootfs.absolutePath}/storage")
    val ubuntuSetupCompFile = File(
        "${filesOneRootfsSupportDir.absolutePath}/ubuntuSetupComp.txt"
    )
    val ubuntuLaunchCompFile = File(
        "${filesOneRootfsSupportDir.absolutePath}/ubuntuLaunchComp.txt"
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

    @Throws(NullPointerException::class, NoSuchFileException::class, Exception::class)
    fun setupLinksForBusyBox() {
        supportDir.mkdirs()
        libDir.listFiles()!!.forEach { libFile ->
            val libFileName = libFile.name
            if(!libFileName.contains("busybox")) return@forEach
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