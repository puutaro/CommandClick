package com.puutaro.commandclick.proccess.qr

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.UbuntuInfo
import com.puutaro.commandclick.service.FileUploadService
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object QrDialogMethod {

    private val fileUploadService = FileUploadService::class.java
    private var imageDialogObj: Dialog? = null

    fun launchPassDialog(
        fragment: Fragment,
//        currentAppDirPath: String,
        fannelName: String,
    ) {
        val context = fragment.context
            ?: return

        val cpQrStr = makeCpFileQrStr(
            fragment,
//            currentAppDirPath,
            fannelName,
        )

        val passQrLogoDrawable = QrLogo.createMonochrome(
            fragment,
            cpQrStr
        ) ?: return
        val intent = Intent(
            context,
            fileUploadService
        )
//        intent.putExtra(
//            FileUploadExtra.CURRENT_APP_DIR_PATH_FOR_FILE_UPLOAD.schema,
//            currentAppDirPath
//        )
        context.let {
            ContextCompat.startForegroundService(context, intent)
        }
        imageDialogObj = Dialog(
            context
        )
        imageDialogObj?.setContentView(
            R.layout.image_dialog_layout
        )
        val titleTextView = imageDialogObj?.findViewById<AppCompatTextView>(
            R.id.image_dialog_title
        )
        titleTextView?.text = makePassDialogTitle(
//            currentAppDirPath,
            fannelName,
        )
        val imageContentsView = imageDialogObj?.findViewById<AppCompatImageView>(
            R.id.image_dialog_image
        )
        val qrBitMap =
            QrLogo.toBitMapWrapper(passQrLogoDrawable)
                ?: return
        imageContentsView
            ?.setImageBitmap(qrBitMap)
        setShareButton(
            fragment,
            qrBitMap
        )
        imageDialogObj?.findViewById<AppCompatImageButton>(
            R.id.image_dialog_cancel
        )?.isVisible = false
        val cancelButton = imageDialogObj?.findViewById<AppCompatImageButton>(
            R.id.image_dialog_ok
        )
        cancelButton?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                imageDialogObj?.dismiss()
                imageDialogObj = null
            }
        }
        imageDialogObj?.setOnCancelListener {
            CoroutineScope(Dispatchers.Main).launch {
                imageDialogObj?.dismiss()
                imageDialogObj = null
            }
        }
