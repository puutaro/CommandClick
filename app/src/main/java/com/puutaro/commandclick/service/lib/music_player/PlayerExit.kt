package com.puutaro.commandclick.service.lib.music_player

import com.puutaro.commandclick.service.MusicPlayerService

object PlayerExit {
    fun exit(
        musicPlayerService: MusicPlayerService,
    ){
        releaseMediaPlayer(musicPlayerService,)
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.execPlayJob?.cancel()

    }

    private fun releaseMediaPlayer(
        musicPlayerService: MusicPlayerService,
    ){
        if(
            musicPlayerService.mediaPlayer == null
        ) return
        MusicPlayerMaker.stop(musicPlayerService)
        musicPlayerService.mediaPlayer?.release()
        musicPlayerService.mediaPlayer = null
    }
}