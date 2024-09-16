package com.puutaro.commandclick.util.gz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.exists

object GzTool {

    suspend fun extractTarWithoutOwnership(
        tarPath: String,
        destPath: String,
        onGzip: Boolean
    ) {
        withContext(Dispatchers.IO) {
            when (onGzip) {
                true -> TarArchiveInputStream(
                    BufferedInputStream(
                        GzipCompressorInputStream(
                            FileInputStream(tarPath)
                        )
                    )
                )
                else -> TarArchiveInputStream(
                    BufferedInputStream(
                        FileInputStream(tarPath)
                    )
                )
            }.use { tis ->
                var entry = tis.nextTarEntry
                while (entry != null) {
                    val targetPath = Paths.get(destPath, entry.name)
                    when {
                        targetPath.exists() ->{}
                        entry.isDirectory -> {
                            try {
                                Files.createDirectories(targetPath)
                            } catch (e: Exception){
                                println(e.toString())
                            }
//                        println("Creating directory: $targetPath")
                        }
                        entry.isSymbolicLink -> {
                            try {
                                val linkTarget = entry.linkName
                                Files.createSymbolicLink(targetPath, Paths.get(linkTarget))
                            } catch (e: Exception){
                                println(e.toString())
                            }
//                        println("Creating symlink: $targetPath -> $linkTarget")
                        }
                        else -> {
                            try {
                                Files.createDirectories(targetPath.parent)
                                Files.newOutputStream(targetPath).use { out ->
                                    tis.copyTo(out)
                                }
                            } catch (e: Exception){
                                println(e.toString())
                            }
//                        println("Extracting file: $targetPath")
                        }
                    }
                    entry = tis.nextTarEntry
                }
            }
        }
    }
}