//        imageDialogObj?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
        imageDialogObj?.window?.setGravity(Gravity.BOTTOM)
        imageDialogObj?.show()
    }

    private fun makePassDialogTitle(
//        currentAppDirPath: String,
        fannelName: String,
    ): String {
//        val isAppDirAdmin = UsePath.cmdclickDefaultAppDirPath.removeSuffix("/") ==
//                UsePath.cmdclickAppDirAdminPath
//        return when(isAppDirAdmin){
//            true
//            -> "Pass AppDir: ${CcPathTool.makeFannelRawName(fannelName)}"
//            else
//            -> "Pass: ${fannelName}"
//        }
        return "Pass: ${SystemFannel.convertDisplayNameToFannelName(fannelName)}"
    }

    private fun makeCpFileQrStr(
        fragment: Fragment,
//        currentAppDirPath: String,
        fannelName: String,
    ): String {
        val context = fragment.context
        val ipV4Address = NetworkTool.getIpv4Address(context)
        val fannelRawName = CcPathTool.makeFannelRawName(fannelName)
        val cmdclickDefaultAppDirPath =  UsePath.cmdclickDefaultAppDirPath
        val isAppDirAdmin = fannelName == SystemFannel.home
//            cmdclickDefaultAppDirPath.removeSuffix("/") ==
//                UsePath.cmdclickAppDirAdminPath
        return when(isAppDirAdmin){
            true -> {
                val appDirPathForCpFile = UsePath.cmdclickDefaultAppDirPath
                QrLaunchType.CpFile.prefix + listOf(
                    "${CpFileKey.ADDRESS.key}=${ipV4Address}:${UsePort.COPY_FANNEL_PORT.num}",
                    "${CpFileKey.PATH.key}=$appDirPathForCpFile",
//                    "${CpFileKey.CURRENT_APP_DIR_PATH_FOR_SERVER.key}=${appDirPathForCpFile}"
                ).joinToString(";")
            }
            else -> {
                makeCpFileQrNormal(
                    fragment,
                    "${cmdclickDefaultAppDirPath}/$fannelRawName",
                )
            }
        }
    }

    fun makeCpFileQrNormal(
        fragment: Fragment,
        path: String,
    ): String {
        val context = fragment.context
        val ipV4Address = NetworkTool.getIpv4Address(context)
        return QrLaunchType.CpFile.prefix + listOf(
            "${CpFileKey.ADDRESS.key}=${ipV4Address}:${UsePort.COPY_FANNEL_PORT.num}",
            "${CpFileKey.PATH.key}=$path",
        ).joinToString(";")
    }

    fun makeScpDirQrStr(
        fragment: Fragment,
        dirPath: String,
    ): String {
        val context = fragment.context
        val ipV4Address = NetworkTool.getIpv4Address(context)
        return QrLaunchType.ScpDir.prefix + listOf(
            "${ScpDirKey.IPV4AD.key}=${ipV4Address}",
            "${ScpDirKey.PORT.key}=${UsePort.DROPBEAR_SSH_PORT.num}",
            "${ScpDirKey.DIR_PATH.key}=$dirPath",
            "${ScpDirKey.USER_NAME.key}=${UbuntuInfo.user}",
            "${ScpDirKey.PASSWORD.key}=${UbuntuInfo.user}",
        ).joinToString(";")
    }

    private fun setShareButton(
        fragment: Fragment,
        myBitmap: Bitmap,
    ){
        val shareButton = imageDialogObj?.findViewById<AppCompatImageButton>(
            R.id.image_dialog_share
        )
        shareButton?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                execShare(
                    fragment,
                    myBitmap
                )
            }
        }
    }


    fun execShare(
        fragment: Fragment,
        myBitmap: Bitmap
    ){
        val context = fragment.context
        FileSystems.removeDir(
            UsePath.cmdclickTempCreateDirPath
        )
        FileSystems.createDirs(
            UsePath.cmdclickTempCreateDirPath
        )
        val imageName = BitmapTool.hash(
            myBitmap
        ) + ".png"
        val file = File(
            UsePath.cmdclickTempCreateDirPath,
            imageName
        )
        FileOutputStream(file).use { stream ->
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        CoroutineScope(Dispatchers.Main).launch {
            IntentVariant.sharePngImage(
                file,
                context,
            )
        }
    }


    fun execChange(
        fragment: Fragment,
//        currentAppDirPath: String,
        fannelName: String,
        dialogObj: Dialog?,
        replace_qr_logo_int: Int,
        isFileCon: Boolean = false
    ){

        val context = fragment.context ?: return
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}"
        val qrLogoPath = "${fannelDirPath}/${UsePath.qrPngRelativePath}"
        val qrLogoPathObj = File(qrLogoPath)
        val qrLogoParentDirPath = qrLogoPathObj.parent
            ?: return
        val qrLogoName = qrLogoPathObj.name
        CoroutineScope(Dispatchers.IO).launch {
            val previousChecksum = withContext(Dispatchers.IO){
                FileSystems.checkSum(
                    File(
                        qrLogoParentDirPath,
                        qrLogoName
                    ).absolutePath
                )
            }
            withContext(Dispatchers.IO) {
                QrLogo.createAndSaveWithGitCloneOrFileCon(
                    context,
//                    currentAppDirPath,
                    fannelName,
                    isFileCon,
                )
            }
            withContext(Dispatchers.IO){
                for(i in 1..20){
                    val isBreak = isForBreakWithCheckSum(
                        previousChecksum,
                        qrLogoParentDirPath,
                        qrLogoName
                    )
                    if(isBreak) break
                    withContext(Dispatchers.Main) toast@ {
                        if(i != 1) return@toast
                        ToastUtils.showShort("Change..")
                    }
                    delay(100)
                }
            }
            withContext(Dispatchers.Main) {
                dialogObj?.findViewById<AppCompatImageView>(
                    replace_qr_logo_int
                )?.load(qrLogoPath)
                when(fragment) {
                    is CommandIndexFragment -> {
                        val indexfannelListUpdateIntent = Intent()
                        indexfannelListUpdateIntent.action =
                            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                        context.sendBroadcast(indexfannelListUpdateIntent)
                    }
                    is EditFragment -> {
                        ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                            fragment,
                            ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                                fragment,
                                ListIndexAdapter.indexListMap,
//                                ListIndexAdapter.listIndexTypeKey
                            )
                        )
                    }
                }
            }
        }
    }

    fun execConUpdate(
        fragment: Fragment,
//        currentAppDirPath: String,
        fannelName: String,
        dialogObj: Dialog?,
        replace_qr_logo_int: Int,
    ){
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}"
        val qrDesignFilePath = "${fannelDirPath}/${UsePath.qrDesignRelativePath}"
        val qrDesignMap = QrLogo.readQrDesignMapWithCreate(
            qrDesignFilePath,
//            currentAppDirPath,
            fannelName,
        )
        val qrLogoPath = "${fannelDirPath}/${UsePath.qrPngRelativePath}"
        val qrLogoPathObj = File(qrLogoPath)
        val qrLogoParentDirPath = qrLogoPathObj.parent
            ?: return
        val qrLogoName = qrLogoPathObj.name
        CoroutineScope(Dispatchers.IO).launch {
            val previousChecksum = withContext(Dispatchers.IO){
                FileSystems.checkSum(qrLogoPath)
            }
            withContext(Dispatchers.IO) {
                QrLogo.createAndSaveFromDesignMap(
                    fragment.context,
                    qrDesignMap,
//                    currentAppDirPath,
                    fannelName,
                )
            }
            withContext(Dispatchers.IO){
                for(i in 1..20){
                    val isBreak = isForBreakWithCheckSum(
                        previousChecksum,
                        qrLogoParentDirPath,
                        qrLogoName
                    )
                    if(isBreak) break
                    withContext(Dispatchers.Main) toast@ {
                        if(i != 1) return@toast
                        ToastUtils.showShort("Contents Change..")
                    }
                    delay(100)
                }
            }
            val updateChecksum = FileSystems.checkSum(
                qrLogoPath
            )
            if(
                updateChecksum == previousChecksum
            ) return@launch
            withContext(Dispatchers.Main) {
                dialogObj?.findViewById<AppCompatImageView>(
                    replace_qr_logo_int
                )?.load(qrLogoPath)
                when(fragment) {
//                    is CommandIndexFragment -> {
//                        val indexfannelListUpdateIntent = Intent()
//                        indexfannelListUpdateIntent.action =
//                            BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
//                        context.sendBroadcast(indexfannelListUpdateIntent)
//                    }
                    is EditFragment -> {
                        ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                            fragment,
                            ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                                fragment,
                                ListIndexAdapter.indexListMap,
//                                ListIndexAdapter.listIndexTypeKey
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun isForBreakWithCheckSum(
        previousChecksum: String,
        qrLogoParentDirPath: String,
        qrLogoName: String
    ): Boolean {
        val qrLogoPath = File(
            qrLogoParentDirPath,
            qrLogoName
        ).absolutePath
        val updateChecksum = FileSystems.checkSum(qrLogoPath)
        return when(
            updateChecksum != previousChecksum
        ) {
            true -> {
                delay(100)
                val confirmUpdateChecksum = FileSystems.checkSum(qrLogoPath)
                if(
                    confirmUpdateChecksum == updateChecksum
                ) return true
                false
            }
            else -> false
        }
    }
}
