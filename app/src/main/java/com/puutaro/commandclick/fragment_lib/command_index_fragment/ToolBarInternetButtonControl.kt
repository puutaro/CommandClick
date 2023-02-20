package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.ExecTerminalDo
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ToolBarInternetButtonControl(
    private val cmdIndexFragment: CommandIndexFragment,
    private val readSharePreffernceMap: Map<String, String>,
) {
    private val context = cmdIndexFragment.context
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val binding = cmdIndexFragment.binding
    private val cmdclickToolBar = cmdIndexFragment.binding.cmdclickToolBar
    private val cmdindexInternet = binding.cmdindexInternetButton
    private val cmdSearchEditText = binding.cmdSearchEditText
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
            if(!cmdIndexFragment.isVisible) return@setOnClickListener
            if(!cmdclickToolBar.isVisible) return@setOnClickListener
            try {
                cmdIndexFragment.activity?.supportFragmentManager?.findFragmentByTag(
                    context?.getString(R.string.index_terminal_fragment)
                ) as TerminalFragment
            } catch(e: Exception){
                context?.let {
                    cmdindexInternet.imageTintList = it.getColorStateList(R.color.black)
                    cmdindexInternet.setBackgroundTintList(it.getColorStateList(R.color.white))
                }
                return@setOnClickListener
            }

            val enableExecInternetButtonShell =
                terminalViewModel.readlinesNum != ReadLines.SHORTH
                        && terminalViewModel.onExecInternetButtonShell
                        && cmdIndexFragment.WebSearchSwitch
            if(
                enableExecInternetButtonShell
            ) {
                ExecTerminalDo.execTerminalDo(
                    cmdIndexFragment,
                    currentAppDirPath,
                    UsePath.cmdclickInternetButtonExecShellFileName,
                )
                return@setOnClickListener
            }

            if(cmdIndexFragment.WebSearchSwitch){
                cmdindexInternet.imageTintList = blackStateList
                cmdindexInternet.setBackgroundTintList(whiteStateList);
                AutoCompleteEditTexter.setAdapter(
                    context,
                    cmdSearchEditText,
                )
                cmdIndexFragment.WebSearchSwitch = !cmdIndexFragment.WebSearchSwitch
                return@setOnClickListener
            }
            cmdindexInternet.imageTintList = blackStateList
            cmdindexInternet.setBackgroundTintList(grayStateList)
            cmdIndexFragment.WebSearchSwitch = !cmdIndexFragment.WebSearchSwitch
            if(
                terminalViewModel.readlinesNum != ReadLines.SHORTH
            ) {
                AutoCompleteEditTexter.setAdapter(
                    context,
                    cmdSearchEditText,
                    currentAppDirPath,
                )
            }
        }

        cmdindexInternet.setOnLongClickListener {
            view ->
            CmdIndexToolbarSwitcher.switch(
                cmdIndexFragment,
                true
            )
            false
        }
    }

}