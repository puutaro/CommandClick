package com.puutaro.commandclick.service

import android.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anggrayudi.storage.file.isEmpty
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTextToSpeech
import com.puutaro.commandclick.common.variable.intent.extra.TextToSpeechIntentExtra
import com.puutaro.commandclick.common.variable.variant.Translate
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.service.lib.BroadcastManagerForService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.service.lib.textToSpeech.ExecShellForTts
import com.puutaro.commandclick.service.variable.ServiceChannelNum
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.StringLength
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.File
import java.util.*


class TextToSpeechService:
    Service() {
    private val debugTemp = "/storage/emulated/0/Music/test/temp"
    private val speechingStr = "text to speech..."
    private val channelNum = ServiceChannelNum.textToSpeech
    private var notificationIdToImportance =
        NotificationIdToImportance.HIGH
    private var textToSpeech: TextToSpeech? = null
    private var textToSpeechJob: Job? = null
    private var execTextToSpeechJob: Job? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var done = true
    private var nextRoop = false
    private var onPressToButton = false
    private var onCurrentRoopBreak = false
    private var currentOrder: Int = 0
    private var currentBlockNum: Int = 0
    private var transMode: String = String()
    private val languageLocaleMap = Translate.languageLocaleMap
    private val noTransMark = "-"
    private var isInitComp = false
    private var busyBoxExecutor: BusyboxExecutor? = null

    private var broadcastReceiverForTextToSpeechPrevious: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentSchemeTextToSpeech.PREVIOUS_TEXT_TO_SPEECH.action
            ) return
            onCurrentRoopBreak = true
            if(
                currentBlockNum == 0
            ) currentOrder -= 2
            else currentOrder--
            currentBlockNum = -1
        }
    }
    private var broadcastReceiverForTextToSpeechFrom: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentSchemeTextToSpeech.FROM_TEXT_TO_SPEECH.action
            ) return
            done = true
            currentBlockNum -= 2
        }
    }
    private var broadcastReceiverForTextToSpeechTo: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentSchemeTextToSpeech.TO_TEXT_TO_SPEECH.action
            ) return
            done = true
            onPressToButton = true
        }
    }
    private var broadcastReceiverForTextToSpeechNext: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action
                != BroadCastIntentSchemeTextToSpeech.NEXT_TEXT_TO_SPEECH.action
            ) return
            onCurrentRoopBreak = true
            currentBlockNum = -1
        }
    }
    private var broadcastReceiverForTextToSpeechStop: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if(
                intent.action
                != BroadCastIntentSchemeTextToSpeech.STOP_TEXT_TO_SPEECH.action
            ) return
            textToSpeechJob?.cancel()
            notificationManager?.cancel(channelNum)
            textToSpeech?.stop()
            execTextToSpeechJob?.cancel()
            done = true
            stopForeground(Service.STOP_FOREGROUND_DETACH)
            stopSelf()
        }
    }


    override fun onCreate() {
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechStop,
            BroadCastIntentSchemeTextToSpeech.STOP_TEXT_TO_SPEECH.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechPrevious,
            BroadCastIntentSchemeTextToSpeech.PREVIOUS_TEXT_TO_SPEECH.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechFrom,
            BroadCastIntentSchemeTextToSpeech.FROM_TEXT_TO_SPEECH.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechTo,
            BroadCastIntentSchemeTextToSpeech.TO_TEXT_TO_SPEECH.action
        )
        BroadcastManagerForService.registerBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechNext,
            BroadCastIntentSchemeTextToSpeech.NEXT_TEXT_TO_SPEECH.action
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechStop,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechPrevious,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechFrom,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechTo,
        )
        BroadcastManagerForService.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForTextToSpeechNext,
        )
        notificationManager?.cancel(channelNum)
        textToSpeech?.stop()
        textToSpeechJob?.cancel()
        execTextToSpeechJob?.cancel()
        done = true
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            unregisterReceiver(
                broadcastReceiverForTextToSpeechStop
            )
            unregisterReceiver(
                broadcastReceiverForTextToSpeechPrevious
            )
            unregisterReceiver(
                broadcastReceiverForTextToSpeechNext
            )
        } catch(e: Exception){
            println("pass")
        }
        notificationManager?.cancel(channelNum)
        textToSpeech?.stop()
        textToSpeechJob?.cancel()
        done = true
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager?.cancel(channelNum)
        textToSpeech?.stop()
        textToSpeechJob?.cancel()
        execTextToSpeechJob?.cancel()
        done = true
        busyBoxExecutor = BusyboxExecutor(
            applicationContext,
            UbuntuFiles(applicationContext)
        )
        val cancelPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeTextToSpeech.STOP_TEXT_TO_SPEECH.action,
        )
        val previousPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeTextToSpeech.PREVIOUS_TEXT_TO_SPEECH.action,
        )
        val fromPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeTextToSpeech.FROM_TEXT_TO_SPEECH.action,
        )
        val toPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeTextToSpeech.TO_TEXT_TO_SPEECH.action,
        )
        val nextPendingIntent = PendingIntentCreator.create(
            applicationContext,
            BroadCastIntentSchemeTextToSpeech.NEXT_TEXT_TO_SPEECH.action,
        )

        val importance = intent?.getStringExtra(
            TextToSpeechIntentExtra.importance.scheme
        )
        notificationIdToImportance = NotificationIdToImportance.values().filter {
            it.name.lowercase() == importance
        }.firstOrNull() ?: NotificationIdToImportance.HIGH

        val channel = NotificationChannel(
            notificationIdToImportance.id,
            notificationIdToImportance.id,
            notificationIdToImportance.importance
        )
        val context = applicationContext

        notificationManager = NotificationManagerCompat.from(context)
        notificationManager?.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(
            context,
            notificationIdToImportance.id
        )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_media_play)
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.ic_menu_close_clear_cancel,
                "cancel",
                cancelPendingIntent
            )
            .addAction(
                R.drawable.ic_media_previous,
                "Previous",
                previousPendingIntent
            )
            .addAction(
                R.drawable.ic_media_rew,
                "From",
                fromPendingIntent
            )
            .addAction(
                R.drawable.ic_media_ff,
                "to",
                toPendingIntent
            )
            .addAction(
                R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
            .setStyle(MediaNotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 4)
            )
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentTitle(speechingStr)
            .setContentText("")
            .setDeleteIntent(
                cancelPendingIntent
            )
        val notificationInstance = notificationBuilder.build()
        notificationManager?.notify(
            channelNum,
            notificationInstance
        )
        startForeground(
            channelNum,
            notificationInstance
        )
        val listFilePath = intent?.getStringExtra(
            TextToSpeechIntentExtra.listFilePath.scheme
        ) ?: return Service.START_NOT_STICKY
        val playMode = intent.getStringExtra(
            TextToSpeechIntentExtra.playMode.scheme
        )
        val onRoop = intent.getStringExtra(
            TextToSpeechIntentExtra.onRoop.scheme
        )
        val playNumber = intent.getStringExtra(
            TextToSpeechIntentExtra.playNumber.scheme
        )
        val onTrack = intent.getStringExtra(
            TextToSpeechIntentExtra.onTrack.scheme
        )
        val pitch = intent.getStringExtra(
            TextToSpeechIntentExtra.pitch.scheme
        )
        val speed = intent.getStringExtra(
            TextToSpeechIntentExtra.speed.scheme
        )
        val currentAppDirName = intent.getStringExtra(
            TextToSpeechIntentExtra.currentAppDirName.scheme
        )
        val fannelRawName = intent.getStringExtra(
            TextToSpeechIntentExtra.scriptRawName.scheme
        )
        val currentTrackFileName = "${currentAppDirName}${fannelRawName}.txt"
        val shellPath = intent.getStringExtra(
            TextToSpeechIntentExtra.shellPath.scheme
        )?: String()
        instantiateTextToSpeech(
            intent
        )
        val factListSource = ReadText(
            listFilePath
        ).textToList()
        val factListSize = factListSource.size

        val noExistFile = factListSource.filter {
            val onFile = it.startsWith(WebUrlVariables.filePrefix)
                    || it.startsWith("/")
            val fileObj = File(it)
            val isEmptyOrNoExist = !fileObj.isFile
                || fileObj.isEmpty
            onFile && isEmptyOrNoExist
        }.firstOrNull()
            ?.split("/")
            ?.lastOrNull()
            ?: String()
        if(noExistFile.isNotEmpty()) {
            notificationBuilder.setContentTitle(
                "remove bellow from list (no exist or blank"
            )
            notificationBuilder.setContentText(
                noExistFile
            )
            notificationManager?.notify(channelNum, notificationBuilder.build())
            return Service.START_NOT_STICKY
        }
        val fileList = makePlayList(
            listFilePath,
            playMode,
            onRoop,
            playNumber,
        ) ?: return START_NOT_STICKY

