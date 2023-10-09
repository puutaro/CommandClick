package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.icon.CmcClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object IntentRequestMonitor {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private const val cmdClickMonitorFileName_2 = UsePath.cmdClickMonitorFileName_2
    private const val elementSeparator = "|"
    private const val keySeparator = "!"
    private const val valueSeparator = "&"

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
        val funcName = object{}.javaClass.enclosingMethod?.name
        val context = ubuntuService.applicationContext
        val cmdclickTempIntentMonitorDirPath = UsePath.cmdclickTempIntentMonitorDirPath
        val cmdclickTmpIntentMonitorRequestFileName = UsePath.cmdclickTmpIntentMonitorRequestFileName
        val cmdclickTmpIntentMonitorRequestFile = File(
            "${UsePath.cmdclickTempIntentMonitorDirPath}/${cmdclickTmpIntentMonitorRequestFileName}"
        )
        val ubuntuFiles = UbuntuFiles(context)
        val ubuntuLaunchCompFile = ubuntuFiles.ubuntuLaunchCompFile
        while (true) {
            delay(100)
            FileSystems.createDirs(
                cmdclickTempIntentMonitorDirPath
            )
            if (
                !cmdclickTmpIntentMonitorRequestFile.isFile
            ) continue
            if (
                !ubuntuLaunchCompFile.isFile
            ) continue
            val broadcastMapStr = readMonitorText(
                cmdclickTempIntentMonitorDirPath,
                cmdclickTmpIntentMonitorRequestFileName,
            )
            val broadcastMap = createBroadcastMap(
                broadcastMapStr
            )
            FileSystems.removeFiles(
                cmdclickTempIntentMonitorDirPath,
                cmdclickTmpIntentMonitorRequestFileName
            )
            if(broadcastMap.isEmpty()) continue
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                cmdClickMonitorFileName_2,
                "### ${this::javaClass.name} ${funcName}\nbroadcastMap ${broadcastMap}"
            )
            val intentType = broadcastMap.get(
                BroadcastMonitorFileScheme.intentType.name
            )
                ?: continue
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

    private suspend fun readMonitorText(
        cmdclickTempIntentMonitorDirPath: String,
        cmdclickTmpIntentMonitorRequestFileName: String,
    ): String {
        var secondMonitorText = String()
        for(i in 1..30) {
            val firstMonitorText = ReadText(
                cmdclickTempIntentMonitorDirPath,
                cmdclickTmpIntentMonitorRequestFileName
            ).readText()
            delay(100)
            secondMonitorText = ReadText(
                cmdclickTempIntentMonitorDirPath,
                cmdclickTmpIntentMonitorRequestFileName
            ).readText()
            if(
                secondMonitorText != firstMonitorText
            ) continue
            break
        }
        return secondMonitorText
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
        val notificatinType = broadcastMap.get(BroadcastMonitorFileScheme.notificationType.name)
            ?: return
        when(notificatinType) {
            IntentMonitorNotificationType.launch.name
            -> broadCastLauncher(
                ubuntuService,
                broadcastMap,
            )

            IntentMonitorNotificationType.exit.name
            -> broadcastExiter(
                ubuntuService,
                broadcastMap,
            )
        }
    }

    private fun broadcastExiter(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val context = ubuntuService.applicationContext
        val channelNumStr = broadcastMap.get(BroadcastMonitorFileScheme.channelNum.name)
            ?: return
        val channelNum = toInt(channelNumStr) ?: return
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(channelNum)

    }

    private fun broadCastLauncher(
        ubuntuService: UbuntuService,
        broadcastMap: Map<String, String>
    ){
        val context = ubuntuService.applicationContext
        val channelNumStr = broadcastMap.get(BroadcastMonitorFileScheme.channelNum.name)
            ?: return
        val importance = decideImportance(
            broadcastMap.get(BroadcastMonitorFileScheme.importance.name)
        )
        val notificationId = listOf(ubuntuService.packageName, importance.str).joinToString(".")
        val onDelete = broadcastMap.get(BroadcastMonitorFileScheme.onDelete.name)
        val channelNum = toInt(channelNumStr) ?: return
        val iconName = broadcastMap.get(BroadcastMonitorFileScheme.iconName.name)
            ?: return
        val title = broadcastMap.get(BroadcastMonitorFileScheme.title.name)
            ?: return
        val message = broadcastMap.get(BroadcastMonitorFileScheme.message.name)
        val channel = NotificationChannel(
            notificationId,
            notificationId,
            importance.int
        )
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(
            context,
            notificationId
        )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
        setSmallIcon(
            notificationBuilder,
            iconName,
        )
        setDeleteIntent(
            ubuntuService,
            notificationBuilder,
            onDelete,
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
            iconName == null
        ) return
        val iconId = CmcClickIcons.values().filter {
            it.str == iconName
        }.firstOrNull()?.id ?: return
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
            toInt(it) ?: 0
        }?.toIntArray() ?: return
        notificationBuilder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(*compactActionsInts)
        )
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

    private fun addButton(
        ubuntuService: UbuntuService,
        notificationBuilder: NotificationCompat.Builder,
        buttonListStr: String?,
    ){
        val funcName = object{}.javaClass.enclosingMethod?.name
        val buttonLabelKey = ButtonKey.label.name
        val buttonList = buttonListStr?.split(elementSeparator)
            ?: return
        if(
            buttonList.isEmpty()
        ) return
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
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                cmdClickMonitorFileName_2,
                "### ${this::javaClass.name} ${funcName}\nbuttonTypeMap: ${buttonTypeMap}\n"
            )
            val buttonIcon = ButtonNameToIcon.values().filter {
                it.str == buttonName
            }.firstOrNull()?.int ?: R.drawable.ic_media_play
            val requestCode = System.currentTimeMillis().toInt()
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                cmdClickMonitorFileName_2,
                "requestCode: ${requestCode}\t"
            )
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
        return broadcastMapStr.split("\n").let {
            SettingFile.formSettingContents(it)
        }.let {
            createMap(
                it,
                ","
            )
        }.toMap()
    }

    private fun decideImportance(
        importanceStr: String?,
    ): NotificationImportanceType {
        return NotificationImportanceType.values().filter {
            it.str == importanceStr
        }.firstOrNull() ?: NotificationImportanceType.LOW
    }
}

private fun toInt(numStr: String?): Int? {
    return try {
        numStr?.toInt()
    } catch (e: Exception){
        FileSystems.updateFile(
            UsePath.cmdclickMonitorDirPath,
            UsePath.cmdClickMonitorFileName_2,
            "### ${LocalDateTime.now()} err\n${e}"
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
    message,
    onDelete,
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
