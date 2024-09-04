package com.puutaro.commandclick.service.lib.music_player.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.ResAndProcess
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.MusicPlayerMaker
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


object ExecMusicPlay {

    private const val queryStr = "?v="

    fun init(){
        FileSystems.removeAndCreateDir(
            UsePath.mediaPlayerServiceStreamingDirPath
        )
    }

    fun exit(context: Context){
        AudioStreamingMapExtractor.exit(context)
    }

    fun playHandler(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
        playIndex: Int,
        fileListConBeforePlayMode: String,
    ){
        val context = musicPlayerService.applicationContext
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "music_last.txt").absolutePath,
//            listOf(
//                "musicPlayerService.currentTrackIndex: ${musicPlayerService.currentTrackIndex}",
//                "playList.lastIndex: ${playList.lastIndex}",
//                "isOver: ${musicPlayerService.currentTrackIndex >= playList.lastIndex}"
//            ).joinToString("\n")
//        )

        if(playIndex > playList.lastIndex){
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.DESTROY_MUSIC_PLAYER.action
            )
            return
        }
        val uri = playList.getOrNull(playIndex)
        val isNotExistPath =
            !EnableUrlPrefix.isHttpPrefix(uri)
                    && !File(uri ?: String()).isFile
        if(
            uri.isNullOrEmpty()
            || isNotExistPath
        ){
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action
            )
            return
        }
        musicPlayerService.notiSetter?.setOnStop()
        val isStreamingUrl = judgeStreamingUri(
            playList,
            playIndex
        ) ?: return
        musicPlayerService.infoFileForMediaPlayer?.savePlayInfo(
            playIndex,
            fileListConBeforePlayMode,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "musicExecPlay.txt").absolutePath,
//            listOf(
//                "isStreamingUrl: ${isStreamingUrl}",
//                "playList: ${playList}",
//                "playIndex: ${playIndex}",
//            ).joinToString("\n")
//        )
        launchLoadingNoti(
            musicPlayerService,
            isStreamingUrl,
            playList,
            playIndex,
        )
        MusicPlayerMaker.releaseMediaPlayer(musicPlayerService)
        musicPlayerService.execPlayJob?.cancel()
        musicPlayerService.execPlayJob = when(isStreamingUrl){
            true -> playByStreamingUrLExtract(
                    musicPlayerService,
                    playList,
                    playIndex,
                )
            else ->
                playByNormal(
                    musicPlayerService,
                    playList,
                    playIndex,
                )
        }
    }

    private fun playByStreamingUrLExtract(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
        playIndex: Int,
    ): Job {
        val context = musicPlayerService.applicationContext
        return CoroutineScope(Dispatchers.IO).launch {
            val uri = withContext(Dispatchers.IO){
                playList.getOrNull(playIndex)
            } ?: return@launch
            val stUrlMapToPreloadJob = withContext(Dispatchers.IO) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "musicExecPlay_yt.txt").absolutePath,
//                    listOf(
//                        "uri: ${uri}",
//                        "playList: ${playList}",
//                        "playIndex: ${playIndex}",
//                    ).joinToString("\n")
//                )
                AudioStreamingMapExtractor.extractController(
                    context,
                    uri,
                    playList,
                    playIndex,
                    musicPlayerService.streamingPreloadFileMakeJob,
                )
            }
            musicPlayerService.streamingPreloadFileMakeJob = stUrlMapToPreloadJob.second
            val streamingMap = stUrlMapToPreloadJob.first
            val isEnd = endByMapCheck(streamingMap)
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "vAudio_url_map.txt").absolutePath,
//                listOf(
//                    "stUrl: ${streamingMap?.get(
//                        AudioStreamingMapExtractor.AudioStreamingKey.STREAMING_URL.key
//                    )}",
//                    "streamingMap: ${streamingMap}",
//                    "isEnd: ${isEnd}",
//                ).joinToString("\n") + "\n-----------\n"
//            )
            if(isEnd){
                LogSystems.stdWarn(
                    "extract failure, check connect env"
                )
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
                )
                return@launch
            }
            val uriTitle = streamingMap?.get(
                AudioStreamingMapExtractor.AudioStreamingKey.TITLE.key
            ) ?: String()
            withContext(Dispatchers.IO) {
                launchLoadingNoti(
                    musicPlayerService,
                    true,
                    playList,
                    playIndex,
                    uriTitle
                )
            }
            withContext(Dispatchers.IO) {
                val videoLength = streamingMap?.get(
                    AudioStreamingMapExtractor.AudioStreamingKey.DURATION.key
                )
                musicPlayerService.currentTrackLength =
                    when (videoLength == null) {
                        true -> 0
                        else -> miliSecToSec(
                            videoLength.toInt()
                        )
                    }
            }
            withContext(Dispatchers.IO) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "vAudio_url.txt").absolutePath,
