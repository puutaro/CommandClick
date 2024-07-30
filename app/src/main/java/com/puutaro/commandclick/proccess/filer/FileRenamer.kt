package com.puutaro.commandclick.proccess.filer

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexDuplicate
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object FileRenamer {

    private var promptDialog: Dialog? = null

    fun rename(
        fragment: Fragment,
        parentDirPath: String,
        fileName: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                execRename(
                    fragment,
                    parentDirPath,
                    fileName,
                )
            }
        }
    }
    private fun execRename(
        fragment: Fragment,
        parentDirPath: String,
        fileName: String,
    ){
        val context = fragment.context
            ?: return
        val extend = CcPathTool.subExtend(fileName)

        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Rename app dir"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        promptEditText?.setText(
            fileName.removeSuffix(extend)
        )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
            promptDialog = null
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            promptDialog = null
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                ToastUtils.showShort("No type item name")
                return@setOnClickListener
            }
            val renamedFileName = UsePath.compExtend(
                inputEditable.toString().trim(),
                extend
            )
            if(
                fileName == renamedFileName
            ) {
                alreadyExistToast(renamedFileName)
                return@setOnClickListener
            }
            ListIndexDuplicate.isFileDetect(
                parentDirPath,
                renamedFileName,
            ).let {
                    isDetect ->
                if(
                    isDetect
                ) return@setOnClickListener
            }
            FileSystems.moveFileWithDir(
                File(parentDirPath, fileName),
                File(parentDirPath, renamedFileName)
            )
            when(fragment){
                is CommandIndexFragment -> {
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                    )
                }
                is EditFragment -> {
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                    )
                }
            }
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
            promptDialog = null
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }

    private fun alreadyExistToast(con: String){
        CoroutineScope(Dispatchers.Main).launch{
            ToastUtils.showLong("Already exist: ${con}")
        }
    }

}