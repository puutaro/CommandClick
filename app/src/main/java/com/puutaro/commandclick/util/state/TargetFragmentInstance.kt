package com.puutaro.commandclick.util.state

import android.app.Activity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment


class TargetFragmentInstance {
    fun <T: Fragment> getFromFragment(
        activity: FragmentActivity?,
        targetFragmentTag: String?
    ): T? {
        return try {
            activity?.supportFragmentManager?.findFragmentByTag(
                targetFragmentTag
            ) as T
        } catch (e: Exception){
            null
        }
    }

    fun <T: Fragment> getFromActivity(
        activity: MainActivity?,
        targetFragmentTag: String?
    ): T? {
        return try {
            activity?.supportFragmentManager?.findFragmentByTag(
                targetFragmentTag
            )  as T
        } catch (e: Exception){
            null
        }
    }

    fun getCmdIndexFragment(
        activity: MainActivity,
    ): CommandIndexFragment? {
        val cmdIndexFragment = getFromActivity<CommandIndexFragment>(
            activity,
            activity.getString(R.string.command_index_fragment)
        )
        if(
            cmdIndexFragment != null
            && cmdIndexFragment.isVisible
        ) {
            return cmdIndexFragment
        }
        return null
    }

    fun getCurrentBottomFragment(
        activity: MainActivity,
        cmdVariableEditFragmentTag: String,
    ): Fragment? {
        val cmdIndexFragment = getCmdIndexFragment(activity)
        if(
            cmdIndexFragment != null
            && cmdIndexFragment.isVisible
        ) {
            return cmdIndexFragment
        }

        val cmdVariableEditFragment = getFromActivity<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        if(
            cmdVariableEditFragment != null
            && cmdVariableEditFragment.isVisible
//            && cmdVariableEditFragment.view?.height != 0
        ) {
            return cmdVariableEditFragment
        }
        return null
    }

    fun getCurrentBottomFragmentInFrag(
        activity: FragmentActivity?,
        cmdVariableEditFragmentTag: String,
        onNoHeightZeroCheckForEdit: Boolean = true,
    ): Fragment? {
        if(
            activity == null
        ) return null
        val cmdIndexFragment = getFromFragment<CommandIndexFragment>(
            activity,
            activity.getString(R.string.command_index_fragment)
        )
        if(
            cmdIndexFragment != null
            && cmdIndexFragment.isVisible
        ) {
            return cmdIndexFragment
        }

        val cmdVariableEditFragment = getFromFragment<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        val isHeight = onNoHeightZeroCheckForEdit || cmdVariableEditFragment?.view?.height != 0
        if(
            cmdVariableEditFragment != null
            && cmdVariableEditFragment.isVisible
            && isHeight
        ) {
            return cmdVariableEditFragment
        }
        return null
    }

    fun getCurrentEditFragmentFromActivity(
        activity: MainActivity,
//        currentAppDirPath: String,
        currentFannelPath: String,
        fannelState: String,
    ): EditFragment? {
        val cmdEditFragTag = FragmentTagManager.makeCmdValEditTag(
//            currentAppDirPath,
            currentFannelPath,
            fannelState,
        )
        val cmdVariableEditFragment = getFromActivity<EditFragment>(
            activity,
            cmdEditFragTag
        )
        if(
            cmdVariableEditFragment != null
            && cmdVariableEditFragment.isVisible
//            && cmdVariableEditFragment.view?.height != 0
        ) {
            return cmdVariableEditFragment
        }
        val settingEditFragTag = FragmentTagManager.makeSettingValEditTag(
//            currentAppDirPath,
            currentFannelPath,
        )
        val settingVariableEditFragment = getFromActivity<EditFragment>(
            activity,
            settingEditFragTag
        )
        if(
            settingVariableEditFragment != null
            && settingVariableEditFragment.isVisible
        ) {
            return settingVariableEditFragment
        }
        return null
    }

