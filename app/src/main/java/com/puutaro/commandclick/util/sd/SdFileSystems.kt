package com.puutaro.commandclick.util.file

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.MimeType
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.Job
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter


object SdFileSystems {

    fun removeDirOrFile(
        context: Context?,
        ubuntuBackupSharePref: SdCardTool.UbuntuBackupSharePref?,
        dirName: String?,
        deleteDirOrFileName: String,
        watchFilePathObj: File?,
    ){
        try {
            val dir = createDir(
                context,
                ubuntuBackupSharePref,
                dirName
            ) ?: return
            dir.findFile(deleteDirOrFileName)?.delete()
            watchFilePathObj?.absolutePath?.let {
                FileSystems.removeFiles(it)
            }
        } catch (e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }  finally {
            watchFilePathObj?.absolutePath?.let {
                FileSystems.removeFiles(it)
            }
        }
    }

    fun createTextFile(
        context: Context?,
        ubuntuBackupSharePref: SdCardTool.UbuntuBackupSharePref?,
        dirName: String?,
        fileName: String,
        con: String,
    ){
        if(
            context == null
        ) return
        val dir = createDir(
            context,
            ubuntuBackupSharePref,
            dirName
        ) ?: return
        val file = dir.createFile(MimeType.TEXT, fileName)
            ?: return
        val out = context.contentResolver.openOutputStream(file.uri)
        OutputStreamWriter(out).use{
            it.write(con)
        }
    }

    object Copy {
        fun copy(
            context: Context?,
            ubuntuBackupSharePref: SdCardTool.UbuntuBackupSharePref?,
            sourceFilePath: String,
            relativeDirPath: String,
            watchFilePathObj: File?,
        ) {
            val relativePathObj = File(relativeDirPath)
            removeDirOrFile(
                context,
                ubuntuBackupSharePref,
                relativePathObj.parent,
                relativePathObj.name,
                null
            )
            try {
                val destinationUri = createDir(
                    context,
                    ubuntuBackupSharePref,
                    relativeDirPath,
                )?.uri ?: return
                val isSdSrc =
                    isSdPath(sourceFilePath)
                when (isSdSrc) {
                    true -> execCopyInSd(
                        context,
                        sourceFilePath,
                        destinationUri,
                    )

                    else -> execCopy(
                        context,
                        sourceFilePath,
                        destinationUri,
                    )
                }
            } catch (e: Exception){
                LogSystems.stdErr(
                    context,
                    e.toString()
                )
            } finally {
                watchFilePathObj?.absolutePath?.let {
                    FileSystems.removeFiles(it)
                }
            }
        }

        private fun execCopy(
            context: Context?,
            sourceFilePath: String,
            destinationUri: Uri,
        ) {
            val resolver = context?.contentResolver
                ?: return
            val sourceFile = File(sourceFilePath)
            val newFile = DocumentsContract.createDocument(
                resolver,
                destinationUri,
                mimeTypeFromExtension(sourceFile.extension),
                sourceFile.name
            ) ?: return
            val inputStream = FileInputStream(sourceFile)
            val outputStream = resolver.openOutputStream(newFile)
                ?: return
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        }

        private fun execCopyInSd(
            context: Context?,
            sourceFilePath: String,
            destinationUri: Uri,
        ) {
            val resolver = context?.contentResolver
                ?: return
            val sourceFile = DocumentFile.fromFile(
                File(sourceFilePath)
            )
            val newFile = DocumentsContract.createDocument(
                resolver,
                destinationUri,
                mimeTypeFromExtension(
                    CcPathTool.subExtend(sourceFilePath)
                ),
                File(sourceFilePath).name
            ) ?: return
            val inputStream = resolver.openInputStream(sourceFile.uri)
                ?: return
            val outputStream = resolver.openOutputStream(newFile)
                ?: return
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        }
    }

    object CopyDirRecursively {

        fun copy(
            context: Context?,
            ubuntuBackupSharePref: SdCardTool.UbuntuBackupSharePref?,
            sourceDirPath: String,
            relativeDirPath: String,
            watchFilePathObj: File?,
            cpWatchJob: Job?
        ) {
            val relativePathObj = File(relativeDirPath)
            removeDirOrFile(
                context,
                ubuntuBackupSharePref,
                relativePathObj.parent,
                relativePathObj.name,
                null
            )
            try {
                val destinationUri = createDir(
                    context,
                    ubuntuBackupSharePref,
                    relativeDirPath,
                )?.uri ?: return
                val isSdSrc =
                    isSdPath(sourceDirPath)
                when (isSdSrc) {
                    true -> execCopyInSd(
                        context,
                        sourceDirPath,
                        destinationUri,
                        watchFilePathObj,
                        cpWatchJob,
                    )

                    else -> execCopy(
                        context,
                        sourceDirPath,
                        destinationUri,
                        watchFilePathObj,
                        cpWatchJob,
                    )
                }
            } catch (e: Exception){
                LogSystems.stdErr(
                    context,
                    e.toString()
                )
            } finally {
                watchFilePathObj?.absolutePath?.let {
                    FileSystems.removeFiles(it)
                }
            }
        }

