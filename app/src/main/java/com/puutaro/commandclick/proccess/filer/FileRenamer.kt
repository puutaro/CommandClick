package com.puutaro.commandclick.proccess.filer

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object FileRenamer {

    private var promptDialog: Dialog? = null

    fun rename(
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
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No type item name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val renamedFileName = UsePath.compExtend(
                inputEditable.toString(),
                extend
            )
            if(
                fileName == renamedFileName
            ) return@setOnClickListener
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

}