package com.puutaro.commandclick.service.lib.music_player

import android.app.NotificationManager
import android.app.Service
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.libs.PlayNotiLauncher
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

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
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
            )
        }
        mediaPlayer.setOnSeekCompleteListener {
            val uriTitle = getUriTitleFromNoti(
                musicPlayerService
            ) ?: return@setOnSeekCompleteListener
            val musicPrepareLog = "musicLog_setOnSeekCompleteListener_log.txt"
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
                "${LocalDateTime.now()} setOnSeekCompleteListener"
            )
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                listOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
                "${LocalDateTime.now()} setOnSeekCompleteListener finish"
            )
        }
        mediaPlayer.setOnPreparedListener {
            val musicPrepareLog = "musicLog_setOnPreparedListener_log.txt"
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
                "${LocalDateTime.now()} setOnPreparedListener"
            )
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
                listOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
            posiUpdate(
                musicPlayerService,
                uriTitle,
            )
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
                "${LocalDateTime.now()} setOnPreparedListener done"
            )
        }
        musicPlayerService.mediaPlayer = mediaPlayer
        return mediaPlayer
    }


    fun start(
        musicPlayerService: MusicPlayerService
    ) {
        val prepareState = howPrepareState()
        if(
            prepareState != MusicPlayerState.PREPARE_DONE
        ) return
        val musicPrepareLog = "musicLog_start.txt"
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} start"
        )
        musicPlayerService.mediaPlayer?.start()
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} started"
        )
    }
    fun prepare(
        musicPlayerService: MusicPlayerService
    ){
        if(
            musicPlayerState != MusicPlayerState.SET_DATASET_DONE
        ) return
        val musicPrepareLog = "musicLog_prepare.txt"
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} prepare"
        )
        setPrepareDoing()
        musicPlayerService.mediaPlayer?.prepareAsync()
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} prepared"
        )
    }

    fun setDatasource(
        musicPlayerService: MusicPlayerService,
        uri: String
    ){
        if(
            musicPlayerState != MusicPlayerState.INIT_DONE
        ) return
        val musicPrepareLog = "musicLog_setDataSource.txt"
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} setDatasource"
        )
        setStateSetDatasetDoing()
        musicPlayerService.mediaPlayer?.setDataSource(uri)
        setStateSetDatasetDone()
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} setDatasourced"
        )
    }

    fun stop(
        musicPlayerService: MusicPlayerService
    ){
        setStateSetInitDoing()
        musicPlayerService.mediaPlayer?.stop()
        musicPlayerService.mediaPlayer?.reset()
        setStateSetInitDone()
    }

    fun releaseMediaPlayer(
        musicPlayerService: MusicPlayerService,
    ){
        val musicPrepareLog = "musicLog_releaseMediaPlayer_log.txt"
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} releaseMediaPlayer"
        )
        if(
            musicPlayerService.mediaPlayer == null
        ) return
        stop(musicPlayerService)
        musicPlayerService.mediaPlayer?.release()
        musicPlayerService.mediaPlayer = null
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, musicPrepareLog).absolutePath,
            "${LocalDateTime.now()} releaseMediaPlayer"
        )
    }

    private fun posiUpdate(
        musicPlayerService: MusicPlayerService,
        uriTitle: String,
    ){
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        musicPlayerService.madiaPlayerPosiUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                try {
                    execPosiUpdate(
                        musicPlayerService,
                        uriTitle,
                    )
                } catch (e: Exception){
                    println("pass")
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
                listOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
        }
    }
}