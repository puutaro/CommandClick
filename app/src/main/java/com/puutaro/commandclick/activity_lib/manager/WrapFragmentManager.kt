package com.puutaro.commandclick.activity_lib.manager

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.manager.curdForFragment.FragmentManagerForActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs

object WrapFragmentManager {

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
        when (isKeyboardShowing) {
            true -> {
                fragmentManagerForActivity.hideFragment<TerminalFragment>(
                    terminalFragmentTag
                )
                fragmentManagerForActivity.commit()
            }

            else -> {
                fragmentManagerForActivity.showFragment<TerminalFragment>(
                    terminalFragmentTag
                )
                fragmentManagerForActivity.commit()
            }
        }
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
        editFragmentArgs: EditFragmentArgs,
        onInit: Boolean = false
    ){
        val fragmentManagerForActivity = FragmentManagerForActivity(
            supportFragmentManager
        )

        val addEditFragment = editFragmentArgs.put(
            EditFragment(),
        )
        val terminalFragment = editFragmentArgs.put(
            TerminalFragment(),
        )

        when(terminalFragmentTag){
            String() -> {
                fragmentManagerForActivity.replaceFragment(
                    R.id.main_container,
                    addEditFragment,
                    editFragmentTag
                )
            }
            else -> {
                fragmentManagerForActivity.replaceFragment(
                    R.id.main_container,
                    terminalFragment,
                    terminalFragmentTag
                )
                fragmentManagerForActivity.addFragment(
                    addEditFragment,
                    editFragmentTag
                )
            }
        }
        if(!onInit) fragmentManagerForActivity.addToBackStack()
        fragmentManagerForActivity.commit()
    }
}