package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

//class ToolBarInternetButtonControl(
//    private val cmdIndexFragment: CommandIndexFragment,
//    fannelInfoMap: Map<String, String>,
//) {
//    private val context = cmdIndexFragment.context
//    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
//    private val binding = cmdIndexFragment.binding
//    private val cmdclickToolBar = cmdIndexFragment.binding.cmdclickToolBar
////    private val cmdindexInternet = binding.cmdindexInternetButton
////    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////        fannelInfoMap
////    )
//
//    fun interneButtontSetOnClickListener () {
//        val terminalStateList = context?.getColorStateList(R.color.terminal_color)
//        val whiteStateList = context?.getColorStateList(R.color.white)
//        val grayStateList = context?.getColorStateList(R.color.icon_selected_color)
//        cmdindexInternet.setOnClickListener {
//                buttonView ->
//            if(!cmdIndexFragment.isVisible) return@setOnClickListener
//            if(!cmdclickToolBar.isVisible) return@setOnClickListener
//            try {
//                cmdIndexFragment.activity?.supportFragmentManager?.findFragmentByTag(
//                    context?.getString(R.string.index_terminal_fragment)
//                ) as TerminalFragment
//            } catch(e: Exception){
//                context?.let {
//                    cmdindexInternet.imageTintList = it.getColorStateList(R.color.terminal_color)
//                    cmdindexInternet.backgroundTintList = it.getColorStateList(R.color.icon_selected_color)
//                }
//                return@setOnClickListener
//            }
//
//            val linearLayoutParam =
//                cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
//            val cmdIndexFragmentWeight = linearLayoutParam.weight
//            val enableExecInternetButtonJs =
//                cmdIndexFragmentWeight != ReadLines.LONGTH
//                        && terminalViewModel.onExecInternetButtonShell
//                        && cmdIndexFragment.WebSearchSwitch
//            if(
//                enableExecInternetButtonJs
//            ) {
//                ExecJsLoad.execJsLoad(
//                    cmdIndexFragment,
////                    currentAppDirPath,
//                    UsePath.cmdclickInternetButtonExecJsFileName,
//                )
//                return@setOnClickListener
//            }
//
//            if(cmdIndexFragment.WebSearchSwitch){
//                cmdindexInternet.imageTintList = terminalStateList
//                cmdindexInternet.backgroundTintList = whiteStateList
//                cmdIndexFragment.WebSearchSwitch = !cmdIndexFragment.WebSearchSwitch
//                return@setOnClickListener
//            }
//            cmdindexInternet.imageTintList = terminalStateList
//            cmdindexInternet.backgroundTintList = grayStateList
//            cmdIndexFragment.WebSearchSwitch = !cmdIndexFragment.WebSearchSwitch
//        }
//
//        cmdindexInternet.setOnLongClickListener {
//            view ->
//            CmdIndexToolbarSwitcher.switch(
//                cmdIndexFragment,
//                true
//            )
//            false
//        }
//    }
//
//}