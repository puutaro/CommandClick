package com.puutaro.commandclick.service.lib.music_player

import android.content.Intent
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.libs.ExecMusicPlay
import com.puutaro.commandclick.service.lib.music_player.libs.PlayNotiLauncher
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object MusicPlayerBroadcastHandler {

    private const val seekUnit = 1000 * 10


    fun handle(
        musicPlayerService: MusicPlayerService,
        intent: Intent,
        playList: List<String>
    ){
        val action = intent.action
        val broadcastIntentAction = BroadCastIntentSchemeMusicPlayer.values().firstOrNull {
            it.action == action
        } ?: return
        when(broadcastIntentAction){
            BroadCastIntentSchemeMusicPlayer.FROM_MUSIC_PLAYER
            -> {
                if(
                    restartJudge(musicPlayerService)
                ) {
                    musicPlayerService.notiSetter?.setOnStart(
                        musicPlayerService.mediaPlayer
                    )
                    return
                }
                val position = musicPlayerService.mediaPlayer?.currentPosition
                    ?: return
                val fromPositionSrc = position - seekUnit
                val fromPosition =
                    if(fromPositionSrc > 0) fromPositionSrc
                    else 0
                val context = musicPlayerService.applicationContext
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.action,
                    listOf(
                        BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.scheme
                                to fromPosition.toString()
                    )
                )
            }

            BroadCastIntentSchemeMusicPlayer.TO_MUSIC_PLAYER
            -> {
                if(
                    restartJudge(musicPlayerService)
                ) {
                    musicPlayerService.notiSetter?.setOnStart(
                        musicPlayerService.mediaPlayer
                    )
                    return
                }
                val position = musicPlayerService.mediaPlayer?.currentPosition
                    ?: return
                val currentTrackLength = musicPlayerService.currentTrackLength
                val toPositionSrc = position + seekUnit
                val toPosition =
                    if(
                        toPositionSrc < currentTrackLength
                    ) toPositionSrc
                    else return
                val context = musicPlayerService.applicationContext
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.action,
                    listOf(
                        BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.scheme
                                to toPosition.toString()
                    )
                )
            }
            BroadCastIntentSchemeMusicPlayer.PREVIOUS_MUSIC_PLAYER
            -> {
                val currentTrackIndex = musicPlayerService.currentTrackIndex
                val maxTrackIndex = playList.lastIndex
                val previousIndexSrc = currentTrackIndex - 1
                val previousIndex =
                    if(previousIndexSrc >= 0) previousIndexSrc
                    else maxTrackIndex
                val context = musicPlayerService.applicationContext
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.action,
                    listOf(
                        BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.scheme
                                to previousIndex.toString()
                    )
                )
            }
            BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER
            -> {
                val currentTrackIndex = musicPlayerService.currentTrackIndex
                val maxTrackIndex = playList.lastIndex
                val nextIndexSrc = currentTrackIndex + 1
                val nextIndex =
                    if(nextIndexSrc <= maxTrackIndex) nextIndexSrc
                    else 0
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "music_next.txt").absolutePath,
//                    listOf(
//                        "currentTrackIndex: ${currentTrackIndex}",
//                        "maxTrackIndex: ${maxTrackIndex}",
//                        "toTrackIndexSrc: ${nextIndexSrc}",
//                        "nextIndex: ${nextIndex}",
//                        "\bplayList: ${playList}",
//                    ).joinToString("\n")
//                )
                val context = musicPlayerService.applicationContext
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.action,
                    listOf(
                        BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.scheme
                                to nextIndex.toString()
                    )
                )
            }
            BroadCastIntentSchemeMusicPlayer.DESTROY_MUSIC_PLAYER
            -> {
                PlayerExit.exit(musicPlayerService)
                musicPlayerService.stopSelf()
            }
            BroadCastIntentSchemeMusicPlayer.PUASE_OR_REPLAY_MUSIC_PLAYER
            -> {
                if(
                    musicPlayerService.mediaPlayer?.isPlaying == true
                ) {
                    musicPlayerService.notiSetter?.setOnPause(
                        musicPlayerService.mediaPlayer
                    )
                }
                else {
                    PlayerExit.exit(musicPlayerService)
                    musicPlayerService.stopSelf()
                }
            }
            BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER
            ->{
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        musicPlayerService.applicationContext,
                        "seek\n${intent.getStringExtra(
                            BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.scheme,
                        )}----\n---",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val seekPosiSrc = intent.getStringExtra(
                    BroadCastIntentSchemeMusicPlayer.SEEK_MUSIC_PLAYER.scheme,
                )?: return
                val seekPosi = toInt(seekPosiSrc)
                    ?: return
                musicPlayerService.mediaPlayer?.seekTo(seekPosi)
            }
            BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER
            -> {
                val index0 = musicPlayerService.currentTrackIndex
                musicPlayerService.currentTrackIndex = intent.getStringExtra(
                    BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.scheme,
                )?.let { toInt(it) }
                    ?: return
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "music_next_playuer.txt").absolutePath,
//                    listOf(
//                        "index0: ${index0}",
//                        "currentTrackIndex: ${musicPlayerService.currentTrackIndex}",
//                    ).joinToString("\n")
//                )
                ExecMusicPlay.playHandler(
                    musicPlayerService,
                    playList,
                    musicPlayerService.currentTrackIndex,
                )
            }
            BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE
            -> {
                val uriTitle = intent.getStringExtra(
                    broadcastIntentAction.scheme
                ) ?: return
                val timeHolder = PlayNotiLauncher.timeHolderCreator(
                    musicPlayerService.mediaPlayer?.currentPosition,
                    musicPlayerService.currentTrackLength
                )
                val header = PlayNotiLauncher.notiTitleCreator(
                    timeHolder,
                    uriTitle,
                    musicPlayerService.currentTrackIndex,
                    playList,
                )
                PlayNotiLauncher.launch(
                    musicPlayerService,
                    header,
                    uriTitle,
                )
                musicPlayerService.infoFileForMediaPlayer?.savePlayInfo(
                    musicPlayerService.currentTrackIndex,
                    playList,
                )
            }
        }
    }

    private fun restartJudge(
        musicPlayerService: MusicPlayerService
    ): Boolean {
        return musicPlayerService.mediaPlayer?.isPlaying != true
                && musicPlayerService.mediaPlayer != null
    }

    private fun toInt(
        intStr: String,
    ): Int? {
        return try{
            intStr.toInt()
        } catch(e: Exception){
            null
        }
    }
}