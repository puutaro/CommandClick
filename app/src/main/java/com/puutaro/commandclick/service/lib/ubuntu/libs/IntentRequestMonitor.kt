package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.intent.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.icon.CmcClickIcons
import com.puutaro.commandclick.common.variable.intent.TextToSpeechIntentExtra
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadCastSenderSchemaForCommon
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.intent.TextToSpeechIntentSender
import com.puutaro.commandclick.service.lib.NotificationIdToImportance
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.Intent.IntentLauncher
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket

object IntentRequestMonitor {

    private val fieldSeparator = ","
    private const val elementSeparator = "|"
    private const val keySeparator = "!"
    private const val valueSeparator = "&"
    private val requireArgsErrMessage = "%s is required"
    private var responseString = String()

    fun launch(
        ubuntuService: UbuntuService,
    ){
        ubuntuService.ubuntuCoroutineJobsHashMap[
                ProcessManager.UbuntuRunningSystemProcessType.IntentRequestMonitor.name
        ]?.cancel()
        val intentRequestMonitor = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                execIntentMonitor (
                    ubuntuService
                )
            }
        }
        ubuntuService.ubuntuCoroutineJobsHashMap[
                ProcessManager.UbuntuRunningSystemProcessType.IntentRequestMonitor.name
        ] = intentRequestMonitor
    }

    private suspend fun execIntentMonitor (
        ubuntuService: UbuntuService
    ){
        val context = ubuntuService.applicationContext
        val ubuntuFiles = UbuntuFiles(context)
        val ubuntuLaunchCompFile = ubuntuFiles.ubuntuLaunchCompFile

        withContext(Dispatchers.IO) {
            ubuntuService.intentMonitorServerSocket?.close()
        }
        ubuntuService.intentMonitorServerSocket = withContext(Dispatchers.IO) {
            ServerSocket(UsePort.UBUNTU_INTENT_MONITOR_PORT.num)
        }

        while (true) {
            if (
                !ubuntuLaunchCompFile.isFile
            ) {
                delay(100)
                continue
            }
            var isTerminated = false
            responseString = String()
            val client = withContext(Dispatchers.IO) {
                try {
                    LogSystems.stdSys(
                        "accept start"
                    )
                    ubuntuService.intentMonitorServerSocket?.accept()
                } catch (e:Exception){
                    LogSystems.stdErr("${e}")
                    isTerminated = true
                    null
                }
            } ?: return
            if(isTerminated) return
            withContext(Dispatchers.IO) {
                val isr = InputStreamReader(client.getInputStream())
                val br = BufferedReader(isr)
                val writer: OutputStream = client.getOutputStream()
                    ?: return@withContext
                try {
                    //code to read and print headers
                    var headerLine: String? = null
                    var responseCon = String()
                    while (
                        br.readLine().also {
                            headerLine = it
                        }.isNotEmpty()
                    ) {
                        responseCon += "\t$headerLine"
                    }
                    val payload = StringBuilder()
                    while (br.ready()) {
                        payload.append(br.read().toChar())
                    }
                    intentHandler(
                        ubuntuService,
                        payload.toString()
                    )
                    val response = String.format(
                        "HTTP/1.1 200 OK\nContent-Length: %d\r\n\r\n%s",
                        responseString.length,
                        responseString
                    )
                    writer.write(response.toByteArray())
                }catch (e: Exception){
                    client.close()
                    LogSystems.stdErr(
                        "inuptstream err ${e}"
                    )
                }
            }
        }
    }

    private fun intentHandler(
        ubuntuService: UbuntuService,
        broadcastMapStr: String
    ){
        val broadcastMap = createBroadcastMap(
            broadcastMapStr
        )
        if(broadcastMap.isEmpty()) return
        LogSystems.stdSys(
            "broadcastMap ${broadcastMap}"
        )
        val intentType = broadcastMap.get(
            BroadcastMonitorScheme.intentType.name
        ) ?: return
        when(intentType){
            ReceiveIntentType.intent.name
            -> intentSender(
                ubuntuService,
                broadcastMap,
            )
            ReceiveIntentType.broadcast.name
            -> BroadcastSender.send(
                ubuntuService,
                broadcastMap,
                keySeparator
            )
            ReceiveIntentType.notification.name
            -> notificationHandler(
                ubuntuService,
                broadcastMap,
            )
            ReceiveIntentType.toast.name
            -> execToast(
                ubuntuService,
                broadcastMap,
            )
            ReceiveIntentType.textToSpeech.name
            -> execTextToSpeech(
                ubuntuService,
                broadcastMap,
            )
        }
    }

    private fun execTextToSpeech(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
        val helpOption = broadcastMap.get(HelpKey.help.name)
        if(
            !helpOption.isNullOrEmpty()
        ) return Unit.also {
            responseString += "\n${makeHelpConForTextToSpeech()}"
        }
        val launchType = broadcastMap.get(TextToSpeechCliSchema.launchType.name)
            ?: return
        when(launchType){
            TextToSpeechLaunchType.launch.name
            -> textToSpeechLauncher(
                ubuntuService,
                broadcastMap,
            )
            TextToSpeechLaunchType.exit.name
            -> ubuntuService.applicationContext.let {
                    ubuntuService.applicationContext.stopService(
                        Intent(it, TextToSpeechService::class.java)
                    )
                }
        }
    }

    private fun textToSpeechLauncher(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
        val listFilePath = broadcastMap.get(TextToSpeechCliSchema.listFilePath.name)
            ?: return
        val currentAppDirName = broadcastMap.get(TextToSpeechCliSchema.currentAppDirName.name)
            ?: String()
        val fannelRawName = broadcastMap.get(TextToSpeechCliSchema.fannelRawName.name)
            ?: String()
        val extraSettingMapStr = broadcastMap.get(TextToSpeechCliSchema.extraSettingMapStr.name)
            ?: String()
        TextToSpeechIntentSender.send(
            ubuntuService.applicationContext,
            currentAppDirName,
            fannelRawName,
            listFilePath,
            extraSettingMapStr,
        )
    }

    private fun intentSender(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
        val helpOption = broadcastMap.get(HelpKey.help.name)
        if(
            !helpOption.isNullOrEmpty()
        ) return Unit.also {
            responseString += "\n${makeHelpConForIntent()}"
        }
        val action = broadcastMap.get(
            IntentMonitorSchema.action.name
        ) ?: String()
        val uriStr = broadcastMap.get(
            IntentMonitorSchema.uriStr.name
        ) ?: String()
        val extraStrListTabSepa = makeTabSepaListStr(
            broadcastMap,
            IntentMonitorSchema.extraStrs.name
        )
        val extraIntListTabSepa = makeTabSepaListStr(
            broadcastMap,
            IntentMonitorSchema.extraInts.name
        )
        val extraLongListTabSepa = makeTabSepaListStr(
            broadcastMap,
            IntentMonitorSchema.extraLongs.name
        )
        val extraFloatListTabSepa = makeTabSepaListStr(
            broadcastMap,
            IntentMonitorSchema.extraFloats.name
        )
        IntentLauncher.send(
            ubuntuService.applicationContext,
            action,
            uriStr,
            extraStrListTabSepa,
            extraIntListTabSepa,
            extraLongListTabSepa,
            extraFloatListTabSepa,
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

    }

    private fun makeTabSepaListStr(
        broadcastMap: Map<String, String>,
        extraShema: String
    ): String {
        return broadcastMap.get(
            extraShema
        )?.replace(keySeparator, "\t") ?: String()
    }

    private fun execToast(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
        val helpOption = broadcastMap.get(HelpKey.help.name)
        if(
            !helpOption.isNullOrEmpty()
        ) return Unit.also {
            responseString += "\n${makeHelpConForToast()}"
        }
        val message = broadcastMap.get(
            ToastSchema.message.name
        )
        val span = broadcastMap.get(
            ToastSchema.span.name
        )
        val context = ubuntuService.applicationContext
        when(span){
            ToastSpan.long.name -> {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun notificationHandler(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val helpOption = broadcastMap.get(HelpKey.help.name)
        if(
            !helpOption.isNullOrEmpty()
        ) return Unit.also {
            responseString += "\n${makeHelpConForNotification()}"
        }
        val typeLaunch = IntentMonitorNotificationType.launch.name
        val typeExit = IntentMonitorNotificationType.exit.name
        val notificatinType = broadcastMap.get(BroadcastMonitorScheme.notificationType.name)
            ?: return
        when(notificatinType) {
            typeLaunch
            -> broadCastLauncher(
                ubuntuService,
                broadcastMap,
            )

            typeExit
            -> notificationExiter(
                ubuntuService,
                broadcastMap,
            )
            else
            -> {
                val notificationTypeOption = "${BroadcastMonitorScheme.notificationType.name.camelToShellArgsName()}/-t"
                responseString +=
                    "\n${notificationTypeOption} must be \"${typeLaunch}\" or \"${typeExit}\""
            }
        }
    }

    private fun notificationExiter(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val context = ubuntuService.applicationContext
        val channelNumStr = broadcastMap.get(BroadcastMonitorScheme.channelNum.name)
            ?: return
        val channelNum = toInt(channelNumStr) ?: return
        ubuntuService.notificationBuilderHashMap.remove(channelNum)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(channelNum)

    }

    private fun broadCastLauncher(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val context = ubuntuService.applicationContext
        val channelNumKey = BroadcastMonitorScheme.channelNum.name
        val channeNumOptionName = "${channelNumKey.camelToShellArgsName()}/-cn"
        val channelNumStr = broadcastMap.get(channelNumKey)
            ?: return Unit.also {
                responseString += "\n${requireArgsErrMessage.format(channeNumOptionName)}"
            }
        val notificationIdToImportance = decideImportance(
            broadcastMap.get(BroadcastMonitorScheme.importance.name)
        )
        val notificationId = decideNotiIdByImportance(
            notificationIdToImportance
        )
        val delete = broadcastMap.get(BroadcastMonitorScheme.delete.name)
        val channelNum = toInt(channelNumStr) ?: return Unit.also {
            responseString += "\n${channeNumOptionName} must be Int"
        }
        val iconName = broadcastMap.get(BroadcastMonitorScheme.iconName.name)
        val title = broadcastMap.get(BroadcastMonitorScheme.title.name)
        val message = broadcastMap.get(BroadcastMonitorScheme.message.name)
        val alertOnce =
            broadcastMap.get(BroadcastMonitorScheme.alertOnce.name)
        val channel = NotificationChannel(
            notificationId,
            notificationId,
            notificationIdToImportance.importance
        )
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = ubuntuService.notificationBuilderHashMap.get(channelNum) ?: let {
            LogSystems.stdSys(
                "builder create"
            )
            val newNotificationBuilder = NotificationCompat.Builder(
                context,
                notificationId
            )
            ubuntuService.notificationBuilderHashMap.put(
                channelNum,
                newNotificationBuilder
            )
            newNotificationBuilder
        }
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)

        setSmallIcon(
            notificationBuilder,
            iconName,
        )
        setAlertOnce(
            notificationBuilder,
            alertOnce
        )
        setDeleteIntent(
            ubuntuService,
            notificationBuilder,
            delete,
        )
        val buttonListSize = addButton(
            ubuntuService,
            notificationBuilder,
            broadcastMap.get(BroadcastMonitorScheme.button.name)
        )
        setStyle(
            notificationBuilder,
            broadcastMap,
            buttonListSize
        )
        val notificationInstance = notificationBuilder.build()
        notificationManager.notify(
            channelNum,
            notificationInstance
        )
    }

    private fun setSmallIcon(
        notificationBuilder:  NotificationCompat.Builder,
        iconName: String?,
    ){
        if(
            iconName.isNullOrEmpty()
        ) return
        val iconId = CmcClickIcons.values().filter {
            it.str == iconName
        }.firstOrNull()?.id ?: return Unit.also {
            LogSystems.stdWarn("no macro icon name ${iconName}")
        }
        notificationBuilder.setSmallIcon(iconId)
    }

    private fun setStyle(
        notificationBuilder: NotificationCompat.Builder,
        broadcastMap: Map<String, String>,
        buttonListSize: Int,
    ){
        if(
            buttonListSize == 0
        ) return
        val buttonListTotalIndex = buttonListSize - 1
        val styleMap =
            broadcastMap.get(
                BroadcastMonitorScheme.notificationStyle.name
            )?.let {
                CmdClickMap.createMap(
                it,
                keySeparator
            ).toMap()
        } ?: return
        if(styleMap.isEmpty()) return
        val styleType = styleMap.get(NotificationStyleSchema.type.name)
        if(styleType != NotificationStyle.media.name) return
        val compactActionsInts = styleMap.get(
            NotificationStyleSchema.compactActionsInts.name
        )?.split(valueSeparator)?.map {
            val posi = toInt(it) ?: return Unit.also {
                LogSystems.stdWarn("no int value ${it}")
            }
            if(posi > buttonListTotalIndex) {
                LogSystems.stdWarn("over index: $posi")
                return@map 0
            }
            posi
        }?.toIntArray() ?: return Unit.also {
            LogSystems.stdWarn("include no int value ${styleMap}")
        }
        try {
            notificationBuilder.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(*compactActionsInts)
            )
        } catch (e: Exception){
            LogSystems.stdWarn("$e")
        }
    }

    private fun setDeleteIntent(
        ubuntuService: UbuntuService,
        notificationBuilder: NotificationCompat.Builder,
        onDeleteMapStr: String?
    ){
        if(
            onDeleteMapStr.isNullOrEmpty()
        ) return
        val onDeleteMap = CmdClickMap.createMap(
            onDeleteMapStr,
            keySeparator
        ).toMap()
        val pendingIntent = pendingIntentCreatorWraper(
            ubuntuService,
            onDeleteMap,
            System.currentTimeMillis().toInt()
        )

        notificationBuilder.setDeleteIntent(
            pendingIntent
        )
    }

    private fun setAlertOnce(
        notificationBuilder: NotificationCompat.Builder,
        alertOnce: String?
    ){
        if(
            alertOnce.isNullOrEmpty()
        ) return
        notificationBuilder.setOnlyAlertOnce(true)
    }
    private fun addButton(
        ubuntuService: UbuntuService,
        notificationBuilder: NotificationCompat.Builder,
        buttonListStr: String?,
    ): Int {
        val buttonLabelKey = ButtonKey.label.name
        val buttonList = buttonListStr?.split(elementSeparator)
            ?: return 0
        if(
            buttonList.isEmpty()
        ) return 0
        LogSystems.stdSys(
            "buttonList: ${buttonList.joinToString("\n")}\n"
        )
        notificationBuilder.clearActions()
        buttonList.indices.forEach {
            index ->
            val buttonMapEl = buttonList[index]
            if(
                buttonMapEl.isEmpty()
            ) return@forEach
            val buttonTypeMap = CmdClickMap.createMap(
                buttonMapEl,
                keySeparator
            ).toMap()
            val buttonName = buttonTypeMap.get(buttonLabelKey)
                ?: return@forEach
            val buttonIcon = ButtonNameToIcon.values().filter {
                it.str == buttonName
            }.firstOrNull()?.int ?: R.drawable.ic_media_play
            val requestCode = System.currentTimeMillis().toInt()
            pendingIntentCreatorWraper(
                ubuntuService,
                buttonTypeMap,
                requestCode
            )?.let {
                notificationBuilder.addAction(
                    buttonIcon,
                    buttonName,
                    it,
                )
            }
        }
        return buttonList.size
    }

    private fun pendingIntentCreatorWraper(
        ubuntuService: UbuntuService,
        targetMap: Map<String, String>,
        requestCode: Int = 0
    ): PendingIntent? {
        val context = ubuntuService.applicationContext
        val shellPath = targetMap.get(ButtonKey.shellPath.name)
            ?: return null
        val execType = targetMap.get(ButtonKey.execType.name) ?: ExecType.fore.name
        val argsTabSepaStr =
            targetMap
                .get(ButtonKey.args.name)
                ?.replace(valueSeparator, "\t")
                ?: String()
        val timeout = targetMap.get(ButtonKey.timeout.name) ?: "200"
        val backgroundAction = BroadCastIntentScheme.BACKGROUND_CMD_START.action
        val execAction = when(execType){
            ExecType.back.name -> backgroundAction
            else -> BroadCastIntentScheme.FOREGROUND_CMD_START.action
        }
        val extraList = when(execAction){
            backgroundAction -> listOf(
                Pair(
                    UbuntuServerIntentExtra.backgroundShellPath.schema,
                    shellPath
                ),
                Pair(
                    UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
                    argsTabSepaStr
                ),
                Pair(
                    UbuntuServerIntentExtra.backgroundMonitorFileName.schema,
                    UsePath.cmdClickMonitorFileName_2
                )
            )
            else -> listOf(
                Pair(
                    UbuntuServerIntentExtra.foregroundShellPath.schema,
                    shellPath
                ),
                Pair(
                    UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema,
                    argsTabSepaStr
                ),
                Pair(
                    UbuntuServerIntentExtra.foregroundTimeout.schema,
                    timeout
                )
            )
        }
        return PendingIntentCreator.create(
            context,
            execAction,
            extraList,
            requestCode
        )
    }

    private fun createBroadcastMap(
        broadcastMapStr: String
    ): Map<String, String> {
        return broadcastMapStr
            .trimSeparatorGap(fieldSeparator)
            .trimSeparatorGap(elementSeparator)
            .trimSeparatorGap(keySeparator)
            .trimSeparatorGap(valueSeparator)
            .split("\n").let {
            SettingFile.formSettingContents(it)
        }.let { CmdClickMap.createMap(
                it,
                fieldSeparator
            )
        }.toMap()
    }

    private fun String.trimSeparatorGap(
        separator: String,
    ): String {
        return this.split(separator).map {
            it.trim()
        }.joinToString(separator)
    }

    private fun decideImportance(
        importanceStr: String?,
    ): NotificationIdToImportance {
        return NotificationIdToImportance.values().filter {
            getImportanceFromNotiId(it.id) == importanceStr
        }.firstOrNull() ?: NotificationIdToImportance.LOW
    }

    private fun decideNotiIdByImportance(
        notificationIdToImportance: NotificationIdToImportance
    ): String {
        val notiId = notificationIdToImportance.id
        return NotificationIdToImportance.values().filter {
            it.id == notiId
        }.firstOrNull()?.id ?: NotificationIdToImportance.LOW.id
    }

    private fun getImportanceFromNotiId(
        notiId: String
    ): String? {
        return notiId.split(".").lastOrNull()
    }

    private fun makeHelpConForIntent(): String {
        val extraStrsOption = IntentMonitorSchema.extraStrs.name.camelToShellArgsName()
        val extraIntsOption = IntentMonitorSchema.extraInts.name.camelToShellArgsName()
        val extraLongsOption = IntentMonitorSchema.extraLongs.name.camelToShellArgsName()
        val extraFloatsOption = IntentMonitorSchema.extraFloats.name.camelToShellArgsName()
        val comaKeySeparator = ","

        return """
        
        ### Intent sender in ${BuildConfig.APPLICATION_ID}
        
        ${IntentMonitorSchema.action.name.camelToShellArgsName()}
        -a
        : intent action string
        
        
        ${IntentMonitorSchema.uriStr.name.camelToShellArgsName()}
        -u
        : uri string
        
        
        ${extraStrsOption}
        -s
        : intent extra string
        option
            format: ${'$'}{key1}=${'$'}{valueStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueStr2}${comaKeySeparator}..

        ex) 
            ${extraStrsOption}="${'$'}{key1}=${'$'}{valueStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueStr2}${comaKeySeparator}.."
        
                
        ${extraIntsOption}
        -i
        : intent extra int
        option
            format: ${'$'}{key1}=${'$'}{valueIntStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueIntStr2}${comaKeySeparator}..

        ex) 
            ${extraIntsOption}="${'$'}{key1}=${'$'}{valueIntStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueIntStr2}${comaKeySeparator}.."

        
        ${extraLongsOption}
        -l
        : intent extra long
        option
            format: ${'$'}{key1}=${'$'}{valueLongStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueLongStr2}${comaKeySeparator}..

        ex) 
            ${extraLongsOption}="${'$'}{key1}=${'$'}{valueLongStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueLongStr2}${comaKeySeparator}.."


        ${extraFloatsOption}
        -f
        : intent extra string
        option
            format: ${'$'}{key1}=${'$'}{valueFloatStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueFloatStr2}${comaKeySeparator}..

        ex) 
            ${extraFloatsOption}="${'$'}{key1}=${'$'}{valueFloatStr2}${comaKeySeparator}${'$'}{key1}=${'$'}{valueFloatStr2}${comaKeySeparator}.."
      
    """.trimIndent()
    }

    private fun makeHelpConForTextToSpeech(): String {
        val verticalBarSepalator = "|"
        val playModeSchema = TextToSpeechIntentExtra.playMode.name
        val onRoopSchema = TextToSpeechIntentExtra.onRoop.name
        val playNumberSchema = TextToSpeechIntentExtra.onRoop.name
        val transModeSchema = TextToSpeechIntentExtra.transMode.name
        val onTrackSchema = TextToSpeechIntentExtra.onTrack.name
        val speedSchema = TextToSpeechIntentExtra.speed.name
        val pitchSchema = TextToSpeechIntentExtra.pitch.name
        val importance = TextToSpeechIntentExtra.importance.name
        return """
        
        ### TextToSpeech manager
        
        ${TextToSpeechCliSchema.launchType.name.camelToShellArgsName()}
        -t
         : ${TextToSpeechLaunchType.launch.name}/${TextToSpeechLaunchType.exit.name}
        
        ${TextToSpeechCliSchema.listFilePath.name.camelToShellArgsName()}
        -l
        : play list path
        
        ${TextToSpeechCliSchema.extraSettingMapStr.name.camelToShellArgsName()}
        -e
        : extra map str
        option
            format: ${'$'}{key1}=${'$'}{valueFloatStr2}${verticalBarSepalator}${'$'}{key1}=${'$'}{valueFloatStr2}${verticalBarSepalator}..
        
        optional key
            ${importance}: Notification importance, high/low
            ${playModeSchema}: Play mode switch: ordinaly(default), shuffle, reverse, number
            ${onRoopSchema}: Some string: roop, "": no roop
            ${playNumberSchema}:  Play list order number string
            ${transModeSchema}: Select language: en(english), zw(chinese), sp(spanish), ko(korean), ja(japanese)
            ${onTrackSchema}: Save track switch: "", on
            ${speedSchema}: Speech speed int string, base '50',
            ${pitchSchema}: Speech pitch int string, base '50'
        
        ${TextToSpeechCliSchema.currentAppDirName.name.camelToShellArgsName()}
        -d
        : current app direcotry(fannel parent directory)
        
        ${TextToSpeechCliSchema.fannelRawName.name.camelToShellArgsName()}
        -f
        : fannle name without extend
        
        ex)
            tspeech \
                -t "${TextToSpeechLaunchType.launch.name} \
                -l "{play list path}" \
                -e "${playModeSchema}=shuffle" \
                -e "${onRoopSchema}=on" \
                -e "${transModeSchema}=en" \
                -e "${onTrackSchema}=on" \
                -e "${pitchSchema}=50"
            
    """.trimIndent()
    }

    private fun makeHelpConForNotification(): String {
        val deleteOption = BroadcastMonitorScheme.delete.name.camelToShellArgsName()
        val notificationStyleOption = BroadcastMonitorScheme.notificationStyle.name.camelToShellArgsName()
        val buttonOption = BroadcastMonitorScheme.button.name.camelToShellArgsName()
        val launch = IntentMonitorNotificationType.launch.name
        val exit = IntentMonitorNotificationType.exit.name
        val high = NotificationIdToImportance.HIGH.name
        val low = NotificationIdToImportance.LOW.name
        val label = ButtonKey.label.name
        val shellPath = ButtonKey.shellPath.name
        val args = ButtonKey.args.name
        val execType = ButtonKey.execType.name
        val timeout = ButtonKey.timeout.name
        val media = NotificationStyle.media.name
        val notificatinStyleType = NotificationStyleSchema.type.name
        val compactActionsInts = NotificationStyleSchema.compactActionsInts.name
        val comaKeySeparator = ","
        return """
        
        ### Notification management command in ${BuildConfig.APPLICATION_ID}
        
        ${BroadcastMonitorScheme.notificationType.name.camelToShellArgsName()}
        -t
        : ${launch}/${exit}
            ${launch} -> launch and update notification
            ${exit} -> exit notification
        
        
        ${BroadcastMonitorScheme.channelNum.name.camelToShellArgsName()}
        -cn
        : Int
        
        
        ${BroadcastMonitorScheme.iconName.name.camelToShellArgsName()}
        -in
        : ${CmcClickIcons.values().joinToString(", ")}
        
        
        ${BroadcastMonitorScheme.importance.name.camelToShellArgsName()}
        -i
        : ${high}/${low}
            ${high} -> importance high
            ${low} -> importance low
        
        ${BroadcastMonitorScheme.alertOnce.name.camelToShellArgsName()}
        -o
        : once alert

            
        ${BroadcastMonitorScheme.title.name.camelToShellArgsName()}
        -t
        : String
        
        
        ${BroadcastMonitorScheme.message.name.camelToShellArgsName()}
        -m
        : String


        ${deleteOption}
        -d
        : In delete, be executed
        option
            * enalble this option by concat '${comaKeySeparator}' 
            ${shellPath}: execute shell script path
            ${args}: script args with '${valueSeparator}' as separator
        ex) 
            ${deleteOption}="${shellPath}=${'$'}{shell path}${comaKeySeparator}${args}=aa${valueSeparator}bb" 
        
        
        ${buttonOption}
        -b
        : Specify about button
        option
            * enable this option by concat '${comaKeySeparator}'
            ${label}: button label
                spedify icon by bellow MACRO in media style
                MACRO: ${ButtonNameToIcon.values().joinToString(", ")}
            ${shellPath}: exec shell script path
            ${args}: script args with '${valueSeparator}' as separator
            ${execType}: exec type 
                ${ExecType.fore.name} -> foreground
                ${ExecType.back.name} -> background
            ${timeout}: time out mili sec[Int] in foreground
        ex) 
            ${buttonOption}="${label}=button1${comaKeySeparator}${shellPath}=${'$'}{shellPath1},args=arg1"
            ${buttonOption}="${label}=button2${comaKeySeparator}${shellPath}=${'$'}{shellPath2},args=arg1${valueSeparator}arg2"
            ${buttonOption}="${label}=button3${comaKeySeparator}${shellPath}=${'$'}{shellPath3}"
            ${buttonOption}="${label}=button4${comaKeySeparator}${shellPath}=${'$'}{shellPath4}"
            ${buttonOption}="${label}=button5${comaKeySeparator}${shellPath}=${'$'}{shellPath5}"
        
        
        $notificationStyleOption
        -s
        : Specify about media
        option
            * enable this option by concat '${comaKeySeparator}'
            ${notificatinStyleType}: only "${media}"
            ${compactActionsInts}: specify button index up to 3 in compact,  
        ex) 
            ${notificationStyleOption}="${notificatinStyleType}=${media}${comaKeySeparator}${compactActionsInts}=0${valueSeparator}1${valueSeparator}3"      
    """.trimIndent()
    }

    private fun makeHelpConForBroadcast(): String {
        return """
        
        ### Broadcast sender
        
        ${BroadCastSenderSchemaForCommon.action.name.camelToShellArgsName()}
        -a
        : Intent action in broadcast
        
        ${BroadCastSenderSchemaForCommon.extras.name.camelToShellArgsName()}
        -e
        : Intent extra string in broadcast
        option
            * enable multiple spedified
        
        ex)
            send-broadcast \
                -a "com.puutaro.commandclick.url.launch" \
                -e "https://github.com/puutaro/CommandClick"
            
    """.trimIndent()
    }

    private fun makeHelpConForToast(): String {
        return """
        
        ### Toast message launcher
        
        
        arg 
        : message
        
        option
        ${ToastSpan.short.name.camelToShellArgsName()}
        -s
        : short (default)
        
        ${ToastSpan.long.name.camelToShellArgsName()}
        -l
        : long
        
        ex)
            toast "CommandClcik"
        ex)
            toast -l "CommandClcik"
            
    """.trimIndent()
    }
}


private fun String.camelToShellArgsName(): String {
    val pattern = "(?<=.)[A-Z]".toRegex()
    return "--" + this.replace(pattern, "-$0").lowercase()
}

private fun toInt(numStr: String?): Int? {
    return try {
        numStr?.toInt()
    } catch (e: Exception){
        LogSystems.stdErr(
            "${e}"
        )
        null
    }

}


private enum class TextToSpeechCliSchema {
    launchType,
    currentAppDirName,
    fannelRawName,
    listFilePath,
    extraSettingMapStr,
}

private enum class TextToSpeechLaunchType {
    launch,
    exit,
}


private enum class ToastSchema {
    message,
    span
}

private enum class ToastSpan {
    short,
    long,
}

private enum class ExecType {
    back,
    fore,
}

private enum class HelpKey {
    help
}

private enum class ReceiveIntentType {
    broadcast,
    notification,
    intent,
    textToSpeech,
    toast
}

private enum class BroadcastMonitorScheme {
    intentType,
    notificationType,
    notificationStyle,
    channelNum,
    iconName,
    importance,
    title,
    alertOnce,
    message,
    delete,
    button
}

private enum class IntentMonitorSchema{
    action,
    uriStr,
    extraStrs,
    extraInts,
    extraLongs,
    extraFloats,
}


private enum class NotificationStyleSchema {
    type,
    compactActionsInts
}
private enum class NotificationStyle{
    media
}


private enum class IntentMonitorNotificationType {
    launch,
    exit
}

private enum class ButtonKey {
    label,
    shellPath,
    execType,
    args,
    timeout,
}


private enum class ButtonNameToIcon(
    val str: String,
    val int: Int,
) {
    CANCEL("CANCEL", R.drawable.ic_menu_close_clear_cancel),
    PREVIOUS("PREVIOUS", R.drawable.ic_media_previous),
    FROM("FROM", R.drawable.ic_media_rew),
    STOP("PAUSE", R.drawable.ic_media_pause),
    PLAY("PLAY", R.drawable.ic_media_play),
    TO("TO", R.drawable.ic_media_ff),
    NEXT("NEXT", R.drawable.ic_media_next),
}