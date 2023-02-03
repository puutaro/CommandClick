package com.puutaro.commandclick.activity_lib.manager.curdForFragment;

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment


class FragmentManagerForActivity(
    private val supportFragmentManager: FragmentManager,
) {
    private val transaction = supportFragmentManager.beginTransaction()

    fun commit(){
        transaction.commit()
    }

    fun deleteAllBackStack(){
        for (i in 0..supportFragmentManager.getBackStackEntryCount()) {
            supportFragmentManager.popBackStack()
        }
    }


    fun addToBackStack(){
        transaction.addToBackStack(null)
    }


    fun initFragments(
        terminalFragment: String,
        commandIndexFragment: String
    ) {
        val sampleFragmentBottom = CommandIndexFragment()
        val sampleFragmentTop = TerminalFragment()
        transaction.replace(
            R.id.main_container,
            sampleFragmentTop,
            terminalFragment
        )
        transaction.add(
            R.id.main_container,
            sampleFragmentBottom,
            commandIndexFragment
        )
    }


    fun <T: Fragment> showFragment(
        terminalFragmentTag: String
    ) {
        val fragmentTop = try {
            supportFragmentManager.findFragmentByTag(terminalFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${terminalFragmentTag}")
            return
        }
        transaction.show(fragmentTop)
    }


    fun <T: Fragment> hideFragment (
        terminalFragmentTag: String
    ) {
        val sampleFragmentTop = try {
            supportFragmentManager.findFragmentByTag(terminalFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${terminalFragmentTag}")
            return
        }
        transaction.hide(sampleFragmentTop)
    }

    fun <T: Fragment> removeFragment(
        cmdIndexFragmentTag: String,
    ) {
        val cmdIndexFragment = try {
            supportFragmentManager.findFragmentByTag(cmdIndexFragmentTag) as T
        } catch (e: java.lang.Exception) {
            Log.d(this.toString(), "not exist ${cmdIndexFragmentTag}")
            return
        }
        transaction.remove(cmdIndexFragment)
    }

    fun replaceFragment(
        fragmentId: Int,
        replaceFragment: Fragment,
        replaceFragmentTag: String,
    ) {
        transaction.replace(
            fragmentId,
            replaceFragment,
            replaceFragmentTag
        )
    }

    fun addFragment(
        addFragment: Fragment,
        addFragmentTag: String,
    ) {
        transaction.add(
            R.id.main_container,
            addFragment,
            addFragmentTag
        )
    }
}