    fun getCurrentEditFragmentFromFragment(
        activity: FragmentActivity?,
//        currentAppDirPath: String,
        currentFannelPath: String,
        fannelState: String,
    ): EditFragment? {
        if(
            activity == null
        ) return null
        val cmdEditFragTag = FragmentTagManager.makeCmdValEditTag(
//            currentAppDirPath,
            currentFannelPath,
            fannelState,
        )
        val cmdVariableEditFragment =
            getFromFragment<EditFragment>(
            activity,
            cmdEditFragTag
        )
        if(
            cmdVariableEditFragment != null
            && cmdVariableEditFragment.isVisible
//            && cmdVariableEditFragment.view?.height != 0
        ) {
            return cmdVariableEditFragment
        }
        val settingEditFragTag = FragmentTagManager.makeSettingValEditTag(
//            currentAppDirPath,
            currentFannelPath,
        )
        val settingVariableEditFragment = getFromFragment<EditFragment>(
            activity,
            settingEditFragTag
        )
        if(
            settingVariableEditFragment != null
            && settingVariableEditFragment.isVisible
        ) {
            return settingVariableEditFragment
        }
        return null
    }

    fun getCurrentBottomFragmentWeight(
        currentBottomFragment: Fragment?,
    ): Float? {
        return when(currentBottomFragment){
            is CommandIndexFragment -> {
                val linearLayoutParam =
                    currentBottomFragment.binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
                linearLayoutParam.weight
            }
            is EditFragment -> {
                val linearLayoutParam =
                    currentBottomFragment.binding.editFragment.layoutParams as LinearLayout.LayoutParams
                linearLayoutParam.weight
            }
            else -> null
        }
    }

    fun getCmdEditFragmentTag(
        activity: Activity?
    ): String {
        if(
            activity == null
        ) return String()
        val sharePref = FannelInfoTool.getSharePref(activity)
//        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
//            sharePref,
//            FannelInfoSetting.current_app_dir
//        )
        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
            sharePref,
            FannelInfoSetting.current_fannel_name
        )
        val fannelState = FannelInfoTool.getStringFromFannelInfo(
            sharePref,
            FannelInfoSetting.current_fannel_state
        )
        return FragmentTagManager.makeCmdValEditTag(
//            currentAppDirPath,
            currentFannelName,
            fannelState
        )
    }

    fun getCurrentTerminalFragment(
        activity: MainActivity?
    ): TerminalFragment? {
        if(
            activity == null
        ) return null
        val indexTerminalFragmentTag =  activity.getString(R.string.index_terminal_fragment)
        val editExecuteTerminalFragmentTag =  activity.getString(R.string.edit_terminal_fragment)
        val targetFragmentInstance = TargetFragmentInstance()
        val indexTerminalFragment = targetFragmentInstance.getFromActivity<TerminalFragment>(
            activity,
            indexTerminalFragmentTag
        )
        if(
            indexTerminalFragment != null
            && indexTerminalFragment.isVisible
        ) return indexTerminalFragment
        val editExecuteTerminalFragment = targetFragmentInstance.getFromActivity<TerminalFragment>(
            activity,
            editExecuteTerminalFragmentTag
        )
        if(
            editExecuteTerminalFragment != null
            && editExecuteTerminalFragment.isVisible
        ) return editExecuteTerminalFragment
        return null
    }
    fun getCurrentTerminalFragmentFromFrag(
        activity: FragmentActivity?
    ): TerminalFragment? {
        if(
            activity == null
        ) return null
        val indexTerminalFragmentTag =  activity.getString(R.string.index_terminal_fragment)
        val editExecuteTerminalFragmentTag =  activity.getString(R.string.edit_terminal_fragment)
        val targetFragmentInstance = TargetFragmentInstance()
        val indexTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            indexTerminalFragmentTag
        )
        if(
            indexTerminalFragment != null
            && indexTerminalFragment.isVisible
        ) return indexTerminalFragment
        val editExecuteTerminalFragment = targetFragmentInstance.getFromFragment<TerminalFragment>(
            activity,
            editExecuteTerminalFragmentTag
        )
        if(
            editExecuteTerminalFragment != null
            && editExecuteTerminalFragment.isVisible
        ) return editExecuteTerminalFragment
        return null
    }
}