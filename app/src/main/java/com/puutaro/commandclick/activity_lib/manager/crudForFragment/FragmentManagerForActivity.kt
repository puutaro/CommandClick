package com.puutaro.commandclick.activity_lib.manager.curdForFragment

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment


object FragmentManagerForActivity{
//    private val transaction = supportFragmentManager.beginTransaction()

    fun commit(
        transaction: FragmentTransaction
    ){
        transaction.commit()
    }

    fun deleteAllBackStack(
        supportFragmentManager: FragmentManager?,
    ){
        if(supportFragmentManager == null) return
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }


    fun addToBackStack(
        transaction: FragmentTransaction
    ){
        transaction.addToBackStack(null)
    }


    fun initFragments(
        transaction: FragmentTransaction,
        terminalFragment: String,
        commandIndexFragment: String
    ) {
        val commandIndexFragmentBottom = CommandIndexFragment()
        val sampleTerminalFragmentTop = TerminalFragment()
        transaction.replace(
            R.id.main_container,
            sampleTerminalFragmentTop,
            terminalFragment
        )
        transaction.add(
            R.id.main_container,
            commandIndexFragmentBottom,
            commandIndexFragment
        )
    }


    fun <T: androidx.fragment.app.Fragment> showFragment(
        supportFragmentManager: FragmentManager?,
        transaction: FragmentTransaction,
        terminalFragmentTag: String
    ) {
        if(supportFragmentManager == null) return
        val fragmentTop = try {
            supportFragmentManager.findFragmentByTag(terminalFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${terminalFragmentTag}")
            return
        }
        transaction.show(fragmentTop)
    }


    fun <T: androidx.fragment.app.Fragment> hideFragment (
        supportFragmentManager: FragmentManager?,
        transaction: FragmentTransaction,
        terminalFragmentTag: String
    ) {
        if(supportFragmentManager == null) return
        val sampleFragmentTop = try {
            supportFragmentManager.findFragmentByTag(terminalFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${terminalFragmentTag}")
            return
        }
        transaction.hide(sampleFragmentTop)
    }

    fun <T: androidx.fragment.app.Fragment> removeFragment(
        supportFragmentManager: FragmentManager?,
        transaction: FragmentTransaction,
        cmdIndexFragmentTag: String,
    ) {
        if(
            supportFragmentManager == null
        ) return
        val cmdIndexFragment = try {
            supportFragmentManager.findFragmentByTag(cmdIndexFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${cmdIndexFragmentTag}")
            return
        }
        transaction.remove(cmdIndexFragment)
    }

    fun replaceFragment(
        transaction: FragmentTransaction,
        fragmentId: Int,
        replaceFragment: androidx.fragment.app.Fragment,
        replaceFragmentTag: String,
    ) {
        transaction.replace(
            fragmentId,
            replaceFragment,
            replaceFragmentTag
        )
    }

    fun addFragment(
        transaction: FragmentTransaction,
        addFragment: androidx.fragment.app.Fragment,
        addFragmentTag: String,
    ) {
        transaction.add(
            R.id.main_container,
            addFragment,
            addFragmentTag
        )
    }
}