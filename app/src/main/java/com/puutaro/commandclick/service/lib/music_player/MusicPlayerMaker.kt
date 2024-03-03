package com.puutaro.commandclick.service.lib.music_player

import android.app.NotificationManager
import android.app.Service
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeMusicPlayer
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
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.action,
                listOf(
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
                listOf(
                    BroadCastIntentSchemeMusicPlayer.NOTI_UPDATE.scheme
                            to uriTitle
                )
            )
            posiUpdate(
                musicPlayerService,
                uriTitle,
            )
        }
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
    fun prepare(
        musicPlayerService: MusicPlayerService
    ){
        if(
            musicPlayerState != MusicPlayerState.SET_DATASET_DONE
        ) return
        setPrepareDoing()
        musicPlayerService.mediaPlayer?.prepareAsync()
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

    fun stop(
        musicPlayerService: MusicPlayerService
    ){
        setStateSetInitDoing()
        musicPlayerService.mediaPlayer?.stop()
        musicPlayerService.mediaPlayer?.reset()
        setStateSetInitDone()
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
//
//    private fun convertMilliToDisplayTime(
//        currentPosi: Int?
//    ): String {
//        if(
//            currentPosi == null
//        ) return String()
//        var millis = currentPosi.toLong()
//        val hours = TimeUnit.MILLISECONDS.toHours(millis)
//        millis -= TimeUnit.HOURS.toMillis(hours)
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
//        millis -= TimeUnit.MINUTES.toMillis(minutes)
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
//        return "${hours}:${minutes}:${seconds}"
////        val secs = (currentPosi % 60000) / 1000
//    }


}