package com.puutaro.commandclick.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.intent.TextToMp3IntentExtra
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.NotificationChanel
import com.puutaro.commandclick.service.variable.ServiceNotificationId
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.StringLength
import kotlinx.coroutines.*
import java.io.*
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path


class TextToMp3Service:
    Service(),
    TextToSpeech.OnInitListener {

    private val nortificationId = ServiceNotificationId.textToMp3
    private val convertingStr = "text to mp3..."
    var textToSpeech: TextToSpeech? = null
    private var textToMp3Job: Job? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var notificationManageFilter: IntentFilter? = null
    private var broadcastReceiverForTextToMp3Stop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            textToMp3Job?.cancel()
            notificationManager?.cancel(nortificationId)
            textToSpeech?.shutdown()
            stopSelf()
        }
    }


    override fun onCreate() {
        notificationManageFilter = IntentFilter()
        notificationManageFilter?.addAction(BroadCastIntentScheme.STOP_TEXT_TO_MP3.action)
        try {
            registerReceiver(broadcastReceiverForTextToMp3Stop, notificationManageFilter)
        } catch(e: Exception){
            println("pass")
        }
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiverForTextToMp3Stop)
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancel(nortificationId)
        textToSpeech?.stop()
        textToMp3Job?.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            unregisterReceiver(broadcastReceiverForTextToMp3Stop)
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancel(nortificationId)
        textToSpeech?.stop()
        textToMp3Job?.cancel()
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager?.cancel(nortificationId)
        textToSpeech?.stop()
        textToMp3Job?.cancel()

        val textToMp3StopIntent = Intent()
        textToMp3StopIntent.action = BroadCastIntentScheme.STOP_TEXT_TO_MP3.action

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, textToMp3StopIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            NotificationChanel.TEXT_TO_MP3_NOTIFICATION.id,
            NotificationChanel.TEXT_TO_MP3_NOTIFICATION.name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(null, null);
        val context = applicationContext

        notificationManager = NotificationManagerCompat.from(context)

        notificationManager?.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationChanel.TEXT_TO_MP3_NOTIFICATION.id
        )
        notificationBuilder.setSmallIcon(R.drawable.progress_horizontal)
        notificationBuilder.setContentTitle(convertingStr)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentText("")
        notificationBuilder.setProgress(0, 0, true)
        notificationBuilder.addAction(
            R.drawable.ic_menu_close_clear_cancel,
            "cancel",
            pendingIntent
        )
        val text = intent?.getStringExtra(
            TextToMp3IntentExtra.text.scheme
        ) ?: return START_NOT_STICKY
        val outDir = intent.getStringExtra(
            TextToMp3IntentExtra.outDir.scheme
        ) ?: return START_NOT_STICKY
        val atomicName = intent.getStringExtra(
            TextToMp3IntentExtra.atomicName.scheme)
            ?: return START_NOT_STICKY
//        textToSpeechJob = CoroutineScope(Dispatchers.IO).launch{
//            withContext(Dispatchers.IO){
                save(
                    text,
                    outDir,
                    atomicName,
                    notificationBuilder,
                    notificationManager,
                )
