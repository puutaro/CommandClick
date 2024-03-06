package com.puutaro.commandclick.service.lib.music_player.libs

import com.puutaro.commandclick.util.file.ReadText

object PlayListMaker {
    fun make(
        listFilePath: String,
        playMode: String?,
        onRoop: String?,
        playNumber: String?,
    ): List<String> {
        return makePlayList(
            listFilePath,
            playMode,
            onRoop,
            playNumber,
        ) ?: emptyList()
    }

    private fun makePlayList(
        listFilePath: String,
        playMode: String?,
        onRoop: String?,
        playNumber: String?,
    ): List<String>? {
        val fileListBeforePlayMode = ReadText(
            listFilePath
        ).textToList()
        val repeatTimes = 100
        return when(
            playMode
        ){
            PlayModeType.reverse.name -> {
                val fileListBeforePlayModeReversed =
                    fileListBeforePlayMode.reversed()
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayModeReversed
                (1..repeatTimes).map {
                    fileListBeforePlayModeReversed
                }.flatten()
            }
            PlayModeType.shuffle.name -> {
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayMode.shuffled()
                (1..repeatTimes).map {
                    fileListBeforePlayMode.shuffled()
                }.flatten()
            }
            PlayModeType.number.name -> {
                try {
                    val numberModeNum = playNumber?.toInt()
                        ?: -1
                    val fileListBeforePlayModeNumber =
                        listOf(fileListBeforePlayMode[numberModeNum-1])
                    if(
                        onRoop.isNullOrEmpty()
                    ) return fileListBeforePlayModeNumber
                    (1..repeatTimes * 10).map {
                        fileListBeforePlayModeNumber
                    }.flatten()
                } catch(e: Exception){
                    return null
                }
            }
            else -> {
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayMode
                (0..repeatTimes).map {
                    fileListBeforePlayMode
                }.flatten()
            }
        }
    }

    private enum class PlayModeType {
        shuffle,
        ordinaly,
        reverse,
        number
    }

}