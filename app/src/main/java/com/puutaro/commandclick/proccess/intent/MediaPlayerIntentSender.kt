package com.puutaro.commandclick.proccess.intent

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.intent.extra.MusicPlayerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap

object MediaPlayerIntentSender {

    private const val keySeparator = '|'
    fun send(
        context: Context?,
        currentAppDirName: String,
        fannelRawName: String,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        try {
            exeSend(
                context,
                currentAppDirName,
                fannelRawName,
                listFilePath,
                extraSettingMapStr,
            )
        }catch (e: Exception){
            ToastUtils.showLong(e.toString())
        }
    }

    private fun exeSend(
        context: Context?,
        currentAppDirName: String,
        fannelRawName: String,
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        val musicPlayerServiceIntent = Intent(context, MusicPlayerService::class.java)
        val extraSettingMap = CmdClickMap.createMap(
            extraSettingMapStr,
            keySeparator
        ).toMap()

        val listFilePathScheme = MusicPlayerIntentExtra.LIST_FILE_PATH.scheme
        musicPlayerServiceIntent.putExtra(
            listFilePathScheme,
            listFilePath
        )
        val importanceScheme = MusicPlayerIntentExtra.IMPORTANCE.scheme
        extraSettingMap.get(importanceScheme).let {
            FileSystems.writeFile(
                UsePath.mediaPlayerServiceConfigPath,
                "${importanceScheme}\t${it ?: String()}"
            )
//            musicPlayerServiceIntent.putExtra(
//                importanceScheme,
//                it
//            )
        }
        val playModeScheme = MusicPlayerIntentExtra.PLAY_MODE.scheme
        extraSettingMap.get(playModeScheme).let {
            musicPlayerServiceIntent.putExtra(
                playModeScheme,
                it
            )
        }
        val onLoopScheme = MusicPlayerIntentExtra.ON_LOOP.scheme
        extraSettingMap.get(onLoopScheme).let {
            musicPlayerServiceIntent.putExtra(
                onLoopScheme,
                it
            )
        }
        val playNumberScheme = MusicPlayerIntentExtra.PLAY_NUMBER.scheme
        extraSettingMap.get(playNumberScheme).let {
            musicPlayerServiceIntent.putExtra(
                playNumberScheme,
                it
            )
        }
        val onTrackScheme = MusicPlayerIntentExtra.ON_TRACK.scheme
        extraSettingMap.get(onTrackScheme).let {
            musicPlayerServiceIntent.putExtra(
                onTrackScheme,
                it
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "music_extraSettingMap.txt").absolutePath,
//            listOf(
//                "extraSettingMap: ${extraSettingMap}",
//                "extraSettingMap.get(onTrackScheme): ${extraSettingMap.get(onTrackScheme)}"
//            ).joinToString("\n")
//        )
        val currentAppDirNameScheme = MusicPlayerIntentExtra.CURRENT_APP_DIR_NAME.scheme
        musicPlayerServiceIntent.putExtra(
            currentAppDirNameScheme,
            currentAppDirName
        )
        val scriptRawNameScheme = MusicPlayerIntentExtra.SCRIPT_RAW_NAME.scheme
        musicPlayerServiceIntent.putExtra(
            scriptRawNameScheme,
            fannelRawName
        )
        val shellPathScheme = MusicPlayerIntentExtra.SHELL_PATH.scheme
        extraSettingMap.get(shellPathScheme).let {
            musicPlayerServiceIntent.putExtra(
                shellPathScheme,
                it
            )
        }
        val shellArgsScheme = MusicPlayerIntentExtra.SHELL_ARGS.scheme
        extraSettingMap.get(shellArgsScheme).let {
            musicPlayerServiceIntent.putExtra(
                shellArgsScheme,
                it
            )
        }
        context?.startForegroundService(musicPlayerServiceIntent)
    }
}
