package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.proccess.setting_menu_for_cmdindex.ExtraMenuForCmdIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TerminalToolbarHandler {
    fun handler(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
            ?: return
        val binding = terminalFragment.binding
        if(
            terminalFragment.tag == context.getString(R.string.edit_terminal_fragment)
        ){
            binding.termBottomLinear.isVisible = false
            return
        }
        setCaptionTextView(
            binding.termHistoryButtonCaption
        )
        setCaptionTextView(
            binding.termUrlHistoryButtonCaption
        )
        setCaptionTextView(
            binding.termSearchButtonCaption
        )
        setCaptionTextView(
            binding.termHidePinButtonCaption
        )
        setCaptionTextView(
            binding.termSettingButtonCaption
        )
        setFannelManagerClickAction(
            terminalFragment
        )
        setUrlHistoryClickAction(terminalFragment)
        setSettingButtonClickAction(terminalFragment)
        CoroutineScope(Dispatchers.Main).launch {
            val toolbarListener = context as TerminalFragment.OnSetToolbarButtonImageListener
            toolbarListener.onSetToolbarButtonImage()
        }

    }

    private fun setCaptionTextView(
        captionTextView: OutlineTextView
    ){
        captionTextView.setStrokeColor(R.color.white)
        captionTextView.setFillColor(R.color.file_dark_green_color)
    }

    private fun setFannelManagerClickAction(
        terminalFragment: TerminalFragment
    ){
        terminalFragment.binding.termHistoryButton.setOnClickListener {
            FannelHistoryButtonEvent.invoke(terminalFragment)
        }
    }

    private fun setUrlHistoryClickAction(
        terminalFragment: TerminalFragment
    ){
        terminalFragment.binding.termUrlHistoryButton.setOnClickListener {
            UrlHistoryButtonEvent.invoke(terminalFragment)
        }
        ExtraMenuForCmdIndex.launch(terminalFragment)
    }

    private fun setSettingButtonClickAction(
        terminalFragment: TerminalFragment
    ){
        ExtraMenuForCmdIndex.launch(terminalFragment)
    }
}