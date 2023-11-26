package com.puutaro.commandclick.proccess.qr

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.intent.extra.FileDownloadExtra
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.service.FileDownloadService
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.GmailKey
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer


object QrUri {

    private val jsDescSeparator = QrSeparator.sepalator.str

    fun handler(
        fragment: Fragment,
        currentAppDirPath: String,
        loadConSrc: String
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
                loadConSrc
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

            else
            -> execCopy(
                fragment,
                loadConSrc
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
    ){
        val fileDownloadService = FileDownloadService::class.java
        val context = fragment.context
        val urlAndPathPair = NetworkTool.extractCopyPath(cpQrString) ?: return
        val mainUrl = urlAndPathPair.first
        val filePath = urlAndPathPair.second
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
            FileDownloadExtra.CURRENT_APP_DIR_PATH_FOR_TRANSFER.schema,
            currentAppDirPath
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
        val listener = context as CommandIndexFragment.OnCallListenerForCmdIndex
        listener.onCallWifiForCmdIndex(
            telString
        )
    }

    private fun sendMail(
        fragment: Fragment,
        loadConSrc: String,
    ){
        val context = fragment.context
            ?: return
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        val gmailMap = NetworkTool.makeGmailMap(loadConSrc)
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

    private fun sendSms(
        fragment: Fragment,
        loadConSrc: String
    ){
        val context = fragment.context ?: return
        val numBodyPair = NetworkTool.getSmsNumAndBody(loadConSrc)
        val number =  numBodyPair.first ?: return
        val body = numBodyPair.second
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        body?.let {
            intent.putExtra("sms_body", body)
        }
        try {
            context.startActivity(intent);
        }catch (e: Exception){
            LogSystems.stdErr(e.toString())
        }
    }


    private fun setWifi(
        fragment: Fragment,
        loadConSrc: String
    ){
        val context = fragment.context ?: return
        val ssidPinPair = NetworkTool.getWifiWpaSsidAndPinPair(loadConSrc)
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