        private fun execCopy(
            context: Context?,
            sourceDirPath: String,
            destinationUri: Uri,
            watchFilePathObj: File?,
            cpWatchJob: Job?
        ) {
            if (
                context == null
            ) return
            val resolver = context.contentResolver
            val sourceDir = File(sourceDirPath)
            val fileList = sourceDir.listFiles()
                ?: return
            for (file in fileList) {
                if (
                    watchFilePathObj != null
                    && !watchFilePathObj.isFile
                ) break
                if (
                    cpWatchJob != null
                    && cpWatchJob.isCancelled
                ) {
                    watchFilePathObj?.absolutePath?.let {
                        FileSystems.removeFiles(it)
                    }
                    break
                }
                when (true) {
                    file.isDirectory -> {
                        val newDir = DocumentsContract.createDocument(
                            resolver,
                            destinationUri,
                            DocumentsContract.Document.MIME_TYPE_DIR,
                            file.name
                        ) ?: continue
                        execCopy(
                            context,
                            file.absolutePath,
                            newDir,
                            watchFilePathObj,
                            cpWatchJob
                        )
                    }

                    else -> {
                        val newFile = DocumentsContract.createDocument(
                            resolver,
                            destinationUri,
                            mimeTypeFromExtension(file.extension),
                            file.name
                        ) ?: continue
                        val inputStream = FileInputStream(file)
                        val outputStream = resolver.openOutputStream(newFile)
                            ?: continue
                        copyStream(inputStream, outputStream)
                        inputStream.close()
                        outputStream.close()
                    }
                }
            }
        }

        private fun execCopyInSd(
            context: Context?,
            sourceDirPath: String,
            destinationUri: Uri,
            watchFilePathObj: File?,
            cpWatchJob: Job?
        ) {
            if (
                context == null
            ) return
            val resolver = context.contentResolver
            val srcDocFile = DocumentFile.fromFile(
                File(sourceDirPath)
            )
            val fileList = srcDocFile.listFiles()
            for (file in fileList) {
                if (
                    watchFilePathObj != null
                    && !watchFilePathObj.isFile
                ) break
                val name = file.name
                if (
                    cpWatchJob != null
                    && cpWatchJob.isCancelled
                    || name.isNullOrEmpty()
                ) {
                    watchFilePathObj?.absolutePath?.let {
                        FileSystems.removeFiles(it)
                    }
                    break
                }
                when (true) {
                    file.isDirectory -> {
                        val newDir = DocumentsContract.createDocument(
                            resolver,
                            destinationUri,
                            DocumentsContract.Document.MIME_TYPE_DIR,
                            name
                        ) ?: continue
                        execCopyInSd(
                            context,
                            file.getAbsolutePath(context),
                            newDir,
                            watchFilePathObj,
                            cpWatchJob
                        )
                    }

                    else -> {
                        val newFile = DocumentsContract.createDocument(
                            resolver,
                            destinationUri,
                            mimeTypeFromExtension(CcPathTool.subExtend(name)),
                            name
                        ) ?: continue

                        val inputStream = resolver.openInputStream(file.uri)
                            ?: continue
                        val outputStream = resolver.openOutputStream(newFile)
                            ?: continue
                        copyStream(inputStream, outputStream)
                        inputStream.close()
                        outputStream.close()
                    }
                }
            }
        }
    }

    private fun isSdPath(
        path: String,
    ): Boolean {
        return !path.startsWith("/")
                || path.startsWith(SdPath.getSdUseRootPath())
    }

    // Helper function to get mime type from file extension (optional)
    private fun mimeTypeFromExtension(extension: String): String {
        if(
            extension == "tar"
            || extension == "gz"
        ) return MimeType.BINARY_FILE
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
    }

    // Helper function to copy stream (optional)
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while ((inputStream.read(buffer).also { bytesRead = it }) > 0) {
            outputStream.write(buffer, 0, bytesRead)
        }
    }

    private fun createDir(
        context: Context?,
        ubuntuBackupSharePref: SdCardTool.UbuntuBackupSharePref?,
        dirPath: String?,
    ): DocumentFile? {
        if(
            context == null
        ) return null

        val document = SdCardTool.getTreeUri(
            context,
            ubuntuBackupSharePref,
        ) ?: return null
        if(
            dirPath.isNullOrEmpty()
        ) return document
        var dir: DocumentFile? = document
        dirPath.split("/").forEach {
            val findDir = dir?.findFile(it)
            if (
                findDir != null && findDir.isDirectory
            ) {
                dir = findDir
                return@forEach
            }
            dir = dir?.createDirectory(it)
        }
        return dir
    }
}