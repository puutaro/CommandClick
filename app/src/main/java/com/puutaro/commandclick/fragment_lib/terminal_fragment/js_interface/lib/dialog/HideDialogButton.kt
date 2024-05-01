package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object HideDialogButton {

    private const val separator = "&"
    enum class HideButtonType(
        val type: String,
    ) {
        SHARE("share"),
        CANCEL("cancel"),
        OK("ok"),
    }


    fun buttonVisualHandler(
        dialogObj: Dialog?,
        jsDialogMap: Map<String, String>?,
        hideButtonMap: Map<String, AppCompatImageButton?>,
        totalButtonNum: Int,
    ){
        val linearLayoutForImageButtonParam = LinearLayoutCompat.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight =  culcWeight(
            jsDialogMap,
            hideButtonMap,
            totalButtonNum,
        )
        hideButtonMap.entries.forEach {
            val buttonType = it.key
            val imageButton = it.value
                ?: return@forEach
            imageButton.isVisible = judgeHide(
                jsDialogMap,
                buttonType,
            )
            imageButton.layoutParams = linearLayoutForImageButtonParam
        }
    }


    private fun culcWeight(
        asciiArtMap: Map<String, String>?,
        hideButtonMap: Map<String, AppCompatImageButton?>,
        totalButtonNum: Int,
    ): Float {
        val defaultWeight = 0.3333f
        if(
            asciiArtMap.isNullOrEmpty()
        ) return defaultWeight
        val hideButtonTypeList = hideButtonMap.entries.map { it.key }
        val hideButtonList =
            asciiArtMap.get(JsDialogButtonMapKey.HIDE_BUTTONS.key)
                ?.split(separator)?.sorted()?.distinct()?.filter {
                    hideButtonTypeList.contains(it)
                }
        if(
            hideButtonList.isNullOrEmpty()
        ) return defaultWeight

        val showButtonNum = totalButtonNum - hideButtonList.size
        return 1f / showButtonNum
    }

    private fun judgeHide(
        jsDialogMap: Map<String, String>?,
        curButtonType: String,
    ): Boolean {
        val visibleOk = true
        if(
            jsDialogMap.isNullOrEmpty()
        ) return visibleOk
        val hideButtonList =
            jsDialogMap.get(JsDialogButtonMapKey.HIDE_BUTTONS.key)
                ?.split(separator)
                ?.sorted()
                ?.distinct()
                ?: return visibleOk
        val isNotVisible = !hideButtonList.contains(curButtonType)
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsDialogMap.txt").absolutePath,
//            listOf(
//                "jsdialogMap: ${jsDialogMap}",
//                "hideButton:value: ${jsDialogMap.get(JsDialogButtonMapKey.HIDE_BUTTONS.key)}",
//                "curButtonType: ${curButtonType}",
//                "hideButtonList: ${hideButtonList}",
//                "isNotVisible: ${isNotVisible}",
//            ).joinToString("\n\n")
//        )
        return isNotVisible
    }
}