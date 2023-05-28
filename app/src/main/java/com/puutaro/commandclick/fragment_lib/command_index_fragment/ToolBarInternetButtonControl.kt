package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ToolBarInternetButtonControl(
    private val cmdIndexCommandIndexFragment: CommandIndexFragment,
    readSharePreffernceMap: Map<String, String>,
) {
    private val context = cmdIndexCommandIndexFragment.context
    private val terminalViewModel: TerminalViewModel by cmdIndexCommandIndexFragment.activityViewModels()
    private val binding = cmdIndexCommandIndexFragment.binding
    private val cmdclickToolBar = cmdIndexCommandIndexFragment.binding.cmdclickToolBar
    private val cmdindexInternet = binding.cmdindexInternetButton
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    fun interneButtontSetOnClickListener () {
        val blackStateList = context?.getColorStateList(R.color.black)
        val whiteStateList = context?.getColorStateList(R.color.white)
        val grayStateList = context?.getColorStateList(R.color.gray_out)
        cmdindexInternet.setOnClickListener {
                buttonView ->
            if(!cmdIndexCommandIndexFragment.isVisible) return@setOnClickListener
            if(!cmdclickToolBar.isVisible) return@setOnClickListener
            try {
                cmdIndexCommandIndexFragment.activity?.supportFragmentManager?.findFragmentByTag(
                    context?.getString(R.string.index_terminal_fragment)
                ) as TerminalFragment
            } catch(e: Exception){
                context?.let {
                    cmdindexInternet.imageTintList = it.getColorStateList(R.color.black)
                    cmdindexInternet.backgroundTintList = it.getColorStateList(R.color.gray_out)
                }
                return@setOnClickListener
            }

            val enableExecInternetButtonJs =
                terminalViewModel.readlinesNum != ReadLines.SHORTH
                        && terminalViewModel.onExecInternetButtonShell
                        && cmdIndexCommandIndexFragment.WebSearchSwitch
            if(
                enableExecInternetButtonJs
            ) {
                ExecJsLoad.execJsLoad(
                    cmdIndexCommandIndexFragment,
                    currentAppDirPath,
                    UsePath.cmdclickInternetButtonExecJsFileName,
                )
                return@setOnClickListener
            }

            if(cmdIndexCommandIndexFragment.WebSearchSwitch){
                cmdindexInternet.imageTintList = blackStateList
                cmdindexInternet.backgroundTintList = whiteStateList;
                cmdIndexCommandIndexFragment.WebSearchSwitch = !cmdIndexCommandIndexFragment.WebSearchSwitch
                return@setOnClickListener
            }
            cmdindexInternet.imageTintList = blackStateList
            cmdindexInternet.backgroundTintList = grayStateList
            cmdIndexCommandIndexFragment.WebSearchSwitch = !cmdIndexCommandIndexFragment.WebSearchSwitch
        }

        cmdindexInternet.setOnLongClickListener {
            view ->
            CmdIndexToolbarSwitcher.switch(
                cmdIndexCommandIndexFragment,
                true
            )
            false
        }
    }

}