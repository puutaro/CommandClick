package com.puutaro.commandclick.util

import android.app.Activity
import android.content.Context
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment


class TargetFragmentInstance {
    fun <T: Fragment> getFromFragment(
        activity: FragmentActivity?,
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

    fun getCurrentBottomFragment(
        activity: MainActivity,
        cmdVariableEditFragmentTag: String,
    ): Fragment? {
        val cmdIndexFragment = TargetFragmentInstance().getFromActivity<CommandIndexFragment>(
            activity,
            activity.getString(R.string.command_index_fragment)
        )
        if(
            cmdIndexFragment != null
            && cmdIndexFragment.isVisible
        ) {
            return cmdIndexFragment
        }

        val cmdVariableEditFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
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
        val cmdIndexFragment = TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
            activity,
            activity.getString(R.string.command_index_fragment)
        )
        if(
            cmdIndexFragment != null
            && cmdIndexFragment.isVisible
        ) {
            return cmdIndexFragment
        }

        val cmdVariableEditFragment = TargetFragmentInstance().getFromFragment<EditFragment>(
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
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        return FragmentTagManager.makeTag(
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
    }
}