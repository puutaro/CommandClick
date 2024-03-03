package com.puutaro.commandclick.service.lib.music_player

import android.R
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.PendingIntentCreator

class NotiSetter(
    private val musicPlayerService: MusicPlayerService
) {
    private val context = musicPlayerService.applicationContext
    private val cancelPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.DESTROY_MUSIC_PLAYER.action,
    )
    private val pauseOrReplayPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.PUASE_OR_REPLAY_MUSIC_PLAYER.action,
    )
    private val previousPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.PREVIOUS_MUSIC_PLAYER.action,
    )
    private val fromPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.FROM_MUSIC_PLAYER.action,
    )
    private val toPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.TO_MUSIC_PLAYER.action,
    )
    private val nextPendingIntent = PendingIntentCreator.create(
        context,
        BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
    )
    fun firstSet(){
        musicPlayerService.notificationBuilder
            ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            ?.setSmallIcon(R.drawable.ic_media_play)
            ?.setOnlyAlertOnce(true)
            ?.setDeleteIntent(cancelPendingIntent)
            ?.addAction(
                com.puutaro.commandclick.R.drawable.icons8_cancel,
                "cancel",
                pauseOrReplayPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_previous,
                "Previous",
                previousPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_rew,
                "From",
                fromPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_ff,
                "to",
                toPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
            ?.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 4)
            )
            ?.setOngoing(false)
            ?.setAutoCancel(true)
            ?.setContentTitle("MediaPlayer")
            ?.setContentText("")
            ?.setDeleteIntent(
                cancelPendingIntent
            )
    }

    fun setOnStop(
        mediaPlayer: MediaPlayer?
    ){
        mediaPlayer?.stop()
        readySet()
    }

    fun setOnStart(
        mediaPlayer: MediaPlayer?
    ){
        mediaPlayer?.start()
        readySet()
    }

    fun setOnPause(
        mediaPlayer: MediaPlayer?
    ){
        mediaPlayer?.pause()
        readySet()
    }

    private fun readySet(){
        val pauseOrCannelIcon = when(musicPlayerService.mediaPlayer?.isPlaying == true){
            true -> R.drawable.ic_media_pause
            else -> com.puutaro.commandclick.R.drawable.icons8_cancel
        }

        musicPlayerService.notificationBuilder
            ?.clearActions()
            ?.addAction(
                pauseOrCannelIcon,
                "cancel",
                pauseOrReplayPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_previous,
                "Previous",
                previousPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_rew,
                "From",
                fromPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_ff,
                "to",
                toPendingIntent
            )
            ?.addAction(
                R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
            ?.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 4)
            )
            ?.setDeleteIntent(
                cancelPendingIntent
            )
    }
}