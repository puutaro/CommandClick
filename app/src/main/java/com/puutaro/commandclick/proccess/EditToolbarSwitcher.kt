package com.puutaro.commandclick.proccess

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object EditToolbarSwitcher {
    fun switch(
        cmdEditFragment: EditFragment?,
        execPlayBtnLongPress: String,
    ) {
        if(
            execPlayBtnLongPress.isEmpty()
        ) return
        val context = cmdEditFragment?.context
        val activity = cmdEditFragment?.activity
        if(cmdEditFragment == null) return
        val editExecuteTerminalTag = context?.getString(
            R.string.edit_terminal_fragment
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
                    FragmentTagManager.Prefix.CMD_EDIT_PREFIX.str
                ) != true
        ) return
        val onExec = execJsFile(
            cmdEditFragment,
            execPlayBtnLongPress,
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
        when(execPlayBtnLongPress){
            EditLongPressType.WEB_SEARCH.name -> {
                onTermSizeLongListenerForEdit.onTermSizeLongForEdit(cmdEditFragment)
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
                onTermSizeLongListenerForEdit.onTermSizeLongForEdit(cmdEditFragment)
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
                    !File(execPlayBtnLongPress).isFile
                ) return
                if(
                    !execPlayBtnLongPress.endsWith(
                        UsePath.JSX_FILE_SUFFIX
                    )
                    || !execPlayBtnLongPress.endsWith(
                        UsePath.JS_FILE_SUFFIX
                    )
                ) return
                cmdEditFragment.jsExecuteJob?.cancel()
                cmdEditFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        cmdEditFragment,
                        context.getString(
                            R.string.edit_terminal_fragment
                        )
                    )
                    if(!onLaunchUrl) return@launch
                    withContext(Dispatchers.Main) {
                        val listenerForWebLaunch = context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                        listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                            JavaScriptLoadUrl.make(
                                context,
                                execPlayBtnLongPress,
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
    if(editLongPressType.isEmpty()){
        LogSystems.stdSys("blank file")
    }
    if(
        !File(editLongPressType).isFile
    ) {
        LogSystems.stdWarn("file not found")
        return true
    }
    val isJsSuffix = editLongPressType.endsWith(
        UsePath.JSX_FILE_SUFFIX
    )
            || editLongPressType.endsWith(
        UsePath.JS_FILE_SUFFIX
    )
    if(!isJsSuffix) {
        LogSystems.stdWarn("no js file")
        return true
    }
    if(cmdEditFragment == null) {
        LogSystems.stdWarn("web view not found")
        return true
    }
    val context = cmdEditFragment.context ?: return true
    cmdEditFragment.jsExecuteJob?.cancel()
    cmdEditFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
        val onLaunchUrl = EnableTerminalWebView.check(
            cmdEditFragment,
            context.getString(
                R.string.edit_terminal_fragment
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