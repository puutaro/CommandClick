package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SpinnerUpdaterForTerminalFragment {
//    fun update(
//        activity: MainActivity,
//        spinnerId: Int?,
//        variableValue: String
//    ) {
//        if(spinnerId == null) return
//        val sharePref = FannelInfoTool.getSharePref(activity)
////        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
////            sharePref,
////            FannelInfoSetting.current_app_dir
////        )
//        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
//            sharePref,
//            FannelInfoSetting.current_fannel_name
//        )
//        val currentFannelState = FannelInfoTool.getStringFromFannelInfo(
//            sharePref,
//            FannelInfoSetting.current_fannel_state
//        )
//        val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
////            currentAppDirPath,
//            currentFannelName,
//            currentFannelState
//        )
//        val cmdEditFragment = TargetFragmentInstance.getFromActivity<EditFragment>(
//            activity,
//            cmdEditFragmentTag
//        ) ?: return
//        try {
//            val spinnerInEditFragment = EditComponent.findSpinnerView(
//                    spinnerId,
//                    EditComponent.makeEditLinearLayoutList(cmdEditFragment)
//                ) ?: return
//            val adapter = spinnerInEditFragment.adapter as ArrayAdapter<String>
//
//            val adapterSize = adapter.count
//            val adapterList =
//                (0 until adapterSize).map {
//                    adapter.getItem(it).toString()
//                }
//
//            val selectUpdatedSpinnerList = listOf(
//                variableValue,
//            ) + adapterList.filter { it != variableValue }
//            CoroutineScope(Dispatchers.Main).launch {
//                execSpinnerUpdate(
//                    spinnerInEditFragment,
//                    selectUpdatedSpinnerList,
//                    adapter
//                )
//            }
//        } catch(e: Exception){
//            Log.e("edit", e.toString())
//        }
//    }
//
//    private fun execSpinnerUpdate(
//        spinnerInEditFragment: Spinner,
//        selectUpdatedSpinnerList: List<String>,
//        adapter: ArrayAdapter<String>
//    ){
//        adapter.clear()
//        adapter.addAll(selectUpdatedSpinnerList)
//        adapter.notifyDataSetChanged()
//        spinnerInEditFragment.setSelection(0)
//    }
}