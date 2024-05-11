package com.puutaro.commandclick.service.lib.music_player

import com.puutaro.commandclick.service.MusicPlayerService

object PlayerExit {
    fun exit(
        musicPlayerService: MusicPlayerService,
    ){
        MusicPlayerMaker.releaseMediaPlayer(musicPlayerService)
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.execPlayJob?.cancel()

    }

}