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

    private var isComp = false

    fun setIsCompFalse(){
        isComp = false
    }

    private fun setIsCompTrue(){
        isComp = true
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
            if(!isComp){
                return@setOnCompletionListener
            }
            if(
                musicPlayerService.currentTrackIndex >= playList.lastIndex
            ) {
                musicPlayerService.notiSetter?.setOnStop(
                    musicPlayerService.mediaPlayer
                )
                return@setOnCompletionListener
            }
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
            )
            setIsCompFalse()
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
            musicPlayerService.notiSetter?.setOnStart(it)
            setIsCompTrue()
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

//        mediaPlayer.setOnPreparedListener(object : OnPreparedListener {
//            override fun onPrepared(mp: MediaPlayer?) {
//                mp?.start()
//            }
//
//            fun onCompletion(mp: MediaPlayer) {
//                mp.start()
//            }
//        };)
        return mediaPlayer
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