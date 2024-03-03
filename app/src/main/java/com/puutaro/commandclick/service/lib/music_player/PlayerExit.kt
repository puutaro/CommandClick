package com.puutaro.commandclick.service.lib.music_player

import com.puutaro.commandclick.service.MusicPlayerService

object PlayerExit {
    fun exit(
        musicPlayerService: MusicPlayerService,
    ){
        val mediaPlayer = musicPlayerService.mediaPlayer
            ?: return
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        musicPlayerService.mediaPlayer = null
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.execPlayJob?.cancel()

    }
}