//            }
//        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val locale = Locale.getDefault()
                if (tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = Locale.getDefault()
                } else {
                    // 言語の設定に失敗
                }
            }


        } else {
            // Tts init 失敗
        }
    }

    fun save(
        text: String,
        outDir: String,
        atomicName: String,
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManagerCompat?,
    ){
        val stringLength = text.length
        val lengthLimit = 3000
        val mp3Extend = ".mp3"
        val tmpDirName = "temp"
        val tempDirPath = "${outDir}/${tmpDirName}"
        FileSystems.removeDir(tempDirPath)
        FileSystems.createDirs(tempDirPath)
        var done = true
        val totalTimes = stringLength / lengthLimit + 1
        textToMp3Job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 0 until stringLength step lengthLimit) {
                if(i >= stringLength) break
                val endPosiEntry = i + lengthLimit
                val endLength = if(
                    endPosiEntry <= stringLength
                ) endPosiEntry
                else stringLength - 1
                val splitTextContent = text.substring(i, endLength)
                val saveMp3TmpName = "${atomicName}-${i/lengthLimit}${mp3Extend}"
                val saveMp3TmpPath = "${outDir}/${saveMp3TmpName}"
                var code: Int? = 0
                val utterId = i.toString()
//                CoroutineScope(Dispatchers.IO).launch {
                delay(4000)

                code = withContext(Dispatchers.IO) {
                    textToSpeech?.synthesizeToFile(
                        splitTextContent,
                        null,
                        File(saveMp3TmpPath),
                        utterId
                    )
                }
//                withContext(Dispatchers.IO) {
//                    FileSystems.writeFile(
//                        File(saveMp3TmpPath).parent ?: String(),
//                        "code${i}",
//                        code.toString()
//                    )
//                }
                done = false
                withContext(Dispatchers.IO) {
                    for (j in 1..200) {
                        textToSpeech?.setOnUtteranceProgressListener(
                            object : UtteranceProgressListener() {
                                override fun onDone(utteranceId: String) {
                                    done = true
//                                    FileSystems.writeFile(
//                                        File(saveMp3TmpPath).parent ?: String(),
//                                        "done${i}",
//                                        "done${i}"
//                                    )
                                    Log.d("debug", "progress on Done $utteranceId")
                                }

                                override fun onError(utteranceId: String) {
                                    Log.d("debug", "progress on Error $utteranceId")
                                }

                                override fun onStart(utteranceId: String) {
                                    Log.d("debug", "progress on Start $utteranceId")
//                                    FileSystems.writeFile(
//                                        File(saveMp3TmpPath).parent ?: String(),
//                                        "start${i}",
//                                        "start${i}"
//                                    )
                                }
                            })
                        if (done) break
                        notificationBuilder.setContentTitle(convertingStr)
                        notificationBuilder.setAutoCancel(true)

                        val currentProgress = 70
                        val firstSize = withContext(Dispatchers.IO) {
                            try {
                                Files.size(Path(saveMp3TmpPath)) / 1000
                            } catch (e: Exception) {
                                0L
                            }
                        }
                        val digitNum = firstSize / 10
                        val displayByte = if(digitNum <= 4){
                            "${firstSize.toString()}B"
                        } else "${firstSize / 1000}KB"
                        val displayNameSource = File(saveMp3TmpPath).name
                        val displayNameSourceLength = StringLength.maxCountFromList(
                            listOf(displayNameSource)
                        )
                        val displayName = if(
                            displayNameSourceLength <= 10
                        ){
                            displayNameSource
                        } else displayNameSource.substring(
                            0, 5
                        ) + "..."
                        val displayTimes = "${(i / lengthLimit) + 1}/${totalTimes}"
                        notificationBuilder.setContentText(
                            "${displayName} / ${displayByte}(${displayTimes})"
                        )
                        notificationBuilder.setProgress(100, currentProgress, true)
                        notificationManager?.notify(nortificationId, notificationBuilder.build())
//                        FileSystems.writeFile(
//                            File(saveMp3TmpPath).parent ?: String(),
//                            "${i}_${j}",
//                            "$i ${j}".toString()
//                        )
                        delay(500)

                    }
                }
//                }
                withContext(Dispatchers.IO) {
                    for (j in 0..200) {
                        if (done) break
                        delay(500)
                    }
                }
//                withContext(Dispatchers.IO) {
//                    FileSystems.writeFile(
//                        File(saveMp3TmpPath).parent ?: String(),
//                        "comp${i}",
//                        "comp${i}".toString()
//                    )
//                }
//                val files = FileSystems.sortedFiles(tempDirPath)
//                val fileList = files.sorted().map{
//                    "${tempDirPath}/${it}"
//                }
//                mergeMp3(fileList, "$outDir/${atomicName}${mp3Extend}")
            }
            withContext(Dispatchers.IO) {
                notificationBuilder.setContentTitle("text to mp3 done")
                notificationBuilder.setContentText("text to mp3 done")
                notificationBuilder.setSmallIcon(R.drawable.progress_indeterminate_horizontal)
                notificationBuilder.setContentText(convertingStr)
                notificationBuilder.setProgress(100, 100, false)
                notificationBuilder.setAutoCancel(true)
                notificationBuilder.clearActions()
                notificationManager?.notify(nortificationId, notificationBuilder.build())
//                FileSystems.writeFile(
//                    tempDirPath,
//                    "super_comp",
//                    "super_comp".toString()
//                )
            }
        }
    }
}

private fun mergeMp3(inputPathList: List<String>, outputPath: String) {
    val iterator = inputPathList.iterator()
//        val iterator = inputs.iterator()
    SequenceInputStream(object : Enumeration<InputStream> {
        override fun hasMoreElements(): Boolean = iterator.hasNext()
        override fun nextElement(): InputStream = FileInputStream(iterator.next())
    }).use { sequenceStream ->
        FileOutputStream(outputPath).use { outputStream ->
            generateSequence { sequenceStream.read() }
                .takeWhile { it != -1 }
                .forEach { outputStream.write(it) }
        }
    }
}
