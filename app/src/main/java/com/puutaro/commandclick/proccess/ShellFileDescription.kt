package com.puutaro.commandclick.proccess

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog


class ShellFileDescription {
    companion object {
        fun show(
            context: Context?,
            currentShellContentsList: List<String>,
            shellFileName: String
        ){
            if(context == null) return
            val descripitionContents = makeDescriptionContents(
                currentShellContentsList,
                shellFileName
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
            alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        }


        private fun makeDescriptionContents(
            currentShellContentsList: List<String>,
            shellFileName: String
        ): String {
            val languageType =
                JsOrShellFromSuffix.judge(shellFileName)

            val languageTypeToSectionHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
            val labelingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.LABELING_SEC_START
            ) as String
            val labelingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.LABELING_SEC_END
            ) as String

            val descripitionContentsList =
                CommandClickVariables.substituteVariableListFromHolder(
                    currentShellContentsList,
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
                        .removePrefix("#")
                } ?: return String()
            return descripitionContentsList.joinToString("\n")
        }
    }
}