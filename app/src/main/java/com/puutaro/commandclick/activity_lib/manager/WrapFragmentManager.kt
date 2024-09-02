package com.puutaro.commandclick.activity_lib.manager

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.curdForFragment.FragmentManagerForActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import java.lang.ref.WeakReference

object WrapFragmentManager {

    fun initFragment(
        savedInstanceState: Bundle?,
        supportFragmentManager: FragmentManager,
        terminalFragmentTag: String,
        commandIndexFragmentTag: String,
    ){
        if(
            savedInstanceState != null
        ) return
        val transaction = supportFragmentManager.beginTransaction()
        FragmentManagerForActivity.initFragments(
            transaction,
            terminalFragmentTag,
            commandIndexFragmentTag,
        )
        FragmentManagerForActivity.commit(transaction)
    }


    fun changeFragmentByKeyBoardVisibleChange(
        isKeyboardShowing: Boolean,
        supportFragmentManager: FragmentManager,
        terminalFragmentTag: String,
    ){
        val transaction = supportFragmentManager.beginTransaction()
        when (isKeyboardShowing) {
            true -> {
                FragmentManagerForActivity.hideFragment<TerminalFragment>(
                    supportFragmentManager,
                    transaction,
                    terminalFragmentTag
                )
                FragmentManagerForActivity.commit(transaction)
            }

            else -> {
                FragmentManagerForActivity.showFragment<TerminalFragment>(
                    supportFragmentManager,
                    transaction,
                    terminalFragmentTag
                )
                FragmentManagerForActivity.commit(transaction)
            }
        }
    }


    fun changeFragmentAtListItemClicked(
        supportFragmentManager: FragmentManager,
        terminalFragmentTag: String,
    ){

        val transaction = supportFragmentManager.beginTransaction()
        FragmentManagerForActivity.showFragment<TerminalFragment>(
            supportFragmentManager,
            transaction,
            terminalFragmentTag
        )
        FragmentManagerForActivity.commit(transaction)
    }



    fun changeFragmentAtCancelClickWhenConfirm(
        supportFragmentManager: FragmentManager,
        terminalFragmentTag: String,
        commandIndexFragmentTag: String,
    ){
        FragmentManagerForActivity.deleteAllBackStack(supportFragmentManager)
        val transaction = supportFragmentManager.beginTransaction()
        FragmentManagerForActivity.replaceFragment(
            transaction,
            R.id.main_container,
            TerminalFragment(),
            terminalFragmentTag
        )
        FragmentManagerForActivity.addFragment(
            transaction,
            CommandIndexFragment(),
            commandIndexFragmentTag
        )
        FragmentManagerForActivity.commit(transaction)
    }


    fun changeFragmentEdit(
        supportFragmentManager: FragmentManager,
        editFragmentTag: String,
        terminalFragmentTag: String,
        editFragmentArgs: EditFragmentArgs,
        disableAddToBackStack: Boolean = false
    ){

        val addEditFragment = editFragmentArgs.put(
            EditFragment(),
        )
        val terminalFragment = editFragmentArgs.put(
            TerminalFragment(),
        )
        val transaction = supportFragmentManager.beginTransaction()
        when(terminalFragmentTag){
            String() -> {
                FragmentManagerForActivity.replaceFragment(
                    transaction,
                    R.id.main_container,
                    addEditFragment,
                    editFragmentTag
                )
            }
            else -> {
                FragmentManagerForActivity.replaceFragment(
                    transaction,
                    R.id.main_container,
                    terminalFragment,
                    terminalFragmentTag
                )
                FragmentManagerForActivity.addFragment(
                    transaction,
                    addEditFragment,
                    editFragmentTag
                )
            }
        }
        if(
            !disableAddToBackStack
        ) FragmentManagerForActivity.addToBackStack(transaction)
        FragmentManagerForActivity.commit(transaction)
    }
}