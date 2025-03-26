package com.puutaro.commandclick.proccess.edit.image_action

import android.graphics.Bitmap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ImageActionBitmapData {
    data class ImportData(
        val importPath: String,
        val key: String,
        val bitmap: Bitmap?,
    )
    private val asyncImportKeyToBitmapMutex = ReentrantReadWriteLock()
    private val importKeyToBitmap: HashSet<ImportData> = hashSetOf()
    suspend fun gets(
    ): List<Triple<String, String, Bitmap?>> {
        return asyncImportKeyToBitmapMutex.readLock().withLock {
            importKeyToBitmap.map {
                    importData ->
                Triple (
                    importData.importPath,
                    importData.key,
                    importData.bitmap,
                )
            }
        }
    }
    suspend fun get(
        importPath: String,
        key: String,
    ): Bitmap? {
        return asyncImportKeyToBitmapMutex.readLock().withLock {
            importKeyToBitmap.firstOrNull {
                    importData ->
                importData.importPath == importPath
                        && importData.key == key
            }?.bitmap
        }
    }

    suspend fun put(
        importPath: String,
        key: String,
        bitmap: Bitmap?,
    ){
        asyncImportKeyToBitmapMutex.writeLock().withLock {
            val existData = importKeyToBitmap.filter {
                    importData ->
                importData.importPath == importPath
                        && importData.key == key
            }
            existData.forEach {
                importKeyToBitmap.remove(it)
            }
            importKeyToBitmap.add(
                ImportData(
                    importPath,
                    key,
                    bitmap,
                )
            )
        }
    }

    suspend fun clear(){
        asyncImportKeyToBitmapMutex.writeLock().withLock {
            importKeyToBitmap.clear()
        }
    }
}