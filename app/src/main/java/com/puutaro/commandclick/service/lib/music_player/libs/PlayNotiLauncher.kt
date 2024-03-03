package com.puutaro.commandclick.service.lib.music_player.libs

import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.PlayerExit
import java.util.concurrent.TimeUnit

object PlayNotiLauncher {

    fun launch(
        musicPlayerService: MusicPlayerService,
        header: String,
        uriTitle: String,
    ){
        val currentPosition = musicPlayerService.mediaPlayer?.currentPosition
        if(currentPosition == null){
            PlayerExit.exit(musicPlayerService)
            musicPlayerService.stopSelf()
            return
        }
        musicPlayerService.notificationBuilder?.setContentTitle(header)
        musicPlayerService.notificationBuilder?.setContentText(uriTitle)
        musicPlayerService.notificationBuilder?.build()?.let {
            musicPlayerService.notificationManager?.notify(
                musicPlayerService.channelNum,
                it
            )
        }
    }

    private fun indexHolderCreator(
        playIndex: Int,
        playList: List<String>,
    ): String {
        return "${playIndex + 1}/${playList.size}"
    }

    fun timeHolderCreator(
        currentPosi: Int?,
        currentTrackLength: Int?,
    ): String {

        val displayCurrentPosi = convertMilliToDisplayTime(
            currentPosi,
        )
        val displayCurrentTrackLength = convertMilliToDisplayTime(
            currentTrackLength,
        )
         return "[${displayCurrentPosi}/${displayCurrentTrackLength}]"
    }

    private fun convertMilliToDisplayTime(
        currentPosi: Int?
    ): String {
        if(
            currentPosi == null
        ) return String()
        var millis = currentPosi.toLong()
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        return "${hours}:${minutes}:${seconds}"

    }
    fun notiTitleCreator(
        timeHolder: String,
        uriTitle: String,
        playIndex: Int,
        playList: List<String>,
    ): String {
        val indexHolder = indexHolderCreator(
            playIndex,
            playList
        )
        return listOf(
            indexHolder,
            timeHolder,
            uriTitle
        ).joinToString("\t")
    }

    fun getUriTitle(
        title: String,
    ): String {
        val uriTitleIndex = 2
        return title
            .split("\t")
            .getOrNull(uriTitleIndex)
            ?: String()
    }

}