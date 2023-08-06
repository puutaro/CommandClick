package com.puutaro.commandclick.proccess

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.Image
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ScriptFileDescription {

    private var subMenuDialog: Dialog? = null

    fun show(
        contextSrc: Context?,
        currentScriptContentsList: List<String>,
        scriptFileName: String
    ) {
        val context = contextSrc
            ?: return
        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.text_simple_dialog_layout
        )
        val titleTextView =
            subMenuDialog?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        titleTextView?.text = "Description"
//        titleTextView?.isVisible = false
        val descriptionTextView =
            subMenuDialog?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        descriptionTextView?.text =
            makeDescriptionContents(
                currentScriptContentsList,
                scriptFileName
            )
        val cancelImageButton =
            subMenuDialog?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            subMenuDialog?.dismiss()
        }
        subMenuDialog?.setOnCancelListener {
            subMenuDialog?.dismiss()
        }
        subMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        subMenuDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        subMenuDialog?.show()

    }

    fun show2(
        context: Context?,
        currentScriptContentsList: List<String>,
        scriptFileName: String
    ){
        if(context == null) return
        val descripitionContents = makeDescriptionContents(
            currentScriptContentsList,
            scriptFileName
        )


        val linearLayout = LinearLayoutAdderForDialog.add(
            context,
            descripitionContents
        ) ?: return

        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Description"
            )
            .setView(linearLayout)
            .setPositiveButton("OK", null)
            .show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context.getColor(android.R.color.black) as Int
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
    }


    fun makeDescriptionContents(
        currentScriptContentsList: List<String>,
        scriptFileName: String
    ): String {
        val languageType =
            JsOrShellFromSuffix.judge(scriptFileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val labelingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
        ) as String
        val labelingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
        ) as String
        val removePrefix = if(languageType == LanguageTypeSelects.SHELL_SCRIPT){
            "#"
        } else "//"

        val descripitionContentsList =
            CommandClickVariables.substituteVariableListFromHolder(
                currentScriptContentsList,
                labelingSectionStart,
                labelingSectionEnd,
            )?.filter {
                (
                        !it.startsWith(labelingSectionStart)
                                && !it.endsWith(labelingSectionStart)
                        )
                        && (
                        !it.startsWith(labelingSectionEnd)
                                && !it.endsWith(labelingSectionEnd)
                        )
            }?.map {
                it
                    .trim(' ')
                    .removePrefix(removePrefix)
            } ?: return String()
        return descripitionContentsList.joinToString("\n")
    }
}