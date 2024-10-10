package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object EditListRecyclerViewGetter {

    fun get(
        terminalFragment: TerminalFragment?,
        srcFragStr: String
    ): RecyclerView? {
        val recyclerViewFrag = RecyclerViewFragment.values().firstOrNull {
            it.frag == srcFragStr
        }
        if(recyclerViewFrag == null){
            ToastUtils.showShort("not found srcFragStr: ${srcFragStr}")
            return null
        }
        val recyclerView = when(recyclerViewFrag){
            RecyclerViewFragment.WEB -> {
                getFromTermFrag(terminalFragment)
            }
            RecyclerViewFragment.EDIT -> {
                getFromEditFrag(terminalFragment)
            }
        }
        if(recyclerView == null){
            ToastUtils.showShort("not found recyclerView")
            return null
        }
        return recyclerView
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