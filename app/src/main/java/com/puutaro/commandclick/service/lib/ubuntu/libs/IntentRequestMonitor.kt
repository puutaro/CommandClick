package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.icon.CmcClickIcons
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.util.CcScript
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
            BroadcastMonitorFileScheme.intentType.name
        ) ?: return
        when(intentType){
            ReceiveIntentType.broadcast.name
            -> broadcastSender(
                ubuntuService,
                broadcastMap,
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
            ReceiveIntentType.intent.name -> {

            }
        }
    }

    private fun execToast(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
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

    private fun broadcastSender(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>,
    ){
        val broadcastIntent = Intent()
        val action = broadcastMap.get(
            BroadCastSenderSchema.action.name
        ) ?: return
        broadcastIntent.action = action
        val extraPairList = broadcastMap.get(
            BroadCastSenderSchema.extra.name
        )?.let {
            createMap(
                it,
                keySeparator
            )
        }
        extraPairList?.forEach {
            broadcastIntent.putExtra(
                it.first,
                it.second
            )
        }
        ubuntuService.sendBroadcast(broadcastIntent)
    }

    private fun notificationHandler(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val helpCon = broadcastMap.get(HelpKey.help.name)
        if(
            !helpCon.isNullOrEmpty()
        ) return Unit.also {
            responseString += "\n${makeHelpCon()}"
        }
        val typeLaunch = IntentMonitorNotificationType.launch.name
        val typeExit = IntentMonitorNotificationType.exit.name
        val notificatinType = broadcastMap.get(BroadcastMonitorFileScheme.notificationType.name)
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
                val notificationTypeOption = "${BroadcastMonitorFileScheme.notificationType.name.camelToShellArgsName()}/-t"
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
        val channelNumStr = broadcastMap.get(BroadcastMonitorFileScheme.channelNum.name)
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
        val channelNumKey = BroadcastMonitorFileScheme.channelNum.name
        val channeNumOptionName = "${channelNumKey.camelToShellArgsName()}/-cn"
        val channelNumStr = broadcastMap.get(channelNumKey)
            ?: return Unit.also {
                responseString += "\n${requireArgsErrMessage.format(channeNumOptionName)}"
            }
        val importance = decideImportance(
            broadcastMap.get(BroadcastMonitorFileScheme.importance.name)
        )
        val notificationId = listOf(ubuntuService.packageName, importance.str).joinToString(".")
        val delete = broadcastMap.get(BroadcastMonitorFileScheme.delete.name)
        val channelNum = toInt(channelNumStr) ?: return Unit.also {
            responseString += "\n${channeNumOptionName} must be Int"
        }
        val iconName = broadcastMap.get(BroadcastMonitorFileScheme.iconName.name)
        val title = broadcastMap.get(BroadcastMonitorFileScheme.title.name)
        val message = broadcastMap.get(BroadcastMonitorFileScheme.message.name)
        val alertOnce =
            broadcastMap.get(BroadcastMonitorFileScheme.alertOnce.name)
        val channel = NotificationChannel(
            notificationId,
            notificationId,
            importance.int
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
        addButton(
            ubuntuService,
            notificationBuilder,
            broadcastMap.get(BroadcastMonitorFileScheme.button.name)
        )
        setStyle(
            notificationBuilder,
            broadcastMap
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
        broadcastMap: Map<String, String>
    ){
        val styleMap =
            broadcastMap.get(
                BroadcastMonitorFileScheme.notificationStyle.name
            )?.let {
            createMap(
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
            toInt(it) ?: return Unit.also {
                LogSystems.stdWarn("no int value ${it}")
            }
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
        val onDeleteMap = createMap(
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
        notificationBuilder
        notificationBuilder.setOnlyAlertOnce(true)
    }
    private fun addButton(
        ubuntuService: UbuntuService,
        notificationBuilder: NotificationCompat.Builder,
        buttonListStr: String?,
    ){
        val buttonLabelKey = ButtonKey.label.name
        val buttonList = buttonListStr?.split(elementSeparator)
            ?: return
        if(
            buttonList.isEmpty()
        ) return
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
            val buttonTypeMap = createMap(
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
        }.let {
            createMap(
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
    ): NotificationImportanceType {
        return NotificationImportanceType.values().filter {
            it.str == importanceStr
        }.firstOrNull() ?: NotificationImportanceType.LOW
    }

    private fun makeHelpCon(): String {
        val deleteOption = BroadcastMonitorFileScheme.delete.name.camelToShellArgsName()
        val notificationStyleOption = BroadcastMonitorFileScheme.notificationStyle.name.camelToShellArgsName()
        val buttonOption = BroadcastMonitorFileScheme.button.name.camelToShellArgsName()
        val launch = IntentMonitorNotificationType.launch.name
        val exit = IntentMonitorNotificationType.exit.name
        val high = NotificationImportanceType.HIGH.name
        val low = NotificationImportanceType.LOW.name
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
        
        ${BroadcastMonitorFileScheme.notificationType.name.camelToShellArgsName()}
        -t
        : ${launch}/${exit}
            ${launch} -> launch and update notification
            ${exit} -> exit notification
        
        
        ${BroadcastMonitorFileScheme.channelNum.name.camelToShellArgsName()}
        -cn
        : Int
        
        
        ${BroadcastMonitorFileScheme.iconName.name.camelToShellArgsName()}
        -in
        : ${CmcClickIcons.values().joinToString(", ")}
        
        
        ${BroadcastMonitorFileScheme.importance.name.camelToShellArgsName()}
        -i
        : ${high}/${low}
            ${high} -> importance high
            ${low} -> importance low
        
        ${BroadcastMonitorFileScheme.alertOnce.name.camelToShellArgsName()}
        -o
        : once alert

            
        ${BroadcastMonitorFileScheme.title.name.camelToShellArgsName()}
        -t
        : String
        
        
        ${BroadcastMonitorFileScheme.message.name.camelToShellArgsName()}
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
            * enable multiple spedified up to 5 with concat ${elementSeparator}
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
            ${buttonOption}="${label}=button1${comaKeySeparator}${shellPath}=${'$'}{shellPath1}"
            ${buttonOption}="${label}=button2${comaKeySeparator}${shellPath}=${'$'}{shellPath2}"
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

private fun createMap(
    mapEntryStr: String,
    sepalator: String
):List<Pair<String, String>> {
    return mapEntryStr.split(sepalator).map {
        CcScript.makeKeyValuePairFromSeparatedString(
            it,
            "="
        )
    }
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
    toast
}


private enum class BroadCastSenderSchema {
    action,
    extra,
}

private enum class BroadcastMonitorFileScheme {
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

private enum class NotificationStyleSchema {
    type,
    compactActionsInts
}

private enum class NotificationImportanceType(
    val str: String,
    val int: Int,
){
    LOW("low", NotificationManager.IMPORTANCE_LOW),
    HIGH("high", NotificationManager.IMPORTANCE_HIGH),
}

private enum class NotificationStyle{
    normal,
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