package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditToolbarSwitcher

object PageSearchToolbarManagerForEdit{
    fun cancleButtonClickListener(
        cmdEditFragment: EditFragment
    ){
        val context = cmdEditFragment.context
        val binding = cmdEditFragment.binding
//        val pageSearch = binding.pageSearch
//        val cmdindexSearchCancel = pageSearch.cmdindexSearchCancel

//        cmdindexSearchCancel.setOnClickListener {
//                cancelView ->
//            EditToolbarSwitcher.switch(
//                cmdEditFragment,
//                EditLongPressType.NORMAL.jsMacro
//            )
//        }
    }

    fun pageSearchTextChangeListner(
        cmdEditFragment: EditFragment
    ){
//        val context = cmdEditFragment.context
//        val binding = cmdEditFragment.binding
//        val pageSearch = binding.pageSearch
//        val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
//        val listener = context as? EditFragment.OnLongPressPlayOrEditButtonListener
//        val pageSearchLongPressType = EditLongPressType.PAGE_SEARCH
//        val editExecuteTerminalTag = context?.getString(R.string.edit_terminal_fragment)
//        try {
//            val linearWeightParamWide = LinearLayoutCompat.LayoutParams(
//                0,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//            )
//            linearWeightParamWide.weight = 0.67F
//            val linearWeightParamShrink = LinearLayoutCompat.LayoutParams(
//                0,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//            )
//            linearWeightParamShrink.weight = 0.56F
//            val searchText = PageSearchToolbarButtonVariant.SEARCH_TEXT
//            cmdPageSearchEditText.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//                override fun afterTextChanged(s: Editable?) {
//                    if (s.isNullOrEmpty()) {
//                        pageSearch.cmdindexSearchTotal.visibility = View.GONE
//                        pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamWide
//                    } else {
//                        pageSearch.cmdindexSearchTotal.visibility = View.VISIBLE
//                        pageSearch.cmdPageSearchEditText.layoutParams = linearWeightParamShrink
//                    }
//                    try {
//                        listener?.onLongPressPlayOrEditButton(
//                            pageSearchLongPressType,
//                            editExecuteTerminalTag,
//                            s.toString(),
//                            searchText,
//                        )
//                    }catch(e: Exception){
//                        ToastUtils.showLong(e.toString())
//                    }
//                }
//            })
//        } catch(e: Exception){
//            ToastUtils.showLong(e.toString())
//        }
    }

    fun onKeyListner(
        cmdEditFragment: EditFragment
    ){
//        val context = cmdEditFragment.context
//        val binding = cmdEditFragment.binding
//        val pageSearch = binding.pageSearch
//        val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
//        val listener = context as? EditFragment.OnLongPressPlayOrEditButtonListener
//        val pageSearchLongPressType = EditLongPressType.PAGE_SEARCH
//        val editExecuteTerminalTag = context?.getString(R.string.edit_terminal_fragment)
//        cmdPageSearchEditText.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
//                if (event.getAction() != KeyEvent.ACTION_DOWN ||
//                    keyCode != KeyEvent.KEYCODE_ENTER
//                ) return false
//                listener?.onLongPressPlayOrEditButton(
//                    pageSearchLongPressType,
//                    editExecuteTerminalTag,
//                    "",
//                    PageSearchToolbarButtonVariant.DOWN,
//                )
//                return false
//            }
//        })
    }

    fun searchTopClickLisnter(
        cmdEditFragment: EditFragment
    ){
//        val context = cmdEditFragment.context
//        val binding = cmdEditFragment.binding
//        val pageSearch = binding.pageSearch
//        val cmdindexSearchTopAllow = pageSearch.cmdindexSearchTopAllow
//        val listener = context as? EditFragment.OnLongPressPlayOrEditButtonListener
//        val pageSearchLongPressType = EditLongPressType.PAGE_SEARCH
//        val editExecuteTerminalTag = context?.getString(R.string.edit_terminal_fragment)
//        cmdindexSearchTopAllow.setOnClickListener {
//                TopAllowButtonView ->
//            if(!cmdEditFragment.isVisible) return@setOnClickListener
//            listener?.onLongPressPlayOrEditButton(
//                pageSearchLongPressType,
//                editExecuteTerminalTag,
//                "",
//                PageSearchToolbarButtonVariant.TOP,
//            )
//            return@setOnClickListener
//        }
    }

    fun searchDownClickLisnter(
        cmdEditFragment: EditFragment
    ){
//        val context = cmdEditFragment.context
//        val binding = cmdEditFragment.binding
//        val pageSearch = binding.pageSearch
//        val cmdindexSearchDownAllow = pageSearch.cmdindexSearchDownAllow
//        val listener = context as? EditFragment.OnLongPressPlayOrEditButtonListener
//        val pageSearchLongPressType = EditLongPressType.PAGE_SEARCH
//        val editExecuteTerminalTag = context?.getString(R.string.edit_terminal_fragment)
//        cmdindexSearchDownAllow.setOnClickListener {
//                DownAllowButtonView ->
//            if(!cmdEditFragment.isVisible) return@setOnClickListener
//            listener?.onLongPressPlayOrEditButton(
//                pageSearchLongPressType,
//                editExecuteTerminalTag,
//                "",
//                PageSearchToolbarButtonVariant.DOWN,
//            )
//        }
    }
}

