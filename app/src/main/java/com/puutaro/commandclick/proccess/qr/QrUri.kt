package com.puutaro.commandclick.proccess.qr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.intent.extra.FileDownloadExtra
import com.puutaro.commandclick.common.variable.intent.extra.GitDownloadExtra
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.proccess.ubuntu.UbuntuController
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.service.GitDownloadService
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object QrUri {

    private val jsDescSeparator = QrSeparator.sepalator.str
    private val keyMissingErrStr = "Must specify %s"

    fun handler(
        fragment: Fragment,
        currentAppDirPath: String,
        loadConSrc: String,
        isMoveCurrentDir: String? = null
    ) {
        when (true) {
            loadConSrc.startsWith(QrLaunchType.Http.prefix),
            loadConSrc.startsWith(QrLaunchType.Https.prefix),
            loadConSrc.startsWith(QrLaunchType.JsDesc.prefix),
            loadConSrc.startsWith(QrLaunchType.Javascript.prefix),
            -> load(
                fragment,
                currentAppDirPath,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.CpFile.prefix),
            -> execCpFile(
                fragment,
                currentAppDirPath,
                loadConSrc,
                isMoveCurrentDir
            )
            loadConSrc.startsWith(QrLaunchType.ScpDir.prefix),
            -> execScpDir(
                fragment,
                loadConSrc,
            )
            loadConSrc.startsWith(QrLaunchType.WIFI.prefix),
            loadConSrc.startsWith(QrLaunchType.WIFI.prefix.uppercase())
            -> setWifi(
                fragment,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.SMS.prefix),
            loadConSrc.startsWith(QrLaunchType.SMS.prefix.uppercase()),
            -> sendSms(
                fragment,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.MAIL.prefix),
            loadConSrc.startsWith(QrLaunchType.MAIL.prefix.uppercase()),
            loadConSrc.startsWith(QrLaunchType.MAIL2.prefix),
            loadConSrc.startsWith(QrLaunchType.MAIL2.prefix.uppercase()),
            -> sendMail(
                fragment,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.TEL.prefix),
            loadConSrc.startsWith(QrLaunchType.TEL.prefix.uppercase()),
            -> execTel(
                fragment,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.G_CALENDAR.prefix),
            loadConSrc.startsWith(QrLaunchType.G_CALENDAR.prefix.uppercase()),
            -> sendGCalendarIntent(
                fragment,
                loadConSrc
            )
            loadConSrc.startsWith(QrLaunchType.ON_GIT.prefix)
            -> execOnGit(
                fragment,
                currentAppDirPath,
                loadConSrc,
            )
            else
            -> execCopy(
                fragment,
                loadConSrc,
            )
        }
    }

    private fun execCopy(
        fragment: Fragment,
        copyString: String,
    ){
        JsUtil(fragment).copyToClipboard(copyString, 10)
        Toast.makeText(
            fragment.context,
            "Copy ok",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun execCpFile(
        fragment: Fragment,
        currentAppDirPath: String,
        cpQrString: String,
        isMoveCurrentDirSrc: String? = null
    ){
        val fileDownloadService = FileDownloadService::class.java
        val context = fragment.context
        val cpFileMap = QrMapper.convertScanConToMap(cpQrString)
        val mainUrlSrc = getRequireKey(
            context,
            cpFileMap,
            CpFileKey.ADDRESS.key,
        ) ?: return
        val mainUrl = QrMapper.makeMainUrl(mainUrlSrc)
        val filePath = getRequireKey(
            context,
            cpFileMap,
            CpFileKey.PATH.key,
        ) ?: return
        val parentDirPath =
            cpFileMap.get(CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key)
        val isMoveCurrentDir =
            cpFileMap.get(CpFileKey.IS_MOVE_CURRENT_DIR.key)
                ?: isMoveCurrentDirSrc
        val intent = Intent(
            context,
            fileDownloadService
        )
        intent.putExtra(
            FileDownloadExtra.MAIN_URL.schema,
            mainUrl
        )
        intent.putExtra(
            FileDownloadExtra.FULL_PATH_OR_FANNEL_RAW_NAME.schema,
            filePath
        )
        intent.putExtra(
            FileDownloadExtra.CURRENT_APP_DIR_PATH_FOR_DOWNLOAD.schema,
            currentAppDirPath
        )
        isMoveCurrentDir?.let {
            intent.putExtra(
                FileDownloadExtra.IS_MOVE_TO_CURRENT_DIR.schema,
                it
            )
        }
        parentDirPath?.let {
            intent.putExtra(
                FileDownloadExtra.CURRENT_APP_DIR_PATH_FOR_UPLOADER.schema,
                it
            )
        }
        try {
            context?.let {
                ContextCompat.startForegroundService(context, intent)
            }
        }catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }

    private fun execScpDir(
        fragment: Fragment,
        cpQrString: String,
    ) {
        val context = fragment.context
            ?: return
        val scpDirMap = QrMapper.convertScanConToMap(cpQrString)
        val dirPath = getRequireKey(
            context,
            scpDirMap,
            ScpDirKey.DIR_PATH.key,
        ) ?: return
        val ipv4Address = getRequireKey(
            context,
            scpDirMap,
            ScpDirKey.IPV4AD.key,
        ) ?: return
        val port = getRequireKey(
            context,
            scpDirMap,
            ScpDirKey.PORT.key,
        ) ?: return
        val userName = getRequireKey(
            context,
            scpDirMap,
            ScpDirKey.USER_NAME.key,
        ) ?: return
        val password = getRequireKey(
            context,
            scpDirMap,
            ScpDirKey.PASSWORD.key,
        ) ?: return
        val ubuntuFiles = UbuntuFiles(context)
        val argsCon = listOf(
            "RSYNC_DIR_PATH=${dirPath}",
            "IP_V4_ADDRESS=${ipv4Address}",
            "PORT=${port}",
            "USER_NAME=${userName}",
            "PASSWORD=${password}",
        ).joinToString(",")
        val isUbuntuProc = LinuxCmd.isBasicProcess()
        when(isUbuntuProc){
            true
            ->
                UbuntuController.execScriptByBackground(
                    fragment,
                    ubuntuFiles.rsyncDownloaderShellPath.absolutePath,
                    argsCon,
                    2,
                )
            else
            -> startRsyncUbuntuService(
                fragment,
                ubuntuFiles,
                argsCon,
            )
        }
    }

    private fun startRsyncUbuntuService(
        fragment: Fragment,
        ubuntuFiles: UbuntuFiles,
        argsCon: String,
    ){
        val context = fragment.context
            ?: return
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                UbuntuController.bootWait(fragment)
            }
            try {
                val ubuntuService = UbuntuService::class.java
                val intent = Intent(
                    context,
                    ubuntuService
                )
                intent.putExtra(
                    UbuntuServerIntentExtra.backgroundShellPath.schema,
                    ubuntuFiles.rsyncDownloaderShellPath.absolutePath
                )
                intent.putExtra(
                    UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
                    argsCon
                )
                context.let {
                    ContextCompat.startForegroundService(context, intent)
                }
            }catch (e: Exception){
                LogSystems.stdErr(e.toString())
            }
        }
    }


    private fun execOnGit(
        fragment: Fragment,
        currentAppDirPath: String,
        onGitCon: String
    ){
        val gitDownloadService = GitDownloadService::class.java
        val context = fragment.context
        val onGitMap = QrMapper.convertScanConToMap(onGitCon)
        if(
            onGitMap.isEmpty()
        ) return
        val prefix = getRequireKey(
            context,
            onGitMap,
            OnGitKey.PREFIX.key,
        ) ?: return
        val fannelListPath = onGitMap.get(OnGitKey.LIST_PATH.key)
        val parentDirRelativePathPATH = onGitMap.get(OnGitKey.DIR_PATH.key)
        val fannelRawName = getRequireKey(
            context,
            onGitMap,
            OnGitKey.NAME.key,
        ) ?: return
        val intent = Intent(
            context,
            gitDownloadService
        )
        intent.putExtra(
            GitDownloadExtra.PREFIX.schema,
            prefix
        )
        intent.putExtra(
            GitDownloadExtra.FANNEL_RAW_NAME.schema,
            fannelRawName
        )
        intent.putExtra(
            GitDownloadExtra.CURRENT_APP_DIR_PATH_FOR_TRANSFER.schema,
            currentAppDirPath
        )
        intent.putExtra(
            GitDownloadExtra.FANNEL_LIST_PATH.schema,
            fannelListPath
        )
        intent.putExtra(
            GitDownloadExtra.PARENT_DIR_PATH_FOR_FILE_UPLOAD.schema,
            parentDirRelativePathPATH
        )
        try {
            context?.let {
                ContextCompat.startForegroundService(context, intent)
            }
        }catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }

    private fun execTel(
        fragment: Fragment,
        telString: String,
    ){
        val context = fragment.context
            ?: return
        try {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse(telString)
            )
            context.startActivity(intent)
        }catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }

    private fun sendGCalendarIntent(
        fragment: Fragment,
        gCalendarStr: String,
    ){
        val context = fragment.context
            ?: return
        val intent = Intent()
        intent.data = CalendarContract.Calendars.CONTENT_URI
        intent.action = Intent.ACTION_INSERT
        val gCalendarMap = QrMapper.convertScanConToMap(
            gCalendarStr
        )
        val jsUtil = JsUtil(fragment)
        gCalendarMap.get(GCalendarKey.TITLE.key)
            ?: return
        putExtraStr(
            intent,
            gCalendarMap,
            GCalendarKey.TITLE.key,
        )
        putExtraStr(
            intent,
            gCalendarMap,
            GCalendarKey.DESCRIPTION.key,
        )
        putExtraStr(
            intent,
            gCalendarMap,
            GCalendarKey.EVENT_LOCATION.key,
        )
        putExtraStr(
            intent,
            gCalendarMap,
            GCalendarKey.EMAIL.key,
        )
        putExtraDate(
            intent,
            gCalendarMap,
            GCalendarKey.BIGIN_TIME.key,
            jsUtil,
        )
        putExtraDate(
            intent,
            gCalendarMap,
            GCalendarKey.END_TIME.key,
            jsUtil,
        )
        context.startActivity(intent)
    }

    private fun putExtraStr(
        intent: Intent,
        map: Map<String, String?>?,
        key: String,
    ){
        if(
            map.isNullOrEmpty()
        ) return
        map.get(key)?.let {
            intent.putExtra(
                key,
                it
            )
        }
    }

    private fun putExtraDate(
        intent: Intent,
        map: Map<String, String?>?,
        key: String,
        jsUtil: JsUtil,
    ){
        map?.get(key)?.let {
            val miliTime =  jsUtil.convertDateTimeToMiliTime(it)
            intent.putExtra(
                key,
                miliTime
            )
        }
    }

    private fun sendMail(
        fragment: Fragment,
        loadConSrc: String,
    ){
        val context = fragment.context
            ?: return
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        val gmailMap = QrMapper.makeGmailMap(loadConSrc)
        val mailAd = gmailMap?.get(
            GmailKey.MAIL_AD.key
        ) ?: return
        intent.putExtra(Intent.EXTRA_EMAIL, mailAd)
        gmailMap.get(GmailKey.SUBJECT.key)?.let {
            intent.putExtra(Intent.EXTRA_SUBJECT, it)
        }
        gmailMap.get(GmailKey.BODY.key)?.let {
            intent.putExtra(Intent.EXTRA_TEXT, it)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }

    private fun getRequireKey(
        context: Context?,
        scanConMap: Map<String, String?>,
        keyName: String,
    ): String? {
        val targetValue = scanConMap.get(keyName)
        if(
            targetValue.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                keyMissingErrStr.format(keyName),
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return targetValue
    }

    private fun sendSms(
        fragment: Fragment,
        loadConSrc: String
    ){
        val context = fragment.context ?: return
        val numBodyPair = QrMapper.getSmsNumAndBody(loadConSrc)
        val number =  numBodyPair.first ?: return
        val body = numBodyPair.second
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        body?.let {
            intent.putExtra("sms_body", body)
        }
        try {
            context.startActivity(intent)
        }catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }


    private fun setWifi(
        fragment: Fragment,
        loadConSrc: String
    ){
        val context = fragment.context ?: return
        val ssidPinPair = QrMapper.getWifiWpaSsidAndPinPair(loadConSrc)
        val ssid = ssidPinPair.first ?: return
        val pin = ssidPinPair.second?: return
        val listener = context as? CommandIndexFragment.OnConnectWifiListenerForCmdIndex
        listener?.onConnectWifiForCmdIndex(
            ssid,
            pin
        )
    }

    fun load(
        fragment: Fragment,
        currentAppDirPath: String,
        loadConSrc: String
    ) {
        val jsDesc = QrLaunchType.JsDesc.prefix
        val replaceLoadUrlSrc =
            ScriptPreWordReplacer.replaceForQr(
                loadConSrc,
                currentAppDirPath
            )
        val loadUrl =
            if (
                replaceLoadUrlSrc.trim().startsWith(jsDesc)
            ) replaceLoadUrlSrc.split(jsDescSeparator).filterIndexed { index, _ ->
                index > 0
            }.joinToString(jsDescSeparator)
            else replaceLoadUrlSrc
        BroadCastIntent.sendUrlCon(
            fragment,
            loadUrl.trim()
        )
    }
}
