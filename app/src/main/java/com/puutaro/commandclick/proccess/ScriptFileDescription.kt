package com.puutaro.commandclick.proccess

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog


object ScriptFileDescription {
    fun show(
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
            CommandClickScriptVariable.Companion.HolderTypeName.LABELING_SEC_START
        ) as String
        val labelingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.LABELING_SEC_END
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