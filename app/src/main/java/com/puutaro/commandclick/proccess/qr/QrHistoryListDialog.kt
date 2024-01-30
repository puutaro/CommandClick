package com.puutaro.commandclick.proccess.qr

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.util.file.ReadText


class QrHistoryListDialog(
    private val fragment: Fragment,
    private val codeScanner: CodeScanner,
    private val qrScanDialogObj: Dialog?,
    private val currentAppDirPath: String,
) {
    private val context = fragment.context
    private val qrLogo = R.drawable.icons_qr_code
    private var subMenuDialog: Dialog? = null


    fun launch(){
        if(
            context == null
        ) return
        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            qrScanDialogObj,
            currentAppDirPath,
        )
        setCancelListener()
        subMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        subMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        subMenuDialog?.show()
    }

    private fun setCancelListener(){
        val cancelImageButton =
            subMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            codeScanner.startPreview()
            subMenuDialog?.dismiss()
        }
        subMenuDialog?.setOnCancelListener {
            codeScanner.startPreview()
            subMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        qrScanDialogObj: Dialog?,
        currentAppDirPath: String
    ) {
        if(
            context == null
        ) return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = makeQrTitleList(
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            subMenuListView,
            qrScanDialogObj,
            currentAppDirPath,
        )
    }

    private fun subMenuItemClickListener(
        subMenuListView: ListView?,
        qrScanDialogObj: Dialog?,
        currentAppDirPath: String,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            qrScanDialogObj?.dismiss()
            codeScanner.releaseResources()
            subMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedQrTitle = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val selectedQrTitleUriLine = makeQrHistoryList().filter {
                val titleUriList = it.split("\t")
                val title = titleUriList.firstOrNull() ?: String()
                title == selectedQrTitle
            }.firstOrNull() ?: return@setOnItemClickListener
            val selectedTitleQrList = selectedQrTitleUriLine.split("\t")
            val selectedQrUri = selectedTitleQrList.filterIndexed{
                index, s -> index > 0
            }.joinToString()
            QrUriHandler.handle(
                fragment,
                currentAppDirPath,
                selectedQrUri
            )
            QrHistoryManager.registerQrUriToHistory(
                currentAppDirPath,
                selectedQrTitle,
                selectedQrUri,
            )

        }
    }

    private fun makeQrTitleList(): List<Pair<String, Int>> {
        return makeQrHistoryList().map {
            val titleUriList = it.split("\t")
            val title = titleUriList.firstOrNull() ?: String()
            title to qrLogo
        }.filter { it.first.isNotEmpty() }.reversed()
    }

    private fun makeQrHistoryList(): List<String>
    {
        val qrHistoryParentDirPath = "$currentAppDirPath/${UsePath.cmdclickQrSystemDirRelativePath}"
        val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
        return ReadText(
            qrHistoryParentDirPath,
            cmdclickQrHistoryFileName
        ).textToList()
    }
}
