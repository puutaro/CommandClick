package com.puutaro.commandclick.proccess.pin

import androidx.core.view.isVisible
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object PinFannelHideShow {

    private const val pinFannelHideFileName = "hidePinFannel.txt"
    private val pinFannelHideFilePath =
        File(UsePath.cmdclickFannelSystemDirPath, pinFannelHideFileName).absolutePath

    fun isHide(): Boolean {
        return File(pinFannelHideFilePath).isFile
    }

    fun setHideListener(
        terminalFragment: TerminalFragment
    ){
        val listener = terminalFragment.context as? TerminalFragment.OnPinFannelHideListener
        CoroutineScope(Dispatchers.Main).launch {
            if(
                isHide()
            ) listener?.onPinFannelHide()
        }
        terminalFragment.binding.termHidePinButton.setOnClickListener {
            listener?.onPinFannelHide()
        }
    }

    fun setShowListener(
        cmdIndexFragment: CommandIndexFragment
    ){
        val listener = cmdIndexFragment.context as? CommandIndexFragment.OnPinFannelShowListener
        cmdIndexFragment.binding.cmdindexShowClearToolbarButton.setOnClickListener {
            listener?.onPinFannelShow()
        }
    }

    fun execHideShow(
        activity: MainActivity,
        isHide: Boolean,
    ){
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdIndexFragment = targetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return

        val terminalFragment = targetFragmentInstance.getCurrentTerminalFragment(
            activity
        ) ?: return
        when(isHide){
            true -> FileSystems.writeFile(
                pinFannelHideFilePath,
                String()
            )
            else -> FileSystems.removeFiles(
                pinFannelHideFilePath
            )
        }
        terminalFragment.binding.termBottomLinear.isVisible = !isHide
        terminalFragment.binding.fannelPinRecyclerView.isVisible = !isHide
        cmdIndexFragment.binding.cmdindexToolbarLinearLayout.isVisible = isHide
    }
}