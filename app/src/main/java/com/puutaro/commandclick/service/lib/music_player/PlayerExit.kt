package com.puutaro.commandclick.service.lib.music_player

import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.libs.ExecMusicPlay

object PlayerExit {
    fun exit(
        musicPlayerService: MusicPlayerService,
    ){
        ExecMusicPlay.exit(musicPlayerService.applicationContext)
        MusicPlayerMaker.releaseMediaPlayer(musicPlayerService)
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.execPlayJob?.cancel()
        musicPlayerService.streamingPreloadFileMakeJob?.cancel()
    }

}