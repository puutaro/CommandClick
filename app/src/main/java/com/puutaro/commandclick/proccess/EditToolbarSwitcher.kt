package com.puutaro.commandclick.proccess

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object EditToolbarSwitcher {
    fun switch(
        cmdEditFragment: EditFragment?,
        editLongPressType: String,
    ) {
        if(
            editLongPressType.isEmpty()
        ) return
        val context = cmdEditFragment?.context
        val activity = cmdEditFragment?.activity
        if(cmdEditFragment == null) return
        val editExecuteTerminalTag = context?.getString(
            R.string.edit_execute_terminal_fragment
        )
        val editExecuteTerminal = TargetFragmentInstance().getFromFragment<TerminalFragment>(
            activity,
            editExecuteTerminalTag
        ) ?: return
        if(
            !editExecuteTerminal.isVisible
        ) return
        val cmdEditFragmentConfirm = TargetFragmentInstance().getFromFragment<EditFragment>(
            activity,
            cmdEditFragment.tag
        ) ?: return
        if(
            !cmdEditFragmentConfirm.isVisible
        ) return
        if(
            cmdEditFragmentConfirm.tag
                ?.startsWith(
                    FragmentTagManager.Prefix.cmdEditPrefix.str
                ) != true
        ) return
        val onExec = execJsFile(
            cmdEditFragment,
            editLongPressType,
        )
        if(onExec) return
        val binding = cmdEditFragment.binding
        val editTextScroll = binding.editTextScroll
        val pageSearch = binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar

        val cmdclickToolBar = binding.editToolBar
        val webSearch = binding.webSearch
        val webSearchToolbar = webSearch.webSearchToolbar
        val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
        val cmdWebSearchEditText = webSearch.cmdWebSearchEditText

        val linearLayoutParamPageSearchToolBar = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
        )
        cmdclickPageSearchToolBar.layoutParams = linearLayoutParamPageSearchToolBar
        val linearLayoutParamPageWebSearchToolBar = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
        )
        webSearchToolbar.layoutParams = linearLayoutParamPageWebSearchToolBar
        val linearLayoutParamToolbar = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
        )

        cmdclickToolBar.layoutParams = linearLayoutParamToolbar
        cmdPageSearchEditText.setText(String())
        cmdWebSearchEditText.setText(String())
        val onTermSizeLongListenerForEdit =
            context as? EditFragment.OnTermSizeLongListenerForEdit
                ?: return
        when(editLongPressType){
            EditLongPressType.WEB_SEARCH.name -> {
                onTermSizeLongListenerForEdit.onTermSizeLongForEdit()
                webSearchToolbar.isVisible = true
                cmdclickPageSearchToolBar.isVisible = false
                cmdclickToolBar.isVisible = false
                editTextScroll.isVisible = false
                linearLayoutParamPageWebSearchToolBar.weight = 1F
                linearLayoutParamPageSearchToolBar.weight = 0F
                linearLayoutParamToolbar.weight = 0F
                cmdWebSearchEditText.requestFocus()
                cmdPageSearchEditText.clearFocus()
            }
            EditLongPressType.PAGE_SEARCH.name -> {
                onTermSizeLongListenerForEdit.onTermSizeLongForEdit()
                webSearchToolbar.isVisible = false
                cmdclickPageSearchToolBar.isVisible = true
                cmdclickToolBar.isVisible = false
                editTextScroll.isVisible = false
                linearLayoutParamPageWebSearchToolBar.weight = 0F
                linearLayoutParamPageSearchToolBar.weight = 1F
                linearLayoutParamToolbar.weight = 0F
                cmdWebSearchEditText.clearFocus()
                cmdPageSearchEditText.requestFocus()
            }
            EditLongPressType.NORMAL.name -> {
                webSearchToolbar.isVisible = false
                cmdclickPageSearchToolBar.isVisible = false
                cmdclickToolBar.isVisible = true
                editTextScroll.isVisible = true
                linearLayoutParamPageWebSearchToolBar.weight = 0F
                linearLayoutParamPageSearchToolBar.weight = 0F
                linearLayoutParamToolbar.weight = 1F
                cmdWebSearchEditText.clearFocus()
                cmdPageSearchEditText.requestFocus()
            }
            else -> {
                if(
                    !File(editLongPressType).isFile
                ) return
                if(
                    !editLongPressType.endsWith(
                        CommandClickScriptVariable.JSX_FILE_SUFFIX
                    )
                    || !editLongPressType.endsWith(
                        CommandClickScriptVariable.JS_FILE_SUFFIX
                    )
                ) return
                cmdEditFragment.jsExecuteJob?.cancel()
                cmdEditFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        cmdEditFragment,
                        context.getString(
                            R.string.edit_execute_terminal_fragment
                        )
                    )
                    if(!onLaunchUrl) return@launch
                    withContext(Dispatchers.Main) {
                        val listenerForWebLaunch = context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                        listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                            JavaScriptLoadUrl.make(
                                context,
                                editLongPressType,
                            ).toString()
                        )
                    }
                }
            }
        }
    }
}


private fun execJsFile(
    cmdEditFragment: EditFragment?,
    editLongPressType: String,
): Boolean {
    when(editLongPressType){
        EditLongPressType.WEB_SEARCH.name,
        EditLongPressType.PAGE_SEARCH.name,
        EditLongPressType.NORMAL.name -> {
            return false
        }
    }
    if(
        !File(editLongPressType).isFile
    ) return false
    val isJsSuffix = editLongPressType.endsWith(
        CommandClickScriptVariable.JSX_FILE_SUFFIX
    )
            || editLongPressType.endsWith(
        CommandClickScriptVariable.JS_FILE_SUFFIX
    )
    if(!isJsSuffix) return false
    if(cmdEditFragment == null) return false
    val context = cmdEditFragment.context ?: return false
    cmdEditFragment.jsExecuteJob?.cancel()
    cmdEditFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
        val onLaunchUrl = EnableTerminalWebView.check(
            cmdEditFragment,
            context.getString(
                R.string.edit_execute_terminal_fragment
            )
        )
        if(!onLaunchUrl) return@launch
        withContext(Dispatchers.Main) {
            val listenerForWebLaunch = context as? EditFragment.OnLaunchUrlByWebViewForEditListener
            listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                JavaScriptLoadUrl.make(
                    context,
                    editLongPressType,
                ).toString()
            )
        }
    }
    return true
}

enum class EditLongPressType {
    WEB_SEARCH,
    PAGE_SEARCH,
    NORMAL,
}