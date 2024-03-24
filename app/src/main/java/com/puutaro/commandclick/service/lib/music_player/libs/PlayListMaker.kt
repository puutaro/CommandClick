package com.puutaro.commandclick.service.lib.music_player.libs

object PlayListMaker {
    fun make(
        inFileListBeforePlayMode: List<String>,
        playMode: String?,
        onRoop: String?,
        playNumber: String?,
    ): List<String> {
        return makePlayList(
            inFileListBeforePlayMode,
            playMode,
            onRoop,
            playNumber,
        ) ?: emptyList()
    }

    private fun makePlayList(
        inFileListBeforePlayMode: List<String>,
        playMode: String?,
        onRoop: String?,
        playNumber: String?,
    ): List<String>? {
        val repeatTimes = 100
        return when(
            playMode
        ){
            PlayModeType.reverse.name -> {
                val fileListBeforePlayModeReversed =
                    inFileListBeforePlayMode.reversed()
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
                ) return inFileListBeforePlayMode.shuffled()
                (1..repeatTimes).map {
                    inFileListBeforePlayMode.shuffled()
                }.flatten()
            }
            PlayModeType.number.name -> {
                try {
                    val numberModeNum = playNumber?.toInt()
                        ?: -1
                    val fileListBeforePlayModeNumber =
                        listOf(inFileListBeforePlayMode[numberModeNum-1])
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
                ) return inFileListBeforePlayMode
                (0..repeatTimes).map {
                    inFileListBeforePlayMode
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