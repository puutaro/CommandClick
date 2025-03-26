package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ReadSettingFileBuf {

//    private const val maxBufSize = 50
//    data class SettingFileBuf(
//        val path: String,
//        val checksum: String,
//        val contents: String,
//    )
//    private val mutex = ReentrantReadWriteLock()
//    private val bufList = mutableListOf<SettingFileBuf>()

    fun read(path: String): String {
        return ReadText(path).readText()
//        val settingFileBuf = mutex.readLock().withLock {
//            bufList.firstOrNull {
//                    settingFileBufSrc ->
//                if(
//                    settingFileBufSrc.path != path
//                ) return@firstOrNull false
//                FileSystems.checkSum(path) ==
//                        settingFileBufSrc.checksum
//            }
//        }
//        if(
//            settingFileBuf != null
//        ) {
//            mutex.writeLock().withLock {
//                bufList.remove(settingFileBuf)
//                bufList.add(settingFileBuf)
//            }
//            return settingFileBuf.contents
//        }
//        val contents =  ReadText(path).readText()
//        mutex.writeLock().withLock {
//            if (bufList.size > maxBufSize) {
//                bufList.removeAt(bufList.lastIndex)
//            }
//            bufList.add(
//                SettingFileBuf(
//                    path,
//                    FileSystems.checkSum(path),
//                    contents
//                )
//            )
//        }
//        return contents
    }
}