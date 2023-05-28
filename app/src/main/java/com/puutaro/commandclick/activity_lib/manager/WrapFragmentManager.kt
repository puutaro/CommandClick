package com.puutaro.commandclick.activity_lib.manager

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.manager.curdForFragment.FragmentManagerForActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment

class WrapFragmentManager {
    companion object {

        fun initFragment(
            savedInstanceState: Bundle?,
            supportFragmentManager: FragmentManager,
            terminalFragmentTag: String,
            commandIndexFragmentTag: String,
        ){
            if( savedInstanceState != null) return
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            fragmentManagerForActivity.initFragments(
                terminalFragmentTag,
                commandIndexFragmentTag,
            )
            fragmentManagerForActivity.commit()
        }


        fun changeFragmentByKeyBoardVisibleChange(
            isKeyboardShowing: Boolean,
            supportFragmentManager: FragmentManager,
            terminalFragmentTag: String,
        ){
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            if(isKeyboardShowing){
                fragmentManagerForActivity.hideFragment<TerminalFragment>(
                    terminalFragmentTag
                )
                fragmentManagerForActivity.commit()
                return
            }
            fragmentManagerForActivity.showFragment<TerminalFragment>(
                terminalFragmentTag
            )
            fragmentManagerForActivity.commit()
        }


        fun changeFragmentAtListItemClicked(
            supportFragmentManager: FragmentManager,
            terminalFragmentTag: String,
        ){
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            fragmentManagerForActivity.showFragment<TerminalFragment>(
                terminalFragmentTag
            )
            fragmentManagerForActivity.commit()
        }



        fun changeFragmentAtCancelClickWhenConfirm(
            supportFragmentManager: FragmentManager,
            terminalFragmentTag: String,
            commandIndexFragmentTag: String,
        ){
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            fragmentManagerForActivity.deleteAllBackStack()
            fragmentManagerForActivity.replaceFragment(
                R.id.main_container,
                TerminalFragment(),
                terminalFragmentTag
            )
            fragmentManagerForActivity.addFragment(
                CommandIndexFragment(),
                commandIndexFragmentTag
            )
            fragmentManagerForActivity.commit()
        }


        fun changeFragmentEdit(
            supportFragmentManager: FragmentManager,
            editFragmentTag: String,
            terminalFragmentTag: String,
            onInit: Boolean = false
        ){
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            when(terminalFragmentTag){
                String() -> {
                    fragmentManagerForActivity.replaceFragment(
                        R.id.main_container,
                        EditFragment(),
                        editFragmentTag
                    )
                }
                else -> {
                    fragmentManagerForActivity.replaceFragment(
                        R.id.main_container,
                        TerminalFragment(),
                        terminalFragmentTag
                    )
                    fragmentManagerForActivity.addFragment(
                        EditFragment(),
                        editFragmentTag
                    )
                }
            }
            if(!onInit) fragmentManagerForActivity.addToBackStack()
            fragmentManagerForActivity.commit()
        }

        fun changeFragmentAppDirAdmin(
            supportFragmentManager: FragmentManager,
            appDirAdminTag: String,
        ){
            val fragmentManagerForActivity = FragmentManagerForActivity(
                supportFragmentManager
            )
            fragmentManagerForActivity.replaceFragment(
                R.id.main_container,
                CommandIndexFragment(),
                appDirAdminTag
            )
            fragmentManagerForActivity.addToBackStack()
            fragmentManagerForActivity.commit()
        }
    }
}