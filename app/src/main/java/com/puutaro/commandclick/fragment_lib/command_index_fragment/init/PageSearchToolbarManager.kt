package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher

class PageSearchToolbarManager(
    private val cmdindexFragment: CommandIndexFragment,
) {

    private val context = cmdindexFragment.context
    private val binding = cmdindexFragment.binding
    private val pageSearch = binding.pageSearch
    private val cmdindexSearchCancel = pageSearch.cmdindexSearchCancel
    private val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
    private val cmdindexSearchTopAllow = pageSearch.cmdindexSearchTopAllow
    private val cmdindexSearchDownAllow = pageSearch.cmdindexSearchDownAllow
    private val listener = context as? CommandIndexFragment.OnPageSearchToolbarClickListener
    private val indexTerminalTag = context?.getString(R.string.index_terminal_fragment)
    fun cancleButtonClickListner(){
        cmdindexSearchCancel.setOnClickListener {
                cancelView ->
            CmdIndexToolbarSwitcher.switch(
                cmdindexFragment,
                false
            )
        }
    }

    fun pageSearchTextChangeListner(){
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
                    pageSearch.cmdindexSearchTotal.visibility = View.GONE
                    pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamWide
                } else {
                    pageSearch.cmdindexSearchTotal.visibility = View.VISIBLE
                    pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamShrink
                }
                listener?.onPageSearchToolbarClick(
                    PageSearchToolbarButtonVariant.SEARCH_TEXT,
                    indexTerminalTag,
                    s.toString()
                )
            }
        })
    }

    fun onKeyListner(){
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

    fun searchTopClickLisnter(){
        cmdindexSearchTopAllow.setOnClickListener {
                TopAllowButtonView ->
            if(!cmdindexFragment.isVisible) return@setOnClickListener
            listener?.onPageSearchToolbarClick(
                PageSearchToolbarButtonVariant.TOP,
                indexTerminalTag

            )
            return@setOnClickListener
        }
    }

    fun searchDownClickLisnter(){
        cmdindexSearchDownAllow.setOnClickListener {
                DownAllowButtonView ->
            if(!cmdindexFragment.isVisible) return@setOnClickListener
            listener?.onPageSearchToolbarClick(
                PageSearchToolbarButtonVariant.DOWN,
                indexTerminalTag
            )
        }
    }
}

