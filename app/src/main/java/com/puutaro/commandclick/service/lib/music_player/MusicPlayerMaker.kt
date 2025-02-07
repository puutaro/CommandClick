package com.puutaro.commandclick.service.lib.music_player

import android.app.NotificationManager
import android.app.Service
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.libs.PlayNotiLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MusicPlayerMaker {

    private enum class MusicPlayerState{
        INIT_DOING,
        INIT_DONE,
        SET_DATASET_DOING,
        SET_DATASET_DONE,
        PREPARE_DOING,
        PREPARE_DONE,
    }
    private var musicPlayerState = MusicPlayerState.INIT_DOING

    private fun howPrepareState(): MusicPlayerState {
        return musicPlayerState
    }

    private fun setStateSetInitDoing(){
        musicPlayerState = MusicPlayerState.INIT_DOING
    }
    private fun setStateSetInitDone(){
        musicPlayerState = MusicPlayerState.INIT_DONE
    }

    private fun setStateSetDatasetDoing(){
        musicPlayerState = MusicPlayerState.SET_DATASET_DOING
    }

    private fun setStateSetDatasetDone(){
        musicPlayerState = MusicPlayerState.SET_DATASET_DONE
    }

    private fun setPrepareDoing(){
        musicPlayerState = MusicPlayerState.PREPARE_DOING
    }

    private fun setPrepareDone(){
        musicPlayerState = MusicPlayerState.PREPARE_DONE
    }

    fun make(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
    ): MediaPlayer {
        setStateSetInitDoing()
        val context =
            musicPlayerService.applicationContext
        val mediaPlayer = MediaPlayer()
        mediaPlayer
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        mediaPlayer.setOnErrorListener {
                mp, what, extra ->
            if(
                musicPlayerService.currentTrackIndex >= playList.lastIndex
            ) {
                musicPlayerService.notiSetter?.setOnStop()
                return@setOnErrorListener true
            }
            when(what){
                MediaPlayer.MEDIA_ERROR_UNKNOWN,
                MediaPlayer.MEDIA_ERROR_SERVER_DIED,
                MediaPlayer.MEDIA_ERROR_TIMED_OUT,
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        ToastUtils.showShort("Next, because cannot read: ${what}")
                    }
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
                    )
                }
            }
            true
        }
        mediaPlayer.setOnCompletionListener {
            if(
                howPrepareState() != MusicPlayerState.PREPARE_DONE
            ) return@setOnCompletionListener
            if(
                musicPlayerService.currentTrackIndex >= playList.lastIndex
            ) {
                musicPlayerService.notiSetter?.setOnStop()
                return@setOnCompletionListener
            }
            releaseMediaPlayer(
                musicPlayerService
            )
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
            )
        }
        mediaPlayer.setOnSeekCompleteListener {
            val uriTitle = getUriTitleFromNoti(
                musicPlayerService
            ) ?: return@setOnSeekCompleteListener
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                sequenceOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
        }
        mediaPlayer.setOnPreparedListener {
            setPrepareDone()
            musicPlayerService.notiSetter?.setOnStart()
            musicPlayerService.currentTrackLength =
                musicPlayerService.mediaPlayer?.duration ?: 0
            val uriTitle = getUriTitleFromNoti(
                musicPlayerService
            ) ?: return@setOnPreparedListener
            BroadcastSender.normalSend(
                musicPlayerService,
                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                sequenceOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
            posiUpdate(
                musicPlayerService,
                uriTitle,
            )
        }
        musicPlayerService.mediaPlayer = mediaPlayer
        setStateSetInitDone()
        return mediaPlayer
    }


    fun start(
        musicPlayerService: MusicPlayerService
    ) {
        val prepareState = howPrepareState()
        if(
            prepareState != MusicPlayerState.PREPARE_DONE
        ) return
        musicPlayerService.mediaPlayer?.start()
    }

    fun setDatasource(
        musicPlayerService: MusicPlayerService,
        uri: String
    ){
        if(
            musicPlayerState != MusicPlayerState.INIT_DONE
        ) return
        setStateSetDatasetDoing()
        musicPlayerService.mediaPlayer?.setDataSource(uri)
        setStateSetDatasetDone()
    }

    fun prepare(
        musicPlayerService: MusicPlayerService
    ){
        if(
            musicPlayerState != MusicPlayerState.SET_DATASET_DONE
        ) return
        setPrepareDoing()
        musicPlayerService.mediaPlayer?.prepareAsync()
    }

    fun releaseMediaPlayer(
        musicPlayerService: MusicPlayerService,
    ){
        if(
            musicPlayerService.mediaPlayer == null
        ) return
        stop(musicPlayerService)
        musicPlayerService.mediaPlayer?.release()
        musicPlayerService.mediaPlayer = null
    }

    private fun stop(
        musicPlayerService: MusicPlayerService
    ){
        musicPlayerService.mediaPlayer?.stop()
        musicPlayerService.mediaPlayer?.reset()
//        setStateSetInitDone()
    }

    private fun posiUpdate(
        musicPlayerService: MusicPlayerService,
        uriTitle: String,
    ){
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.madiaPlayerPosiUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                withContext(Dispatchers.IO) {
                    try {
                        val waitMiliSecond = 1000L
                        val previousPosi =
                            musicPlayerService.mediaPlayer?.currentPosition ?: 0
                        delay(waitMiliSecond)
                        val posi =
                            musicPlayerService.mediaPlayer?.currentPosition ?: 0
                        if (
                            posi == previousPosi
                        ) return@withContext
                        BroadcastSender.normalSend(
                            musicPlayerService,
                            BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                            sequenceOf(
                                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                                        to uriTitle
                            )
                        )
                    } catch (e: Exception){
                        println("pass")
                    }
                }
            }
        }
    }

    private fun getUriTitleFromNoti(
        musicPlayerService: MusicPlayerService
    ): String? {
        val notificationManager = musicPlayerService.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val currentDisplayMessage = notificationManager.activeNotifications.filter {
            it.id == musicPlayerService.channelNum
        }.firstOrNull()?.notification?.extras?.getString("android.title")
            ?: return null
        return PlayNotiLauncher.getUriTitle(
            currentDisplayMessage
        )
    }

    private suspend fun execPosiUpdate(
        musicPlayerService: MusicPlayerService,
        uriTitle: String,
    ){
        val waitMiliSecond = 1000L
        val previousPosi = withContext(Dispatchers.IO) {
            musicPlayerService.mediaPlayer?.currentPosition ?: 0
        }
        val posi = withContext(Dispatchers.IO) {
            delay(waitMiliSecond)
            musicPlayerService.mediaPlayer?.currentPosition ?: 0
        }
        if (posi == previousPosi) return
        withContext(Dispatchers.IO){
            BroadcastSender.normalSend(
                musicPlayerService,
                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                sequenceOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
        }
    }
}