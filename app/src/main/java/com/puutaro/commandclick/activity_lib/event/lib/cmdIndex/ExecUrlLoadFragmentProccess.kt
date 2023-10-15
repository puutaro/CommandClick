package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object ExecUrlLoadFragmentProccess {
    fun execUrlLoadCmdIndexFragment (
        activity: MainActivity,
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        val cmdIndexFragmentTag = activity.getString(R.string.command_index_fragment)
        val supportFragmentManager = activity.supportFragmentManager
        val cmdIndexFragment = try {
            supportFragmentManager.findFragmentByTag(cmdIndexFragmentTag) as CommandIndexFragment
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${cmdIndexFragmentTag}")
            return
        }
        if(!cmdIndexFragment.isVisible) return
        val context = cmdIndexFragment.context
        val binding = cmdIndexFragment.binding
        val cmdindexInternetButton = binding.cmdindexInternetButton
        binding.cmdListSwipeToRefresh.isVisible = false
        context?.let {
            cmdindexInternetButton.setImageResource(
                SearchSwichImage.WEB.image
            )
            cmdindexInternetButton.imageTintList = context.getColorStateList(R.color.terminal_color)
            cmdindexInternetButton.setBackgroundTintList(it.getColorStateList(R.color.icon_selected_color));
            (it.getColor(R.color.white))
        }
        if(terminalViewModel.readlinesNum == ReadLines.SHORTH) {
            ExecTerminalLongOrShort.open<CommandIndexFragment>(
                cmdIndexFragmentTag,
                supportFragmentManager,
                terminalViewModel
            )
        }
        cmdIndexFragment.WebSearchSwitch = true
    }


    fun execUrlLoadCmdVriableEditFragment (
        activity: MainActivity,
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_script_file_name
            ),
            FragmentTagManager.Suffix.ON.str
        )
        val supportFragmentManager = activity.supportFragmentManager
        val editFragment = try {
            supportFragmentManager.findFragmentByTag(
                cmdEditFragmentTag
            ) as EditFragment
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${cmdEditFragmentTag}")
            return
        }
        if(!editFragment.isVisible) return
        EditLayoutViewHideShow.exec(
            editFragment,
            false
        )
        if(
            terminalViewModel.readlinesNum != ReadLines.SHORTH
        ) return
        ExecTerminalLongOrShort.open<CommandIndexFragment>(
            cmdEditFragmentTag,
            supportFragmentManager,
            terminalViewModel
        )

    }
}