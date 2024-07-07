package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.broadcast.extra.MusicPlayerIntentExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.music_player.MusicPlayerMaker
import com.puutaro.commandclick.service.lib.music_player.MusicPlayerBroadcastHandler
import com.puutaro.commandclick.service.lib.music_player.NotiSetter
import com.puutaro.commandclick.service.lib.music_player.PlayerExit
import com.puutaro.commandclick.service.lib.music_player.libs.ExecMusicPlay
import com.puutaro.commandclick.service.lib.music_player.libs.InfoFileForMediaPlayer
import com.puutaro.commandclick.service.lib.music_player.libs.PlayListMaker
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.shell.LinuxCmd
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
//import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MusicPlayerService: Service() {

    var notificationIdToImportance =
        NotificationIdToImportance.HIGH
    var notificationBuilder:  NotificationCompat.Builder? = null
    val channelNum = ServiceChannelNum.musicPlayer
    var mediaPlayer: MediaPlayer? = null
    var madiaPlayerPosiUpdateJob: Job? = null
    var streamingPreloadFileMakeJob: Job? = null
    var execPlayJob: Job? = null
    var currentTrackLength: Int = 0
    var currentTrackIndex = 0
    var notiSetter: NotiSetter? = null
    private var shellPath = String()
    private var shellArgs = String()
    private var fileListConBeforePlayMode: String = String()
    private var playList: List<String> = emptyList()
    var infoFileForMediaPlayer: InfoFileForMediaPlayer? = null
    private var broadcastReceiverForMusicPlayerService: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            MusicPlayerBroadcastHandler.handle(
                this@MusicPlayerService,
                intent,
                playList,
            )
        }
    }
    var notificationManager: NotificationManagerCompat? = null

    override fun onCreate(){
//        YoutubeDL.getInstance().init(this)
        BroadcastManagerForService.registerActionListBroadcastReceiver(
            this,
            broadcastReceiverForMusicPlayerService,
            BroadCastIntentSchemeMusicPlayer.values().map {
                it.action
            }
        )
        notificationManager = NotificationManagerCompat.from(applicationContext)
        val importance = TsvTool.getKeyValue(
           UsePath.mediaPlayerServiceConfigPath,
           MusicPlayerIntentExtra.IMPORTANCE.scheme
        )
        notificationIdToImportance = NotificationIdToImportance.values().filter {
            it.name.lowercase() == importance
        }.firstOrNull() ?: NotificationIdToImportance.HIGH

        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )
        notificationManager?.createNotificationChannel(channel)
        notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            notificationIdToImportance.id
        )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_media_play)
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
            )
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentTitle("MediaPlayer")
            .setContentText("")
        notiSetter = NotiSetter(this)
        notificationBuilder?.build()?.let {
            notificationManager?.notify(
                channelNum,
                it
            )
            startForeground(
                channelNum,
                it
            )
        }
        ExecMusicPlay.init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        PlayerExit.exit(this)
        currentTrackIndex = 0
        val onLoop = intent?.getStringExtra(
            MusicPlayerIntentExtra.ON_LOOP.scheme
        )
        val playNumber = intent?.getStringExtra(
            MusicPlayerIntentExtra.PLAY_NUMBER.scheme
        )
        val playMode = intent?.getStringExtra(
            MusicPlayerIntentExtra.PLAY_MODE.scheme
        )
        val onTrack = intent?.getStringExtra(
            MusicPlayerIntentExtra.ON_TRACK.scheme
        )
        val currentAppDirName = intent?.getStringExtra(
            MusicPlayerIntentExtra.CURRENT_APP_DIR_NAME.scheme
        )
        val fannelRawName = intent?.getStringExtra(
            MusicPlayerIntentExtra.SCRIPT_RAW_NAME.scheme
        )
        shellPath = intent?.getStringExtra(
            MusicPlayerIntentExtra.SHELL_PATH.scheme
        )?: String()
        shellArgs = intent?.getStringExtra(
            MusicPlayerIntentExtra.SHELL_ARGS.scheme
        ) ?: String()
        val infoFileName = "${currentAppDirName}${fannelRawName}.txt"
        val playInfoFilePath = File(
            UsePath.cmdclickTempMediaPlayerDirPath,
            infoFileName
        ).absolutePath
        val listFilePath = intent?.getStringExtra(
            MusicPlayerIntentExtra.LIST_FILE_PATH.scheme
        ) ?: return Service.START_NOT_STICKY
        val context = applicationContext
        notiSetter?.firstSet()
        notificationBuilder?.build()?.let {
            notificationManager?.notify(
                channelNum,
                it
            )
        }
        fileListConBeforePlayMode = ReadText(
            listFilePath
        ).readText()
        playList = PlayListMaker.make(
            fileListConBeforePlayMode.split("\n"),
            playMode,
            onLoop,
            playNumber
        )
        infoFileForMediaPlayer = InfoFileForMediaPlayer(
            playMode,
            playInfoFilePath
        )
        currentTrackIndex = infoFileForMediaPlayer?.makeFirstPlayPosi(
            onTrack,
            infoFileName,
            playMode,
            fileListConBeforePlayMode,
        ) ?: 0
//        mediaPlayer = MusicPlayerMaker.make(
//            this,
//            playList,
//        )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                val procName =
                    CcPathTool.trimAllExtend(
                        UbuntuFiles.extractAudioStreamingMapShellName
                    )
                for (i in 1..30){
                    val isNotExist = !LinuxCmd.isProcessCheck(
                        applicationContext,
                        procName
                    )
                    if(isNotExist) break
//                    LinuxCmd.killCertainProcess(
//                        context,
//                        procName
//                    )
                    delay(300)
                }
            }
            withContext(Dispatchers.IO) {
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.action,
                    listOf(
                        BroadCastIntentSchemeMusicPlayer.PLAY_MUSIC_PLAYER.scheme
                                to currentTrackIndex.toString(),
                    )
                )
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerExit.exit(this,)

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        PlayerExit.exit(this)
        stopSelf()
    }

    fun getFileListBeforePlayMode(): String {
        return fileListConBeforePlayMode
    }


    fun getShellArgs(): String {
        return shellArgs
    }

    fun getShellPath(): String {
        return shellPath
    }
}