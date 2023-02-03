package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.AutoCompleteThreshold
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ExecUrlloadFragmentProccess {
    companion object {

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
                cmdindexInternetButton.imageTintList = context.getColorStateList(R.color.black)
                cmdindexInternetButton.setBackgroundTintList(it.getColorStateList(R.color.gray_out));
                (it.getColor(R.color.white))
            }
            if(terminalViewModel.readlinesNum == ReadLines.SHORTH) {
                ExecTerminalLongOrShort.open<CommandIndexFragment>(
                    cmdIndexFragmentTag,
                    supportFragmentManager,
                    terminalViewModel
                )
            }
            cmdIndexFragment.SpecialSearchSwitch = true
            val sharePref =  activity.getPreferences(Context.MODE_PRIVATE)
            val currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            )
            AutoCompleteEditTexter.setAdapter(
                context,
                binding.cmdSearchEditText,
                currentAppDirPath,
                AutoCompleteThreshold.ON.num
            )

        }


        fun execUrlLoadCmdVriableEditFragment (
            activity: MainActivity,
        ){
            val terminalViewModel: TerminalViewModel =
                ViewModelProvider(activity).get(TerminalViewModel::class.java)
            val editFragmentTag = activity.getString(R.string.cmd_variable_edit_fragment)
            val supportFragmentManager = activity.supportFragmentManager
            val editFragment = try {
                supportFragmentManager.findFragmentByTag(
                    editFragmentTag
                ) as EditFragment
            } catch(e: java.lang.Exception){
                Log.d(this.toString(), "not exist ${editFragmentTag}")
                return
            }
            if(!editFragment.isVisible) return
            if(terminalViewModel.readlinesNum != ReadLines.SHORTH) return
            ExecTerminalLongOrShort.open<CommandIndexFragment>(
                editFragmentTag,
                supportFragmentManager,
                terminalViewModel
            )

        }

    }
}