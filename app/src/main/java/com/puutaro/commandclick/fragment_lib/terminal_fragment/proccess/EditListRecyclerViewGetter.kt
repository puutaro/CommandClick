package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object EditListRecyclerViewGetter {

    fun get(
        terminalFragment: TerminalFragment?,
        recyclerViewFragStr: String
    ): RecyclerView? {
        val recyclerViewFrag = RecyclerViewFragment.values().firstOrNull {
            it.frag == recyclerViewFragStr
        } ?: return null
        return when(recyclerViewFrag){
            RecyclerViewFragment.WEB -> {
                getFromTermFrag(terminalFragment)
            }
            RecyclerViewFragment.EDIT -> {
                getFromEditFrag(terminalFragment)
            }

        }

    }
    private fun getFromEditFrag(
        terminalFragment: TerminalFragment?
    ): RecyclerView? {
        if(
            terminalFragment == null
        ) return null
        val activity = terminalFragment.activity
        val cmdEditFragmentTag =
            TargetFragmentInstance.getCmdEditFragmentTag(activity)
        val editFragment = TargetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdEditFragmentTag,
        ).let {
            bottomFragment ->
            if (
                bottomFragment !is EditFragment
            ) return null
            bottomFragment
        }
        return editFragment.binding.editListRecyclerView

    }

    private fun getFromTermFrag(
        terminalFragment: TerminalFragment?
    ): RecyclerView? {
    if(
        terminalFragment == null
    ) return null
    return terminalFragment.editListDialog?.findViewById<RecyclerView>(
            R.id.edit_list_dialog_recycler_view
        )
    }

    enum class RecyclerViewFragment(
        val frag: String
    ){
        WEB("web"),
        EDIT("edit")

    }


}