//        FileSystems.removeDir(debugTemp)
//        FileSystems.createDirs(debugTemp)
//        FileSystems.writeFile(
//            debugTemp,
//            "roopList",
//            fileList.joinToString("\n")
//        )
        FileSystems.createDirs(
            UsePath.cmdclickTempTextToSpeechDirPath
        )
        val fileListSize = fileList.size - 1
        val pastTrackKeyValueList = ReadText(
            File(
                UsePath.cmdclickTempTextToSpeechDirPath,
                currentTrackFileName
            ).absolutePath,
        ).textToList()
        val readLength = getIntValue(
            pastTrackKeyValueList,
            PlayTrackFileKey.length.name
        )
        val readPlayMode = getStrValue(
            pastTrackKeyValueList,
            PlayTrackFileKey.playMode.name,
            "ordinaly"
        )
        currentOrder = 0
        currentBlockNum = 0
        if(
            fileList.joinToString("").length == readLength
            && readPlayMode == playMode
            && !onTrack.isNullOrEmpty()
        ){
            currentOrder =  getIntValue(
                pastTrackKeyValueList,
                PlayTrackFileKey.order.name
            )
            currentBlockNum =  getIntValue(
                pastTrackKeyValueList,
                PlayTrackFileKey.blockNum.name
            )

        }
        textToSpeech?.setSpeechRate(
            convertFloat(speed)
        )
        textToSpeech?.setSpeechRate(
            convertFloat(pitch)
        )
        val roopNumExtend = 1000
        textToSpeechJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                for(i in 1..100){
                    if(isInitComp) break
                    delay(100)
                }
            }
            for (roopNum in 0 .. fileListSize + roopNumExtend) {
                nextRoop = false
                if(
                    currentOrder < 0
                ) currentOrder = 0
                if(
                    currentOrder > fileListSize
                ) {
                    textToSpeech?.stop()
                    notificationManager?.notify(
                        channelNum, notificationBuilder.build()
                    )
                    stopForeground(Service.STOP_FOREGROUND_DETACH)
                    notificationManager?.cancel(channelNum)
                    break
                }

                val playFile = fileList[currentOrder]
                if(factListSize == 0) {
                    notificationBuilder.setContentTitle(
                        "play list size must be more zero"
                    )
                    notificationManager?.notify(
                        channelNum, notificationBuilder.build()
                    )
                    return@launch
                }
                val displayRoopTimes = "${currentOrder % factListSize + 1}"
//                  (${fileListSize}
                withContext(Dispatchers.IO) {
                    try {
                        execPlay(
                            playFile,
                            playMode,
                            fileList,
                            notificationBuilder,
                            shellPath,
                            displayRoopTimes,
                            cancelPendingIntent,
                            currentTrackFileName
                        )
                    } catch(e: Exception){
                        Log.e("textToSpeech", e.toString())
                    }
                }
                withContext(Dispatchers.IO){
                    while(true) {
                        if(nextRoop) break
                        delay(300)
//                        FileSystems.writeFile(
//                            listFilePathParentDir,
//                            "mainRoop-${roopTimes}",
//                            ""
//                        )
                    }
                    currentOrder++
                }
            }
            withContext(Dispatchers.IO){
                textToSpeech?.stop()
                notificationManager?.notify(channelNum, notificationBuilder.build())
                stopForeground(Service.STOP_FOREGROUND_DETACH)
                notificationManager?.cancel(channelNum)

//                FileSystems.writeFile(
//                    listFilePathParentDir,
//                    "super_comp",
//                    "super_comp".toString()
//                )
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun instantiateTextToSpeech(
        intent: Intent
    ){
        if(textToSpeech != null) return
        transMode = intent.getStringExtra(
            TextToSpeechIntentExtra.transMode.scheme
        ) ?: String()
        val defaultLocale = Locale.getDefault()
        val lang = if(
            transMode.isEmpty()
            || transMode == noTransMark
        ) defaultLocale
        else languageLocaleMap.get(transMode)
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (
                status != TextToSpeech.SUCCESS
            ) return@OnInitListener
            textToSpeech?.let { tts ->
                val isEnableLanguage = try {
                    tts.isLanguageAvailable(lang)
                } catch (e: Exception){
                    tts.language = defaultLocale
                    isInitComp = true
                    return@let
                }
                if (isEnableLanguage > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = lang
                } else {
                    tts.language = defaultLocale
                }
                isInitComp = true
            }
        })
    }

    private fun makePlayList(
//        listFilePathParentDir: String,
//        listFileName: String,
        listFilePath: String,
        playMode: String?,
        onRoop: String?,
        playNumber: String?,
    ): List<String>? {
        val fileListBeforePlayMode = ReadText(
            listFilePath
        ).textToList()
        val repeatTimes = 100
        return when(
            playMode
        ){
            PlayModeType.reverse.name -> {
                val fileListBeforePlayModeReversed =
                    fileListBeforePlayMode.reversed()
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayModeReversed
                (1..repeatTimes).map {
                    fileListBeforePlayModeReversed
                }.flatten()
            }
            PlayModeType.shuffle.name -> {
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayMode.shuffled()
                (1..repeatTimes).map {
                    fileListBeforePlayMode.shuffled()
                }.flatten()
            }
            PlayModeType.number.name -> {
                try {
                    val numberModeNum = playNumber?.toInt()
                        ?: -1
                    val fileListBeforePlayModeNumber =
                        listOf(fileListBeforePlayMode[numberModeNum-1])
                    if(
                        onRoop.isNullOrEmpty()
                    ) return fileListBeforePlayModeNumber
                    (1..repeatTimes * 10).map {
                        fileListBeforePlayModeNumber
                    }.flatten()
                } catch(e: Exception){
                    return null
                }
            }
            else -> {
                if(
                    onRoop.isNullOrEmpty()
                ) return fileListBeforePlayMode
                (0..repeatTimes).map {
                    fileListBeforePlayMode
                }.flatten()
            }
        }
    }

    private fun execPlay(
        playPath: String,
        playMode: String?,
        fileList: List<String>,
        notificationBuilder: NotificationCompat.Builder,
        shelllPath: String,
        displayRoopTimes: String,
        cancelPendingIntent: PendingIntent,
        currentTrackFileName: String,
    ){
        val text = getText(
            playPath,
        )
        if(
            text.isNullOrEmpty()
        ){
            downNotification(
                notificationBuilder,
                cancelPendingIntent
            )
            return
        }
        done = true
        val lengthLimit = 500
        execTextToSpeechJob = CoroutineScope(Dispatchers.IO).launch {
            val stringLength = text.length
            val totalTimesSource = stringLength / lengthLimit
            val totalTimes = if(
                stringLength % lengthLimit > 0
            ) totalTimesSource + 1
            else totalTimesSource
            withContext(Dispatchers.IO) {
                for (i in 0 .. 10000) {
                    if (currentBlockNum >= totalTimes
                        && onPressToButton
                    ) currentBlockNum = 0
                    else if (
                        currentBlockNum >= totalTimes
                    ){
                        currentBlockNum = 0
                        break
                    }
                    onPressToButton = false

                    val currentBlockNumEntry = totalTimes - 1
                    val currentBlockNumSource = if(
                        currentBlockNumEntry >= 0
                    ) currentBlockNumEntry
                    else 0
                    if(currentBlockNum < 0) {
                        currentBlockNum = currentBlockNumSource
                    }
                    if(
                        currentBlockNum * lengthLimit >= stringLength
                    ) break
                    val trackFileCon = """
                        |${PlayTrackFileKey.length.name}=${fileList.joinToString("").length}
                        |${PlayTrackFileKey.playMode.name}=${playMode}
                        |${PlayTrackFileKey.order.name}=${currentOrder}
                        |${PlayTrackFileKey.blockNum.name}=${currentBlockNum}
                    """.trimMargin()
                    ExecShellForTts.exec(
                        applicationContext,
                        shelllPath,
                        playPath,
                        currentOrder,
                        displayRoopTimes,
                        currentBlockNum,
                        totalTimes,
                    )
                    FileSystems.writeFile(
                        File(
                            UsePath.cmdclickTempTextToSpeechDirPath,
                            currentTrackFileName,
                        ).absolutePath,
                        trackFileCon
                    )
                    if(onCurrentRoopBreak) break
                    if (i >= stringLength) break
                    val splitTextContent = makeSplitTextContent(
                            text,
                            currentBlockNum * lengthLimit,
                            stringLength,
                            lengthLimit,
                        )

//                    FileSystems.writeFile(
//                        debugTemp,
//                        "write${i}",
//                        splitTextContent
//                    )
                    val utterId = i.toString()
                    delay(200)
                    withContext(Dispatchers.IO) {
                        if(!onCurrentRoopBreak) {
                            textToSpeech?.speak(
                                splitTextContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                utterId
                            )
                        }
                    }
//            withContext(Dispatchers.IO) {
//                FileSystems.writeFile(
//                    debugTemp,
//                    "code${i}",
//                    code.toString()
//                )
//            }
                    done = false
                    val displayTitle = makeDisplayTitle(splitTextContent)
                    val displayTimes =
                        if(totalTimes != 0) {
                            "${currentBlockNum + 1}/${totalTimes} (${displayRoopTimes}"
                        } else "${currentBlockNum + 1} (${displayRoopTimes}"
                    makeProgressNotification(
                        notificationBuilder,
                        displayTitle,
                        displayTimes,
                    )
                    withContext(Dispatchers.IO) {
                        while (true) {
                            monitorUtterLanceProgressListener()
                            if (done) break
                            if(onCurrentRoopBreak) break
                            delay(500)
                        }
                    }
                    withContext(Dispatchers.IO){
                        currentBlockNum++
                    }
                }
            }
            withContext(Dispatchers.IO){
                nextRoop = true
                onCurrentRoopBreak = false
            }
        }
    }

    private fun makeSplitTextContent(
        text: String,
        i: Int,
        stringLength: Int,
        lengthLimit: Int,
    ): String {
        val endPosiEntry = i + lengthLimit
        val endLength = if (
            endPosiEntry <= stringLength
        ) endPosiEntry
        else stringLength
        return subtituteText(
            text,
            i,
            endLength,
        )
    }

    private fun subtituteText(
        text: String,
        startPosi: Int,
        endLength: Int,
    ): String {
        return try {
            text.substring(
                startPosi, endLength
            )
        } catch (e: Exception){
            text.substring(
                startPosi, endLength - 1
            )
        }
    }

    private fun makeDisplayTitle(
        splitTextContent: String
    ): String {
        val displayFileConLimit = 100
        val factSplitTextContent = splitTextContent
            .replace("\n", "")
        val factSplitTextContentSize = factSplitTextContent.length - 1
        val endStrNum = if(
            factSplitTextContentSize <= displayFileConLimit
        ) factSplitTextContentSize
        else displayFileConLimit
        val prefixSource =
            factSplitTextContent
                .substring(0..endStrNum)
        val prefixSourceLength = StringLength.count(prefixSource)
        if(
            prefixSourceLength <= displayFileConLimit
        ) return prefixSource
        val displayFileConLimitHalfSource = displayFileConLimit / 2
        val prefixSourceSize = prefixSource.length - 1
        val displayFileConLimitHalf = if(
            displayFileConLimitHalfSource > prefixSourceSize
        ) prefixSourceSize
        else displayFileConLimitHalfSource
        return prefixSource.substring(0..displayFileConLimitHalf)
    }

    private fun makeProgressNotification(
        notificationBuilder: NotificationCompat.Builder,
        displayTitle: String,
        displayTimes: String,
    ){
        val notificationInstance = notificationBuilder
            .setContentTitle(displayTitle)
            .setContentText(displayTimes)
            .setOngoing(false)
            .build()
        notificationManager?.notify(
            channelNum,
            notificationInstance
        )
    }

    private fun getText(
        playPath: String,
    ): String? {
        if (
            playPath.startsWith(WebUrlVariables.httpsPrefix)
            || playPath.startsWith(WebUrlVariables.httpPrefix)
        ) {
            try {
                val doc = Jsoup.connect(playPath)
                    .timeout(2000)
                    .get()
                return doc.body()
                    .text()
                    .let {
                        chunkText(it)
                    }
            } catch (e: Exception){
                return null
            }
        }
        return ReadText(
            playPath
        ).readText().let {
            chunkText(it)
        }
    }

    private fun monitorUtterLanceProgressListener(){
//        if(
//            textToSpeech?.isSpeaking != true
//        ) return
//        done = true
//        return
        textToSpeech?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    done = true
//                                FileSystems.writeFile(
//                                    debugTemp,
//                                    "done${i}",
//                                    "done${i}"
//                                )
                    Log.d("debug", "progress on Done $utteranceId")
                }

                @Deprecated("Deprecated in Java",
                    ReplaceWith(
                        "Log.d(\"debug\", \"progress on Error \$utteranceId\")",
                        "android.util.Log"
                    )
                )
                override fun onError(utteranceId: String) {
                    Log.d("debug", "progress on Error $utteranceId")
                }

                override fun onStart(utteranceId: String) {
                    Log.d("debug", "progress on Start $utteranceId")
//                                FileSystems.writeFile(
//                                    debugTemp,
//                                    "start${i}",
//                                    "start${i}"
//                                )
                }
            })
    }

    private fun downNotification(
        notificationBuilder:  NotificationCompat.Builder,
        pendingIntent: PendingIntent,
    ){
        notificationBuilder.setSmallIcon(R.drawable.progress_indeterminate_horizontal)
        notificationBuilder.setContentText("text to speech blank")
        notificationBuilder.setDeleteIntent(
            pendingIntent
        )
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.clearActions()
        notificationManager?.notify(
            channelNum,
            notificationBuilder.build()
        )
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        notificationManager?.cancel(channelNum)
    }
}

private fun chunkText(
    text: String
): String {
    val lineLimitCharNum = 25
    return text.replace(
        Regex(
            "(https|http)://[^ \n]*"),
        ""
    ).replace(" ", "\n")
}

private fun getIntValue(
    pastTrackKeyValueList: List<String>,
    keyName: String,
): Int {
    val currentOrderSource = getStrValue(
        pastTrackKeyValueList,
        keyName,
        "0"
    )
    return try {
        currentOrderSource.toInt()
    } catch(e: Exception){
        0
    }
}

private fun getStrValue(
    pastTrackKeyValueList: List<String>,
    keyName: String,
    defaultValue: String
): String {
    return pastTrackKeyValueList.filter{
        it.contains(keyName)
    }.firstOrNull()
        ?.replace("${keyName}=", "")
        ?.trim()
        ?: defaultValue
}

private fun convertFloat(
    value: String?
): Float {
    return try {
        value?.toFloat()
    } catch(e: Exception){
        1.0F
    } ?: 1.0F
}

private enum class PlayModeType {
    shuffle,
    ordinaly,
    reverse,
    number
}

private enum class PlayTrackFileKey {
    length,
    playMode,
    order,
    blockNum,
}
