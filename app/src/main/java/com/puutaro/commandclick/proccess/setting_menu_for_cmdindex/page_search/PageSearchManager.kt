package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex.page_search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PageSearchManager {

    fun switch(
        activity: MainActivity,
    ){
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val pageSearch = cmdIndexFragment.binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        ) ?: return
        setButtonImage(
            pageSearch.cmdindexSearchDownAllowImage,
            CmdClickIcons.DOWN
        )
        setButtonImage(
            pageSearch.cmdindexSearchTopAllowImage,
            CmdClickIcons.TOP
        )
        setButtonImage(
            pageSearch.cmdindexSearchCancelImage,
            CmdClickIcons.CANCEL
        )
        val terminalWebView = terminalFragment.binding.terminalWebView
        terminalWebView.requestFocus()
        cmdclickPageSearchToolBar.isVisible = true
//        cmdPageSearchEditText.requestFocus()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(100)
            }
            withContext(Dispatchers.Main) {
                Keyboard.showKeyboard(
                    activity,
                    terminalWebView,
                )
            }
        }
//        terminalFragment.binding.terminalWebView.requestFocus()
//        Keyboard.showKeyboard(
//            activity,
//            terminalFragment.binding.terminalWebView,
//        )
    }

    private fun setButtonImage(
        imageView: AppCompatImageView,
        icon: CmdClickIcons,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            ExecSetToolbarButtonImage.setImageButton(
                imageView,
                icon
            )
        }
    }

    fun set(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val binding = cmdIndexFragment.binding
        val pageSearch = binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar
        val cmdindexSearchTotal = pageSearch.cmdindexSearchTotal
        val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
        val cmdindexSearchCancel = pageSearch.cmdindexSearchCancel
        val context = cmdIndexFragment.context
        cancleButtonClickListner(
            cmdIndexFragment,
            cmdindexSearchCancel,
            cmdclickPageSearchToolBar,
            cmdPageSearchEditText,
        )
        pageSearchTextChangeListner(
            context,
            cmdPageSearchEditText,
            cmdindexSearchTotal
        )
        onKeyListner(
            cmdIndexFragment,
            cmdPageSearchEditText
        )
        searchTopClickLisnter(
            cmdIndexFragment,
            pageSearch.cmdindexSearchTopAllow
        )
        searchDownClickLisnter(
            context,
            pageSearch.cmdindexSearchDownAllow
        )
    }

    private fun cancleButtonClickListner(
        cmdIndexFragment: CommandIndexFragment,
        cmdindexSearchCancel: FrameLayout,
        cmdclickPageSearchToolBar: LinearLayoutCompat,
        cmdPageSearchEditText: AppCompatEditText,
    ){
        cmdindexSearchCancel.setOnClickListener {
                cancelView ->
            cmdclickPageSearchToolBar.isVisible = false
            cmdPageSearchEditText.setText(String())
            cmdPageSearchEditText.clearFocus()
            Keyboard.hiddenKeyboardForFragment(cmdIndexFragment)
        }
    }

    private fun pageSearchTextChangeListner(
        context: Context?,
        cmdPageSearchEditText: AppCompatEditText,
        cmdindexSearchTotal: AppCompatTextView
    ){
        val listener = context as? CommandIndexFragment.OnPageSearchToolbarClickListener
        val indexTerminalTag = context?.getString(R.string.index_terminal_fragment)
        val linearWeightParamWide = LinearLayoutCompat.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearWeightParamWide.weight = 0.67F
        val linearWeightParamShrink = LinearLayoutCompat.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearWeightParamShrink.weight = 0.56F
        cmdPageSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(s.isNullOrEmpty()) {
                    cmdindexSearchTotal.visibility = View.GONE
                    cmdPageSearchEditText.layoutParams = linearWeightParamWide
                } else {
                    cmdindexSearchTotal.visibility = View.VISIBLE
                    cmdPageSearchEditText.layoutParams = linearWeightParamShrink
                }
                listener?.onPageSearchToolbarClick(
                    PageSearchToolbarButtonVariant.SEARCH_TEXT,
                    indexTerminalTag,
                    s.toString()
                )
            }
        })
    }

    private fun onKeyListner(
        cmdIndexFragment: CommandIndexFragment,
        cmdPageSearchEditText: AppCompatEditText
    ){
        val context = cmdIndexFragment.context
        val listener = context as? CommandIndexFragment.OnPageSearchToolbarClickListener
        val indexTerminalTag = context?.getString(R.string.index_terminal_fragment)
        cmdPageSearchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() != KeyEvent.ACTION_DOWN ||
                    keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                listener?.onPageSearchToolbarClick(
                    PageSearchToolbarButtonVariant.DOWN,
                    indexTerminalTag
                )
                return false
            }
        })
    }

    private fun searchTopClickLisnter(
        cmdIndexFragment: CommandIndexFragment,
        cmdindexSearchTopAllow: FrameLayout
    ){
        val context = cmdIndexFragment.context
        val listener = context as? CommandIndexFragment.OnPageSearchToolbarClickListener
        val indexTerminalTag = context?.getString(R.string.index_terminal_fragment)
        cmdindexSearchTopAllow.setOnClickListener {
                TopAllowButtonView ->
//            if(!cmdindexFragment.isVisible) return@setOnClickListener
            listener?.onPageSearchToolbarClick(
                PageSearchToolbarButtonVariant.TOP,
                indexTerminalTag

            )
            return@setOnClickListener
        }
    }

    fun searchDownClickLisnter(
        context: Context?,
        cmdindexSearchDownAllow: FrameLayout
    ){
        val indexTerminalTag = context?.getString(R.string.index_terminal_fragment)
        val listener = context as? CommandIndexFragment.OnPageSearchToolbarClickListener
        cmdindexSearchDownAllow.setOnClickListener {
                DownAllowButtonView ->
//            if(!cmdindexFragment.isVisible) return@setOnClickListener
            listener?.onPageSearchToolbarClick(
                PageSearchToolbarButtonVariant.DOWN,
                indexTerminalTag
            )
        }
    }
}