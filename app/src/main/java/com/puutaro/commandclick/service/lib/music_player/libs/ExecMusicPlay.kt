package com.puutaro.commandclick.service.lib.music_player.libs

import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeMusicPlayer
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.service.lib.music_player.MusicPlayerMaker
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


private val queryStr = "?v="

object ExecMusicPlay {

    fun playHandler(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
        playIndex: Int,
        fileListConBeforePlayMode: String,
    ){
        musicPlayerService.notiSetter?.setOnStop()
        val isYtUrl = judgeYtUri(
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
//                "isYtUrl: ${isYtUrl}",
//                "playList: ${playList}",
//                "playIndex: ${playIndex}",
//            ).joinToString("\n")
//        )
        launchLoadingNoti(
            musicPlayerService,
            isYtUrl,
            playList,
            playIndex,
        )
        musicPlayerService.execPlayJob?.cancel()
        musicPlayerService.execPlayJob = when(isYtUrl){
            true -> playByYtExtract(
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

    private fun playByYtExtract(
        musicPlayerService: MusicPlayerService,
        playList: List<String>,
        playIndex: Int,
    ): Job {
        val context = musicPlayerService.applicationContext
        return CoroutineScope(Dispatchers.IO).launch {
            val uri = withContext(Dispatchers.IO){
                playList.getOrNull(playIndex)
            } ?: return@launch
            val yt = withContext(Dispatchers.IO) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "musicExecPlay_yt.txt").absolutePath,
//                    listOf(
//                        "uri: ${uri}",
//                        "playList: ${playList}",
//                        "playIndex: ${playIndex}",
//                    ).joinToString("\n")
//                )
                YtExtractorMaker.make(
                    musicPlayerService,
                    uri,
                )
            }
            if(
                yt == null
                || yt.state != State.SUCCESS
            ) {
                LogSystems.stdWarn(
                    "extract failure"
                )
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeMusicPlayer.NEXT_MUSIC_PLAYER.action,
                )
                return@launch
            }
            val videoMeta = withContext(Dispatchers.IO) {
                yt.getVideoMeta()
            }
            val ytFiles = withContext(Dispatchers.IO) {
                yt.getYTFiles()
            } ?: return@launch
            val ytFile = withContext(Dispatchers.IO) {
                ytFiles.get(251)
            }
            val uriTitle = videoMeta?.title ?: String()
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
                val videoLength = videoMeta?.videoLength
                musicPlayerService.currentTrackLength =
                    when (videoLength == null) {
                        true -> 0
                        else -> miliSecToSec(
                            videoLength.toInt()
                        )
                    }
            }
            withContext(Dispatchers.IO) {
                ytFile.url?.let {
                    execPlay(
                        musicPlayerService,
                        it,
                        playList,
                    )
                }
            }
        }
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
                return@launch
            }
            withContext(Dispatchers.IO) {
                try {
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

    private suspend fun execPlay(
        musicPlayerService: MusicPlayerService,
        uri: String,
        playList: List<String>,
    ){
        if(
            musicPlayerService.mediaPlayer == null
        ) {
            LogSystems.stdWarn("musicPlayer null")
            return
        }
//        val url = "http://137.110.92.231/~chenyu/BBC.mp4"
//        https://www.youtube.com/watch?v=5c1F8FzPfz8
//            .setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "musicPlay.txt").absolutePath,
//                listOf(
//                    "uri: ${uri}",
//                    "uriTitle: ${uriTitle}"
//                ).joinToString("\n")
//            )
            withContext(Dispatchers.IO) {
                MusicPlayerMaker.releaseMediaPlayer(musicPlayerService)
                musicPlayerService.mediaPlayer = MusicPlayerMaker.make(
                    musicPlayerService,
                    playList
                )
                MusicPlayerMaker.setDatasource(
                    musicPlayerService,
                    uri
                )
            }
        } catch (e: IOException) {
            LogSystems.stdWarn("$e")
            e.printStackTrace()
        }
        withContext(Dispatchers.IO) {
            try {
                MusicPlayerMaker.prepare(musicPlayerService)
            } catch (e: IOException) {
                LogSystems.stdWarn("$e")
                e.printStackTrace()
            }
        }
    }

    private fun judgeYtUri(
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


private object YtExtractorMaker {

    suspend fun make(
        musicPlayerService: MusicPlayerService,
        url: String,
    ): YTExtractor? {
        val context = musicPlayerService.applicationContext
        if(context == null) return null
//        musicPlayerService.currentTrackIndex =
//            playList.indexOf(url)
        //If your YouTube link is "https://www.youtube.com/watch?v=IDwytT0wFRM" so this videoId is "IDwytT0wFRM"
        val videoId =
            extractYtVideoId(url)
                ?: return null
        val yt = YTExtractor(
            con = context,
            CACHING = true,
            LOGGING = true,
            retryCount = 3
        )
// CACHING and LOGGING are 2 optional params. LOGGING is for showing Log and CACHING is for saving SignatureCipher to optimize extracting time (not recommend CACHING to extract multiple videos because it causes HTTP 403 Error)
// retryCount is for retrying when extract fail (default is 1)
        yt.extract(videoId)
        return yt
        //Get stream URL
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "music.txt").absolutePath,
//                listOf(
//                    "title: ${videoMeta?.title}",
//                    "videoId: ${videoId}",
//                    "videoLength: ${videoMeta?.videoLength}",
//                    "viewCount: ${videoMeta?.author}",
//                    "channelId: ${videoMeta?.channelId}",
//                ).joinToString("\n")
//            )
    }

    private fun extractYtVideoId(
        url: String,
    ): String? {
        return url.split(queryStr)
            .lastOrNull()
            ?.split("&")
            ?.firstOrNull()
    }
}