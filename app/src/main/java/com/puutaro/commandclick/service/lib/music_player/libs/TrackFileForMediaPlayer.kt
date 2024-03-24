package com.puutaro.commandclick.service.lib.music_player.libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

class InfoFileForMediaPlayer(
    private val playMode: String? = null ,
    private val playInfoFilePath: String? = null
) {

    fun makeFirstPlayPosi(
        onTrack: String?,
        infoFileName: String?,
        playMode: String?,
        fileListConBeforePlayMode: String?,
    ): Int {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "musicInfo.txt").absolutePath,
//            listOf(
//                "onTrack: ${onTrack}",
//                "inTrack: ${onTrack != onTrackValue}",
//                "infoFileName: ${infoFileName}",
//
//            ).joinToString("\n")
//        )
        val defaultIndex = 0
        if(
            onTrack.isNullOrEmpty()
            || infoFileName.isNullOrEmpty()
        ) return defaultIndex
        val previousPlayInfoMap = makePlayListInfoFromFile(
            infoFileName,
        )
        val currentPlayInfoMap = makeCurrentPlayInfoMap(
            playMode,
            fileListConBeforePlayMode,
        )
       val isEqual = PlayListInfoMapEqual.judge(
            previousPlayInfoMap,
            currentPlayInfoMap,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "musicInfo02.txt").absolutePath,
//            listOf(
//                "onTrack: ${onTrack}",
//                "inTrack: ${onTrack != onTrackValue}",
//                "infoFileName: ${infoFileName}",
//                "previousPlayInfoMap: ${previousPlayInfoMap}",
//                "currentPlayInfoMap: ${currentPlayInfoMap}",
//                "isEqual: ${isEqual}",
//
//                ).joinToString("\n")
//        )
        return when(isEqual){
            true ->
                previousPlayInfoMap.get(
                    InfoKey.INDEX.key
                )?.toInt()?: defaultIndex
            else -> defaultIndex
        }
    }

    private fun makeCurrentPlayInfoMap(
        playMode: String?,
        inFileListConBeforePlayMode: String?,
    ): Map<String, String?> {
        return mapOf (
            InfoKey.PLAY_MODE.key
                    to playMode,
            InfoKey.PLAY_LIST_CON.key
                    to makeConpareSrcFileListCon(inFileListConBeforePlayMode),
        )
    }

    private fun makePlayListInfoFromFile(
        infoFileName: String,
    ): Map<String, String> {
        val infoFilePath = File(
            UsePath.cmdclickTempMediaPlayerDirPath,
            infoFileName
        ).absolutePath
        return ReadText(
            infoFilePath
        ).readText().replace("\t", "=").let {
            CmdClickMap.createMap(
                it,
                '\n'
            )
        }.toMap()
    }

    fun savePlayInfo(
        index: Int,
        fileListConBeforePlayMode: String?
    ){
        if(
            playInfoFilePath.isNullOrEmpty()
        ) return
        val playInfoCon = mapOf (
            InfoKey.INDEX.key to index.toString(),
            InfoKey.PLAY_MODE.key
                    to playMode,
            InfoKey.PLAY_LIST_CON.key
                    to makeConpareSrcFileListCon(fileListConBeforePlayMode),
        ).map {
            "${it.key}\t${it.value}"
        }.joinToString("\n")
        playInfoFilePath.let {
            FileSystems.writeFile(
                it,
                playInfoCon
            )
        }
    }

    private fun makeConpareSrcFileListCon(
        fileListConBeforePlayMode: String?
    ): String {
        if(
            fileListConBeforePlayMode.isNullOrEmpty()
        ) return String()
        return fileListConBeforePlayMode.replace(
            "\n",
            " "
        ).replace(
            "\t",
        "TTTAAABBB",
        )
    }
}

private object PlayListInfoMapEqual {
    fun judge(
        previousPlayInfoMap: Map<String, String?>?,
        currentPlayInfoMap: Map<String, String?>?,
    ): Boolean {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "musicPlayInfoCompare.txt").absolutePath,
//            listOf(
//                "previousPlayInfoMap: ${previousPlayInfoMap}",
//                "currentPlayInfoMap: ${currentPlayInfoMap}",
//                "is2: ${currentPlayInfoMap?.all {
//                    val keyName = it.key
//                    previousPlayInfoMap?.get(keyName).toString() ==
//                            it.value.toString()
//                } ?: false}"
//            ).joinToString("\n\n\n")
//        )
        return currentPlayInfoMap?.all {
            val keyName = it.key
            previousPlayInfoMap?.get(keyName).toString() ==
                    it.value.toString()
        } ?: false

    }

}

private enum class InfoKey(
    val key: String,
){
    INDEX("index"),
    PLAY_MODE("playMode"),
    PLAY_LIST_CON("playListCon"),
}