//                    listOf(
//                        "stUrl: ${streamingUrlMap.get(
//                            AudioStreamingUrlExtractor.AudioStreamingKey.STREAMING_URL.key
//                        )}",
//                    ).joinToString("\n")
//                )
                streamingMap?.get(
                    AudioStreamingMapExtractor.AudioStreamingKey.STREAMING_URL.key
                )?.let {
                    execPlay(
                        musicPlayerService,
                        it,
                        playList,
                    )
                }
            }
        }
    }

    private fun endByMapCheck(
        streamingMap: Map<String, String>?
    ): Boolean {
        if(
            streamingMap.isNullOrEmpty()
        ) return true
        val audioStreamingKeyList =
            AudioStreamingMapExtractor.AudioStreamingKey.values()
        audioStreamingKeyList.forEach {
                audioStreamingKey ->
            when(audioStreamingKey){
                AudioStreamingMapExtractor.AudioStreamingKey.TITLE -> {
                    val uriTitle =
                        streamingMap.get(audioStreamingKey.key)
                    if (
                        uriTitle.isNullOrEmpty()
                        || EnableUrlPrefix.isHttpPrefix(uriTitle)
                    ) return true
                }
                AudioStreamingMapExtractor.AudioStreamingKey.STREAMING_URL -> {
                    val stUrl =
                        streamingMap.get(audioStreamingKey.key)
                    if (
                        stUrl.isNullOrEmpty()
                        || !EnableUrlPrefix.isHttpPrefix(stUrl)
                    ) return true
                }
                AudioStreamingMapExtractor.AudioStreamingKey.SRC_URL -> {
                    val srcUrl =
                        streamingMap.get(audioStreamingKey.key)
                    if (
                        srcUrl.isNullOrEmpty()
                        || !EnableUrlPrefix.isHttpPrefix(srcUrl)
                    ) return true
                }
                AudioStreamingMapExtractor.AudioStreamingKey.DURATION -> {
                    val duration =
                        streamingMap.get(audioStreamingKey.key)
                    if (
                        duration.isNullOrEmpty()
                    ) return true
                    try {
                        duration.toInt()
                    } catch (e: Exception) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun miliSecToSec(
        miliSec: Int,
    ): Int {
        val oneMiliSec = 1000
        return miliSec * oneMiliSec
    }

    private fun playByNormal(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
        playIndex: Int,
    ): Job {
        val context = musicPlayerService.applicationContext
        return CoroutineScope(Dispatchers.IO).launch {
            val uri = withContext(Dispatchers.IO) {
                playList.getOrNull(
                    playIndex
                )
            }
            if(
                uri.isNullOrEmpty()
            ){
                LogSystems.stdWarn(
                    "playList getIndex failure: ${playIndex} / ${playList.size}"
                )
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
                )
                return@launch
            }
            withContext(Dispatchers.IO) {
                try {
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "playByNormal_1.txt").absolutePath,
//                        listOf(
//                            "text",
//                            "playList: ${playList.joinToString("----")}",
//                            "uri: ${uri}"
//                        ).joinToString("\n")
//                    )
                    execPlay(
                        musicPlayerService,
                        uri,
                        playList
                    )
                } catch (e: Exception) {
                    LogSystems.stdWarn(e.toString())
                }
            }
        }
    }

    private fun execPlay(
        musicPlayerService: MusicPlayerService,
        uri: String,
        playList: List<String>,
    ){
//        if(
//            musicPlayerService.mediaPlayer == null
//        ) {
//            LogSystems.stdWarn("musicPlayer null")
//            return
//        }
        try {
            if(musicPlayerService.mediaPlayer != null) {
                MusicPlayerMaker.releaseMediaPlayer(musicPlayerService)
            }
            musicPlayerService.mediaPlayer = MusicPlayerMaker.make(
                musicPlayerService,
                playList
            )
            MusicPlayerMaker.setDatasource(
                musicPlayerService,
                uri
            )
//            }
        } catch (e: IOException) {
            LogSystems.stdWarn("$e")
            e.printStackTrace()
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "pexecPlay.txt").absolutePath,
//            listOf(
//                "uri: ${uri}",
//                "playList: ${playList.joinToString("----")}"
//            ).joinToString("\n")
//        )
        try {
            MusicPlayerMaker.prepare(musicPlayerService)
        } catch (e: IOException) {
            LogSystems.stdWarn("$e")
            e.printStackTrace()
        }
    }

    private fun judgeStreamingUri(
        playList: List<String>,
        playIndex: Int,
    ): Boolean? {
        val uri = playList
                .getOrNull(
                    playIndex
                ) ?: return null
        val httpPrefix = "http"
        val ytWord = "youtube"
        return uri.startsWith(httpPrefix)
                && uri.contains(ytWord)
                && uri.contains(queryStr)
    }

    private fun launchLoadingNoti(
        musicPlayerService: MusicPlayerService,
        isYtUrl: Boolean,
        playList: List<String>,
        playIndex: Int,
        uriTitleSrc: String = String(),
    ){
        musicPlayerService.madiaPlayerPosiUpdateJob?.cancel()
        val uri = playList.getOrNull(
            playIndex
        ) ?: return
        val uriTitle = uriTitleSrc.ifEmpty {
             when (
                    uri.startsWith("/")
                            && !isYtUrl
                ) {
                    true -> File(uri).name
                    else -> uri
                }
        }

        val header = PlayNotiLauncher.notiTitleCreator(
            "Loading..",
            uriTitle,
            playIndex,
            playList,
        )
        PlayNotiLauncher.launch(
            musicPlayerService,
            header,
            uriTitle,
        )
    }
}


private object AudioStreamingMapExtractor {

    private var execPreloadJobJobList: List<Deferred<Unit>> = listOf()
    private const val preloadNum = 30
    private const val pastLoadNum = 10
    private const val mapLineLimit = preloadNum + pastLoadNum

    private val mediaPlayerServiceStreamingPreloadTxtPath =
        UsePath.mediaPlayerServiceStreamingPreloadTxtPath
    fun exit(context: Context){
        execPreloadJobJobList.forEach {
            it.cancel()
        }
//        LinuxCmd.killCertainProcess(
//            context,
//            CcPathTool.trimAllExtend(UbuntuFiles.extractAudioStreamingUrlShellName)
//        )
        killExtractProcess(
            context,
            CcPathTool.trimAllExtend(UbuntuFiles.extractAudioStreamingMapShellName)
        )
    }

    private fun killExtractProcess(
        context: Context?,
        processName: String
    ){
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN.action,
            listOf(
                UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema to
                        processName
            )
        )
    }

    suspend fun extractController(
        context: Context,
        url: String,
        playList: List<String>,
        playIndex: Int,
        streamingPreloadFileMakeJob: Job?,
    ): Pair<Map<String, String>?, Job?>{
        val streamingMap = extractOneMap(
            context,
            url
        )
        val lastIndex = playList.lastIndex
        if(
            playIndex >= lastIndex
        ) return streamingMap to null
        return streamingMap to
                StPreloadFileMakeJob.handle(
                    context,
                    playList,
                    playIndex,
                    streamingPreloadFileMakeJob,
                )
    }

    private object StPreloadFileMakeJob {

        fun handle(
            context: Context,
            playList: List<String>,
            playIndex: Int,
            streamingPreloadFileMakeJob: Job?,
        ): Job? {
            return when(
                streamingPreloadFileMakeJob?.isActive == true
            ) {
                true -> streamingPreloadFileMakeJob
                else -> execute(
                    context,
                    playList,
                    playIndex,
                )
            }
        }
        private fun execute(
            context: Context,
            playList: List<String>,
            playIndex: Int,
        ): Job {
            return CoroutineScope(Dispatchers.IO).launch {
                val isNotPreload = withContext(Dispatchers.IO) {
                    !judgePreload(
                        playList,
                        playIndex,
                    )
                }
                if (
                    isNotPreload
                ) return@launch
                val preLoadUrlList = withContext(Dispatchers.IO) {
                    playList.filterIndexed { index, _ ->
                        playIndex <= index && index <= playIndex + preloadNum
                    }
                }
//                withContext(Dispatchers.IO){
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "preload_execute.txt").absolutePath,
//                        listOf(
//                            "isNotPreload: ${isNotPreload}",
//                            "preLoadUrlList: ${preLoadUrlList}",
//                        ).joinToString("\n")
//                    )
//                }
                val preloadStUrlMapList = withContext(Dispatchers.IO) {
                    extractByLoop(
                        context,
                        preLoadUrlList,
                        UsePath.mediaPlayerServiceStreamingPreloadShellDirPath,
                        UsePath.mediaPlayerServiceStreamingPreloadShellOutDirPath,
                    ).sortedBy {
                        it.first
                    }.map {
                        it.second
                    }
                }
                withContext(Dispatchers.IO) {
                    saveMapToPreloadFile(preloadStUrlMapList)
                }
            }
        }
    }

    private fun judgePreload(
        playList: List<String>,
        playIndex: Int,
    ): Boolean {
        val searchEndPlayIndex = playIndex + 5
        for (i in playIndex..searchEndPlayIndex) {
            val searchUrl = playList.getOrNull(i)
            if (
                searchUrl.isNullOrEmpty()
            ) {
                return false
            }
            val hitStUrlMapCon = findStUrlMapConFromPreloadFileByUrl(searchUrl)
            if (
                hitStUrlMapCon.isNullOrEmpty()
            ) return true
        }
       return false
    }

    private suspend fun extractOneMap(
        context: Context,
        url: String
    ): Map<String, String>? {
        val existStreamingMapCon =
            findStUrlMapConFromPreloadFileByUrl(url)
        if(
            !existStreamingMapCon.isNullOrEmpty()
        ) {
            val existStreamingMap = CmdClickMap.createMap(
                existStreamingMapCon,
                '\t'
            ).toMap()
            saveMapToPreloadFile(
                listOf(existStreamingMap)
            )
            return existStreamingMap
        }
        val newStreamingMap = extractByLoop(
            context,
            listOf(url),
            UsePath.mediaPlayerServiceStreamingShellDirPath,
            UsePath.mediaPlayerServiceStreamingShellOutDirPath
        ).firstOrNull()?.second
        if(
            newStreamingMap.isNullOrEmpty()
        ) return null
        saveMapToPreloadFile(
            listOf(newStreamingMap)
        )
        return newStreamingMap

    }


    private fun findStUrlMapConFromPreloadFileByUrl(
        url: String,
    ): String? {
        val srcUrlKey = AudioStreamingKey.SRC_URL.key
        return ReadText(mediaPlayerServiceStreamingPreloadTxtPath)
            .textToList()
            .firstOrNull {
                it.contains("${srcUrlKey}=${url}")
            }
    }

    private fun saveMapToPreloadFile(
        insertStUrlMapList: List<Map<String, String>,>
    ){
        val insertMapLineList = insertStUrlMapList.map{
            it.toSortedMap().map {
                "${it.key}=${it.value}"
            }.joinToString("\t")
        }
        val existPreloadMapFileCon =
            ReadText(mediaPlayerServiceStreamingPreloadTxtPath)
                .textToList()
                .filter { it.isNotEmpty() }
                .filter {
                    !insertMapLineList.contains(it)
                }
        val preloadStUrlMapListSrc =
            insertMapLineList +
                    existPreloadMapFileCon
        val preloadStUrlMapList =
            preloadStUrlMapListSrc
                .filter { it.isNotEmpty() }
                .take(mapLineLimit)
                .joinToString("\n")
        FileSystems.writeFile(
            mediaPlayerServiceStreamingPreloadTxtPath,
            preloadStUrlMapList
        )

    }

    private suspend fun extractByLoop(
        context: Context,
        urlList: List<String>,
        shellDirPath: String,
        shellOutDirPath: String,
    ): List<Pair<Int, Map<String, String>>> {
        val isNotLaunch = !UbuntuServiceManager.launchByNoCoroutine(
            context
        )
        if(isNotLaunch){
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeMusicPlayer.DESTROY_MUSIC_PLAYER.action,
            )
            return emptyList()
        }
        for(i in 1..100){
            delay(100)
            val isBasicProcess = LinuxCmd.isBasicProcess(context)
            if(isBasicProcess) break
        }
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        val loopTimes = urlList.size
        val stUrlMapChannel = Channel<Pair<Int, Map<String, String>>>(loopTimes)
        val receiveMapList = mutableListOf<Pair<Int, Map<String, String>>>()
        val ubuntuFiles = UbuntuFiles(context)
        val extractAudioStreamingMapShellPathObj =
            ubuntuFiles.extractAudioStreamingMapShell
