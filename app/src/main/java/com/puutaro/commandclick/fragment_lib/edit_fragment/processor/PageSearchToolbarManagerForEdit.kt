package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditLongPressType
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditToolbarSwitcher

class PageSearchToolbarManagerForEdit(
    private val cmdEditFragment: EditFragment,
) {

    private val context = cmdEditFragment.context
    private val binding = cmdEditFragment.binding
    private val pageSearch = binding.pageSearch
    private val cmdindexSearchCancel = pageSearch.cmdindexSearchCancel
    private val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
    private val cmdindexSearchTopAllow = pageSearch.cmdindexSearchTopAllow
    private val cmdindexSearchDownAllow = pageSearch.cmdindexSearchDownAllow
    private val listener = context as? EditFragment.OnLongPressPlayOrEditButtonListener
    private val pageSearchLongPressType = EditLongPressType.PAGE_SEARCH
    private val editExecuteTerminalTag = context?.getString(R.string.edit_terminal_fragment)

    fun cancleButtonClickListener(){
        cmdindexSearchCancel.setOnClickListener {
                cancelView ->
            EditToolbarSwitcher.switch(
                cmdEditFragment,
                EditLongPressType.NORMAL.jsMacro
            )
        }
    }

    fun pageSearchTextChangeListner(){
        try {
            val linearWeightParamWide = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            linearWeightParamWide.weight = 0.67F
            val linearWeightParamShrink = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            linearWeightParamShrink.weight = 0.56F
            val searchText = PageSearchToolbarButtonVariant.SEARCH_TEXT
            cmdPageSearchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        pageSearch.cmdindexSearchTotal.visibility = View.GONE
                        pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamWide
                    } else {
                        pageSearch.cmdindexSearchTotal.visibility = View.VISIBLE
                        pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamShrink
                    }
                    try {
                        listener?.onLongPressPlayOrEditButton(
                            pageSearchLongPressType,
                            editExecuteTerminalTag,
                            s.toString(),
                            searchText,
                        )
                    }catch(e: Exception){
                        ToastUtils.showLong(e.toString())
                    }
                }
            })
        } catch(e: Exception){
            ToastUtils.showLong(e.toString())
        }
    }

    fun onKeyListner(){
        cmdPageSearchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() != KeyEvent.ACTION_DOWN ||
                    keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                listener?.onLongPressPlayOrEditButton(
                    pageSearchLongPressType,
                    editExecuteTerminalTag,
                    "",
                    PageSearchToolbarButtonVariant.DOWN,
                )
                return false
            }
        })
    }

    fun searchTopClickLisnter(){
        cmdindexSearchTopAllow.setOnClickListener {
                TopAllowButtonView ->
            if(!cmdEditFragment.isVisible) return@setOnClickListener
            listener?.onLongPressPlayOrEditButton(
                pageSearchLongPressType,
                editExecuteTerminalTag,
                "",
                PageSearchToolbarButtonVariant.TOP,
            )
            return@setOnClickListener
        }
    }

    fun searchDownClickLisnter(){
        cmdindexSearchDownAllow.setOnClickListener {
                DownAllowButtonView ->
            if(!cmdEditFragment.isVisible) return@setOnClickListener
            listener?.onLongPressPlayOrEditButton(
                pageSearchLongPressType,
                editExecuteTerminalTag,
                "",
                PageSearchToolbarButtonVariant.DOWN,
            )
        }
    }
}