//        withContext(Dispatchers.IO){
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "preload_extractByLoop00.txt").absolutePath,
//                listOf(
//                    "isNotLaunch: ${isNotLaunch}",
//                    "urlList: ${urlList}",
//                ).joinToString("\n")
//            )
//        }

        withContext(Dispatchers.IO){
            execPreloadJobJobList = urlList.mapIndexed { index, url ->
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "preload_extractByLoop.txt").absolutePath,
//                    listOf(
//                        "${LocalDateTime.now()}",
//                        "isNotLaunch: ${isNotLaunch}",
//                        "urlList: ${urlList}",
//                    ).joinToString("\n")
//                )
                async {
                    semaphore.withPermit {
                        execExtractByLoop(
                            context,
                            url,
                            index,
                            extractAudioStreamingMapShellPathObj,
                            shellDirPath,
                            shellOutDirPath,
                            stUrlMapChannel
                        )
                    }
                }
            }
            execPreloadJobJobList.forEach { it.await() }
            stUrlMapChannel.close()
//            var indexCount = 1
            for (rowNumToLine in stUrlMapChannel){
//                FileSystems.updateFile(
//                    File(
//                        UsePath.cmdclickDefaultAppDirPath,
//                        "prelaod_foreach_channel.txt"
//                    ).absolutePath,
//                    listOf(
//                        "${LocalDateTime.now()}",
//                        "indexCount: ${indexCount}",
//                        "rowNumToLine: ${rowNumToLine}",
//                    ).joinToString("\n\n")
//                )
//                indexCount++
                // Channelから受信
                receiveMapList.add(rowNumToLine)
            }
        }

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "prelaod.txt").absolutePath,
//            listOf("newStreamingUrlMap: ${receiveMapList}").joinToString("\n\n")
//        )
        return receiveMapList
    }

    private suspend fun execExtractByLoop(
        context: Context?,
        url: String,
        index: Int,
        extractAudioStreamingMapShellPathObj: File,
        shellDirPath: String,
        shellOutDirPath: String,
        stUrlMapChannel: Channel<Pair<Int, Map<String, String>>>
    ){
//        FileSystems.writeFileToDirByTimeStamp(
//            File(UsePath.cmdclickDefaultAppDirPath, "preload_extractByLoop").absolutePath,
//            listOf(
//                "isNotLaunch: ${isNotLaunch}",
//                "urlList: ${urlList}",
//            ).joinToString("\n")
//        )
        val existStreamingMap =
            withContext(Dispatchers.IO) {
                findStUrlMapConFromPreloadFileByUrl(url)?.let {
                    CmdClickMap.createMap(
                        it,
                        '\t'
                    ).toMap()
                }
            }
//        withContext(Dispatchers.IO) {
//            FileSystems.updateFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
////                                    "prelaod_foreach.txt"
//                ).absolutePath,
//                listOf(
//                    "index: ${index}",
//                    "url: ${url}",
//                    "existStreamingUrlMap: ${existStreamingUrlMap}",
////                    "urlList: ${urlList}"
//                ).joinToString("\n\n")
//            )
//        }
        if(
            !existStreamingMap.isNullOrEmpty()
        ) {
            stUrlMapChannel.send(
                Pair(index, existStreamingMap)
            )
            return
        }
        val extractAudioStreamingMapShellName =
            withContext(Dispatchers.IO) {
                CcPathTool.makeRndSuffixFilePath(
                    extractAudioStreamingMapShellPathObj.name
                )
            }
        val extractAudioStreamingMapShellPathInMusic =
            withContext(Dispatchers.IO) {
                File(
                    shellDirPath,
                    extractAudioStreamingMapShellName
                ).absolutePath
            }
        withContext(Dispatchers.IO) {
            FileSystems.createDirs(
                shellDirPath
            )
            FileSystems.copyFile(
                extractAudioStreamingMapShellPathObj.absolutePath,
                extractAudioStreamingMapShellPathInMusic
            )
        }
        val resFilePath = withContext(Dispatchers.IO) {
                CcPathTool.trimAllExtend(extractAudioStreamingMapShellName).let {
                    File(shellOutDirPath, "${it}Res.txt").absolutePath
                }
            }
        withContext(Dispatchers.IO) {
            FileSystems.createDirs(
                shellOutDirPath
            )
        }
        val res = withContext(Dispatchers.IO) {
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action,
                listOf(
                    UbuntuServerIntentExtra.backgroundShellPath.schema to
                            extractAudioStreamingMapShellPathInMusic,
                    UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema to
                            url,
                    UbuntuServerIntentExtra.backgroundResFilePath.schema to
                            resFilePath,
                )
            )
            ResAndProcess.wait(
                context,
                resFilePath,
                extractAudioStreamingMapShellPathInMusic,
            )
            ReadText(resFilePath).readText()
        }
//        withContext(Dispatchers.IO) {
//            BroadcastSender.normalSend(
//                context,
//                BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action,
//                listOf(
//                    UbuntuServerIntentExtra.foregroundShellPath.schema to
//                            extractAudioStreamingMapShellPathInMusic,
//                    UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema to
//                            url,
//                    UbuntuServerIntentExtra.foregroundTimeout.schema to
//                            "0",
//                    UbuntuServerIntentExtra.foregroundResFilePath.schema to
//                            resFilePath
//                )
//            )
//        }
//        withContext(Dispatchers.IO) {
//            ResAndProcess.wait(
//                context,
//                resFilePath,
//                extractAudioStreamingMapShellPathInMusic,
//            )
//        }
        val audioStreamingMap =
            withContext(Dispatchers.IO) {
                CmdClickMap.createMap(
                    res,
                    '\t'
                ).toMap().filterKeys { it.isNotEmpty() }
            }
        withContext(Dispatchers.IO) {
            stUrlMapChannel.send(
                Pair(index, audioStreamingMap)
            )
        }
    }

    enum class AudioStreamingKey (
        val key: String,
    ){
        SRC_URL("src_url"),
        DURATION("duration"),
        TITLE("title"),
        STREAMING_URL("streaming_url"),
    }

}